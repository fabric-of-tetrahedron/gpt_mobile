package dev.chungjungsoo.gptmobile.data.repository

import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIHost
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.*
import com.tddworks.common.network.api.ktor.api.AnySerial
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
import javax.inject.Inject
import kotlinx.coroutines.flow.*

class ChatRepositoryImpl @Inject constructor(
    private val chatRoomDao: ChatRoomDao,
    private val messageDao: MessageDao,
    private val settingRepository: SettingRepository,
    private val anthropic: AnthropicAPI
) : ChatRepository {

    /**
     * OpenAI API客户端
     */
    private lateinit var openAI: OpenAI

    /**
     * Google Generative AI模型
     */
    private lateinit var google: GenerativeModel

    /**
     * Ollama API客户端
     */
    private lateinit var ollamaApi: OllamaApi

    /**
     * 使用OpenAI完成聊天
     *
     * @param question 当前问题
     * @param history 聊天历史
     * @return 包含API状态的Flow
     */
    override suspend fun completeOpenAIChat(question: Message, history: List<Message>): Flow<ApiState> {
        // 获取OpenAI平台配置
        val platform = checkNotNull(settingRepository.fetchPlatforms().firstOrNull { it.name == ApiType.OPENAI })
        // 初始化OpenAI客户端
        openAI = OpenAI(platform.token ?: "", host = OpenAIHost(baseUrl = platform.apiUrl))

        // 将历史消息和当前问题转换为OpenAI消息格式
        val generatedMessages = messageToOpenAIMessage(history + listOf(question))
        // 添加系统提示
        val generatedMessageWithPrompt = listOf(
            ChatMessage(role = ChatRole.System, content = platform.systemPrompt ?: ModelConstants.OPENAI_PROMPT)
        ) + generatedMessages
        // 创建聊天完成请求
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId(platform.model ?: ""),
            messages = generatedMessageWithPrompt,
            temperature = platform.temperature?.toDouble(),
            topP = platform.topP?.toDouble()
        )

        // 发送请求并处理响应
        return openAI.chatCompletions(chatCompletionRequest)
            .map<ChatCompletionChunk, ApiState> { chunk -> ApiState.Success(chunk.choices[0].delta.content ?: "") }
            .catch { throwable -> emit(ApiState.Error(throwable.message ?: "Unknown error")) }
            .onStart { emit(ApiState.Loading) }
            .onCompletion { emit(ApiState.Done) }
    }

    /**
     * 使用Anthropic完成聊天
     *
     * @param question 当前问题
     * @param history 聊天历史
     * @return 包含API状态的Flow
     */
    override suspend fun completeAnthropicChat(question: Message, history: List<Message>): Flow<ApiState> {
        // 获取Anthropic平台配置
        val platform = checkNotNull(settingRepository.fetchPlatforms().firstOrNull { it.name == ApiType.ANTHROPIC })
        // 设置Anthropic API的token和URL
        anthropic.setToken(platform.token)
        anthropic.setAPIUrl(platform.apiUrl)

        // 将历史消息和当前问题转换为Anthropic消息格式
        val generatedMessages = messageToAnthropicMessage(history + listOf(question))
        // 创建消息请求
        val messageRequest = MessageRequest(
            model = platform.model ?: "",
            messages = generatedMessages,
            maxTokens = ModelConstants.ANTHROPIC_MAXIMUM_TOKEN,
            systemPrompt = platform.systemPrompt ?: ModelConstants.DEFAULT_PROMPT,
            stream = true,
            temperature = platform.temperature,
            topP = platform.topP
        )

        // 发送流式请求并处理响应
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

    /**
     * 完成Google聊天
     *
     * @param question 当前问题
     * @param history 聊天历史
     * @return 包含API状态的Flow
     */
    override suspend fun completeGoogleChat(question: Message, history: List<Message>): Flow<ApiState> {
        // 获取Google平台配置
        val platform = checkNotNull(settingRepository.fetchPlatforms().firstOrNull { it.name == ApiType.GOOGLE })
        // 创建生成配置
        val config = generationConfig {
            temperature = platform.temperature
            topP = platform.topP
        }
        // 初始化Google生成模型
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

        // 将历史消息转换为Google消息格式
        val inputContent = messageToGoogleMessage(history)
        // 开始聊天
        val chat = google.startChat(history = inputContent)

        // 发送消息并处理响应
        return chat.sendMessageStream(question.content)
            .map<GenerateContentResponse, ApiState> { response -> ApiState.Success(response.text ?: "") }
            .catch { throwable -> emit(ApiState.Error(throwable.message ?: "Unknown error")) }
            .onStart { emit(ApiState.Loading) }
            .onCompletion { emit(ApiState.Done) }
    }

    /**
     * 完成Ollama聊天
     *
     * @param question 当前问题
     * @param history 聊天历史
     * @return 包含API状态的Flow
     */
    override suspend fun completeOllamaChat(question: Message, history: List<Message>): Flow<ApiState> {
        // 获取Ollama平台配置
        val platform = checkNotNull(settingRepository.fetchPlatforms().firstOrNull { it.name == ApiType.OLLAMA })

        // 如果Ollama API未初始化，则进行初始化
        if (!::ollamaApi.isInitialized) {
            initOllama(
                OllamaConfig(
                    baseUrl = { platform.apiUrl.split(":")[1].removePrefix("//") },
                    protocol = { platform.apiUrl.split(":")[0] },
                    port = { platform.apiUrl.split(":")[2].toInt() }
                )
            )
        }

        // 解析API URL
        val urlParts = platform.apiUrl.split(":")
        val protocol = urlParts[0]
        val baseUrl = urlParts[1].removePrefix("//")
        val port = urlParts[2].toInt()

        // 创建Ollama API实例
        ollamaApi = OllamaApi(
            baseUrl = baseUrl,
            port = port,
            protocol = protocol
        )

        // 将历史消息和当前问题转换为Ollama消息格式
        val generatedMessages = messageToOllamaMessage(history + listOf(question))
        val generatedMessageWithPrompt = listOf(
            OllamaChatMessage(role = ChatRole.System.role, content = platform.systemPrompt ?: ModelConstants.OPENAI_PROMPT)
        ) + generatedMessages

        // 设置选项
        val options = mutableMapOf<String, AnySerial>()
        platform.temperature?.let { options["temperature"] = it }
        platform.topP?.let { options["topP"] = it }

        // 创建聊天完成请求
        val chatCompletionRequest = OllamaChatRequest(
            model = platform.model!!,
            messages = generatedMessageWithPrompt,
            options = options,
            stream = false
        )

        // 发送请求并处理响应
        return flow {
            val response = ollamaApi.request(chatCompletionRequest)
            response.message?.let { message ->
                emit(ApiState.Success(message.content ?: ""))
            } ?: emit(ApiState.Error("No message content"))
        }
            .catch { throwable ->
                throwable.printStackTrace()
                emit(ApiState.Error(throwable.message ?: "Unknown error"))
            }
            .onStart {
                emit(ApiState.Loading)
            }
            .onCompletion {
                emit(ApiState.Done)
            }
    }

    /**
     * 获取聊天室列表
     *
     * @return 返回所有聊天室的列表
     */
    override suspend fun fetchChatList(): List<ChatRoom> = chatRoomDao.getChatRooms()

    /**
     * 获取指定聊天室的所有消息
     *
     * @param chatId 聊天室ID
     * @return 返回指定聊天室的所有消息列表
     */
    override suspend fun fetchMessages(chatId: Int): List<Message> = messageDao.loadMessages(chatId)

    /**
     * 更新聊天室标题
     *
     * @param chatRoom 要更新的聊天室
     * @param title 新的标题
     */
    override suspend fun updateChatTitle(chatRoom: ChatRoom, title: String) {
        chatRoomDao.editChatRoom(chatRoom.copy(title = title.take(50)))
    }

    /**
     * 保存聊天室及其消息
     *
     * @param chatRoom 要保存的聊天室
     * @param messages 要保存的消息列表
     * @return 返回保存后的聊天室
     */
    override suspend fun saveChat(chatRoom: ChatRoom, messages: List<Message>): ChatRoom {
        if (chatRoom.id == 0) {
            // 新建聊天室
            val chatId = chatRoomDao.addChatRoom(chatRoom)
            val updatedMessages = messages.map { it.copy(chatId = chatId.toInt()) }
            messageDao.addMessages(*updatedMessages.toTypedArray())

            val savedChatRoom = chatRoom.copy(id = chatId.toInt())
            updateChatTitle(savedChatRoom, updatedMessages[0].content)

            return savedChatRoom.copy(title = updatedMessages[0].content.take(50))
        }

        // 更新现有聊天室
        val savedMessages = fetchMessages(chatRoom.id)
        val updatedMessages = messages.map { it.copy(chatId = chatRoom.id) }

        // 找出需要删除、更新和添加的消息
        val shouldBeDeleted = savedMessages.filter { m ->
            updatedMessages.firstOrNull { it.id == m.id } == null
        }
        val shouldBeUpdated = updatedMessages.filter { m ->
            savedMessages.firstOrNull { it.id == m.id && it != m } != null
        }
        val shouldBeAdded = updatedMessages.filter { m ->
            savedMessages.firstOrNull { it.id == m.id } == null
        }

        // 执行删除、更新和添加操作
        messageDao.deleteMessages(*shouldBeDeleted.toTypedArray())
        messageDao.editMessages(*shouldBeUpdated.toTypedArray())
        messageDao.addMessages(*shouldBeAdded.toTypedArray())

        return chatRoom
    }

    /**
     * 删除多个聊天室
     *
     * @param chatRooms 要删除的聊天室列表
     */
    override suspend fun deleteChats(chatRooms: List<ChatRoom>) {
        chatRoomDao.deleteChatRooms(*chatRooms.toTypedArray())
    }

    /**
     * 将消息转换为OpenAI格式的消息
     *
     * @param messages 原始消息列表
     * @return 返回OpenAI格式的消息列表
     */
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

    /**
     * 将消息转换为Ollama格式的消息
     *
     * @param messages 原始消息列表
     * @return 返回Ollama格式的消息列表
     */
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

    /**
     * 将消息转换为Anthropic格式的消息
     *
     * @param messages 原始消息列表
     * @return 返回Anthropic格式的消息列表
     */
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

    /**
     * 将消息转换为Google格式的消息
     *
     * @param messages 原始消息列表
     * @return 返回Google格式的消息列表
     */
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
