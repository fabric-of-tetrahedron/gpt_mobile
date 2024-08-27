package dev.chungjungsoo.gptmobile.data.repository

import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIHost
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.tddworks.common.network.api.ktor.api.AnySerial
import com.tddworks.ollama.api.Ollama
import com.tddworks.ollama.api.OllamaConfig
import com.tddworks.ollama.api.chat.OllamaChatMessage
import com.tddworks.ollama.api.chat.OllamaChatRequest
import com.tddworks.ollama.api.internal.OllamaApi
import com.tddworks.ollama.di.initOllama
import dev.chungjungsoo.gptmobile.data.ModelConstants
import dev.chungjungsoo.gptmobile.data.database.dao.ChatRoomDao
import dev.chungjungsoo.gptmobile.data.database.dao.MessageDao
import dev.chungjungsoo.gptmobile.data.database.entity.ChatRoom
import dev.chungjungsoo.gptmobile.data.database.entity.Message
import dev.chungjungsoo.gptmobile.data.dto.ApiState
import dev.chungjungsoo.gptmobile.data.dto.anthropic.common.MessageRole
import dev.chungjungsoo.gptmobile.data.dto.anthropic.common.TextContent
import dev.chungjungsoo.gptmobile.data.dto.anthropic.request.InputMessage
import dev.chungjungsoo.gptmobile.data.dto.anthropic.request.MessageRequest
import dev.chungjungsoo.gptmobile.data.dto.anthropic.response.ContentDeltaResponseChunk
import dev.chungjungsoo.gptmobile.data.dto.anthropic.response.ErrorResponseChunk
import dev.chungjungsoo.gptmobile.data.dto.anthropic.response.MessageResponseChunk
import dev.chungjungsoo.gptmobile.data.model.ApiType
import dev.chungjungsoo.gptmobile.data.network.AnthropicAPI
import io.ktor.http.URLProtocol
import java.security.cert.X509Certificate
import javax.inject.Inject
import javax.net.ssl.X509TrustManager
import kotlinx.coroutines.flow.*

class ChatRepositoryImpl @Inject constructor(
    private val chatRoomDao: ChatRoomDao,
    private val messageDao: MessageDao,
    private val settingRepository: SettingRepository,
    private val anthropic: AnthropicAPI
) : ChatRepository {

    private lateinit var openAI: OpenAI
    private lateinit var google: GenerativeModel
    private lateinit var ollamaApi: OllamaApi

    override suspend fun completeOpenAIChat(question: Message, history: List<Message>): Flow<ApiState> {
        val platform = checkNotNull(settingRepository.fetchPlatforms().firstOrNull { it.name == ApiType.OPENAI })
        openAI = OpenAI(platform.token ?: "", host = OpenAIHost(baseUrl = platform.apiUrl))

        val generatedMessages = messageToOpenAIMessage(history + listOf(question))
        val generatedMessageWithPrompt = listOf(
            ChatMessage(role = ChatRole.System, content = platform.systemPrompt ?: ModelConstants.OPENAI_PROMPT)
        ) + generatedMessages
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId(platform.model ?: ""),
            messages = generatedMessageWithPrompt,
            temperature = platform.temperature?.toDouble(),
            topP = platform.topP?.toDouble()
        )

        return openAI.chatCompletions(chatCompletionRequest)
            .map<ChatCompletionChunk, ApiState> { chunk -> ApiState.Success(chunk.choices[0].delta.content ?: "") }
            .catch { throwable -> emit(ApiState.Error(throwable.message ?: "Unknown error")) }
            .onStart { emit(ApiState.Loading) }
            .onCompletion { emit(ApiState.Done) }
    }

    override suspend fun completeAnthropicChat(question: Message, history: List<Message>): Flow<ApiState> {
        val platform = checkNotNull(settingRepository.fetchPlatforms().firstOrNull { it.name == ApiType.ANTHROPIC })
        anthropic.setToken(platform.token)
        anthropic.setAPIUrl(platform.apiUrl)

        val generatedMessages = messageToAnthropicMessage(history + listOf(question))
        val messageRequest = MessageRequest(
            model = platform.model ?: "",
            messages = generatedMessages,
            maxTokens = ModelConstants.ANTHROPIC_MAXIMUM_TOKEN,
            systemPrompt = platform.systemPrompt ?: ModelConstants.DEFAULT_PROMPT,
            stream = true,
            temperature = platform.temperature,
            topP = platform.topP
        )

        return anthropic.streamChatMessage(messageRequest)
            .map<MessageResponseChunk, ApiState> { chunk ->
                when (chunk) {
                    is ContentDeltaResponseChunk -> ApiState.Success(chunk.delta.text)
                    is ErrorResponseChunk -> throw Error(chunk.error.message)
                    else -> ApiState.Success("")
                }
            }
            .catch { throwable -> emit(ApiState.Error(throwable.message ?: "Unknown error")) }
            .onStart { emit(ApiState.Loading) }
            .onCompletion { emit(ApiState.Done) }
    }

    override suspend fun completeGoogleChat(question: Message, history: List<Message>): Flow<ApiState> {
        val platform = checkNotNull(settingRepository.fetchPlatforms().firstOrNull { it.name == ApiType.GOOGLE })
        val config = generationConfig {
            temperature = platform.temperature
            topP = platform.topP
        }
        google = GenerativeModel(
            modelName = platform.model ?: "",
            apiKey = platform.token ?: "",
            systemInstruction = content { text(platform.systemPrompt ?: ModelConstants.DEFAULT_PROMPT) },
            generationConfig = config,
            safetySettings = listOf(
                SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.ONLY_HIGH),
                SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.NONE)
            )
        )

        val inputContent = messageToGoogleMessage(history)
        val chat = google.startChat(history = inputContent)

        return chat.sendMessageStream(question.content)
            .map<GenerateContentResponse, ApiState> { response -> ApiState.Success(response.text ?: "") }
            .catch { throwable -> emit(ApiState.Error(throwable.message ?: "Unknown error")) }
            .onStart { emit(ApiState.Loading) }
            .onCompletion { emit(ApiState.Done) }
    }

//    override suspend fun completeOllamaChat(question: Message, history: List<Message>): Flow<ApiState> {
//        val platform = checkNotNull(settingRepository.fetchPlatforms().firstOrNull { it.name == ApiType.OLLAMA })
//        openAI = OpenAI(platform.token ?: "", host = OpenAIHost(baseUrl = platform.apiUrl))
//
//        val generatedMessages = messageToOpenAIMessage(history + listOf(question))
//        val generatedMessageWithPrompt = listOf(
//            ChatMessage(role = ChatRole.System, content = platform.systemPrompt ?: ModelConstants.OPENAI_PROMPT)
//        ) + generatedMessages
//        val chatCompletionRequest = ChatCompletionRequest(
//            model = ModelId(platform.model ?: ""),
//            messages = generatedMessageWithPrompt,
//            temperature = platform.temperature?.toDouble(),
//            topP = platform.topP?.toDouble()
//        )
//
//        return openAI.chatCompletions(chatCompletionRequest)
//            .map<ChatCompletionChunk, ApiState> { chunk -> ApiState.Success(chunk.choices[0].delta.content ?: "") }
//            .catch { throwable -> emit(ApiState.Error(throwable.message ?: "Unknown error")) }
//            .onStart { emit(ApiState.Loading) }
//            .onCompletion { emit(ApiState.Done) }
//    }

    override suspend fun completeOllamaChat(question: Message, history: List<Message>): Flow<ApiState> {
        println("开始执行 completeOllamaChat 函数")
        val platform = checkNotNull(settingRepository.fetchPlatforms().firstOrNull { it.name == ApiType.OLLAMA })
        println("获取到 Ollama 平台信息: $platform")

        if (!::ollamaApi.isInitialized) {
            initOllama(
                OllamaConfig(
                    baseUrl = { platform.apiUrl.split(":")[1].removePrefix("//") },
                    protocol = { platform.apiUrl.split(":")[0] },
                    port = { platform.apiUrl.split(":")[2].toInt() }
                )
            )
        }

        val urlParts = platform.apiUrl.split(":")
        val protocol = urlParts[0]
        val baseUrl = urlParts[1].removePrefix("//")
        val port = urlParts[2].toInt()

        ollamaApi = OllamaApi(
            baseUrl = baseUrl,
            port = port,
            protocol = protocol
        )

        println("初始化 Ollama 客户端，API URL: ${platform.apiUrl}")

        val generatedMessages = messageToOllamaMessage(history + listOf(question))
        println("生成的消息历史: $generatedMessages")
        val generatedMessageWithPrompt = listOf(
            OllamaChatMessage(role = ChatRole.System.role, content = platform.systemPrompt ?: ModelConstants.OPENAI_PROMPT)
        ) + generatedMessages
        println("添加系统提示后的消息: $generatedMessageWithPrompt")

        val options = mutableMapOf<String, AnySerial>()
        platform.temperature?.let { options["temperature"] = it }
        platform.topP?.let { options["topP"] = it }

        val chatCompletionRequest = OllamaChatRequest(
            model = platform.model!!,
            messages = generatedMessageWithPrompt,
            options = options,
            stream = false
        )
        println("创建聊天完成请求: $chatCompletionRequest")

        return flow {
            val response = ollamaApi.request(chatCompletionRequest)
            println("收到聊天完成响应: $response")
            response.message?.let { message ->
                emit(ApiState.Success(message.content ?: ""))
            } ?: emit(ApiState.Error("No message content"))
        }
            .catch { throwable ->
                println("捕获到错误: ${throwable.message}")
                throwable.printStackTrace()
                emit(ApiState.Error(throwable.message ?: "Unknown error"))
            }
            .onStart {
                println("开始流式传输")
                emit(ApiState.Loading)
            }
            .onCompletion {
                println("完成流式传输")
                emit(ApiState.Done)
            }
    }

    override suspend fun fetchChatList(): List<ChatRoom> = chatRoomDao.getChatRooms()

    override suspend fun fetchMessages(chatId: Int): List<Message> = messageDao.loadMessages(chatId)

    override suspend fun updateChatTitle(chatRoom: ChatRoom, title: String) {
        chatRoomDao.editChatRoom(chatRoom.copy(title = title.take(50)))
    }

    override suspend fun saveChat(chatRoom: ChatRoom, messages: List<Message>): ChatRoom {
        if (chatRoom.id == 0) {
            // New Chat
            val chatId = chatRoomDao.addChatRoom(chatRoom)
            val updatedMessages = messages.map { it.copy(chatId = chatId.toInt()) }
            messageDao.addMessages(*updatedMessages.toTypedArray())

            val savedChatRoom = chatRoom.copy(id = chatId.toInt())
            updateChatTitle(savedChatRoom, updatedMessages[0].content)

            return savedChatRoom.copy(title = updatedMessages[0].content.take(50))
        }

        val savedMessages = fetchMessages(chatRoom.id)
        val updatedMessages = messages.map { it.copy(chatId = chatRoom.id) }

        val shouldBeDeleted = savedMessages.filter { m ->
            updatedMessages.firstOrNull { it.id == m.id } == null
        }
        val shouldBeUpdated = updatedMessages.filter { m ->
            savedMessages.firstOrNull { it.id == m.id && it != m } != null
        }
        val shouldBeAdded = updatedMessages.filter { m ->
            savedMessages.firstOrNull { it.id == m.id } == null
        }

        messageDao.deleteMessages(*shouldBeDeleted.toTypedArray())
        messageDao.editMessages(*shouldBeUpdated.toTypedArray())
        messageDao.addMessages(*shouldBeAdded.toTypedArray())

        return chatRoom
    }

    override suspend fun deleteChats(chatRooms: List<ChatRoom>) {
        chatRoomDao.deleteChatRooms(*chatRooms.toTypedArray())
    }

    private fun messageToOpenAIMessage(messages: List<Message>): List<ChatMessage> {
        val result = mutableListOf<ChatMessage>()

        messages.forEach { message ->
            when (message.platformType) {
                null -> {
                    result.add(
                        ChatMessage(
                            role = ChatRole.User,
                            content = message.content
                        )
                    )
                }

                ApiType.OPENAI -> {
                    result.add(
                        ChatMessage(
                            role = ChatRole.Assistant,
                            content = message.content
                        )
                    )
                }

                else -> {}
            }
        }

        return result
    }

    private fun messageToOllamaMessage(messages: List<Message>): List<OllamaChatMessage> {
        val result = mutableListOf<OllamaChatMessage>()

        messages.forEach { message ->
            when (message.platformType) {
                null -> result.add(
                    OllamaChatMessage(role = ChatRole.User.role, content = message.content)
                )

                ApiType.OLLAMA -> result.add(
                    OllamaChatMessage(role = ChatRole.Assistant.role, content = message.content)
                )

                else -> {}
            }
        }

        return result
    }

    private fun messageToAnthropicMessage(messages: List<Message>): List<InputMessage> {
        val result = mutableListOf<InputMessage>()

        messages.forEach { message ->
            when (message.platformType) {
                null -> result.add(
                    InputMessage(role = MessageRole.USER, content = listOf(TextContent(text = message.content)))
                )

                ApiType.ANTHROPIC -> result.add(
                    InputMessage(role = MessageRole.ASSISTANT, content = listOf(TextContent(text = message.content)))
                )

                else -> {}
            }
        }

        return result
    }

    private fun messageToGoogleMessage(messages: List<Message>): List<Content> {
        val result = mutableListOf<Content>()

        messages.forEach { message ->
            when (message.platformType) {
                null -> result.add(content(role = "user") { text(message.content) })

                ApiType.GOOGLE -> result.add(content(role = "model") { text(message.content) })

                else -> {}
            }
        }

        return result
    }
}
