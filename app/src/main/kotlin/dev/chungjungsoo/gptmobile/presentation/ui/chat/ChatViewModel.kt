package dev.chungjungsoo.gptmobile.presentation.ui.chat

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.chungjungsoo.gptmobile.data.database.entity.ChatRoom
import dev.chungjungsoo.gptmobile.data.database.entity.Message
import dev.chungjungsoo.gptmobile.data.dto.ApiState
import dev.chungjungsoo.gptmobile.data.model.ApiType
import dev.chungjungsoo.gptmobile.data.repository.ChatRepository
import dev.chungjungsoo.gptmobile.data.repository.SettingRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 聊天视图模型类
 *
 * 该类负责管理聊天界面的数据和状态，包括消息列表、加载状态、用户输入等。
 * 它与多个AI平台（如OpenAI、Anthropic、Google和Ollama）进行交互，处理消息的发送和接收。
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val chatRepository: ChatRepository,
    private val settingRepository: SettingRepository
) : ViewModel() {

    /**
     * 加载状态密封类
     *
     * 用于表示各AI平台的加载状态
     */
    sealed class LoadingState {
        /** 空闲状态 */
        data object Idle : LoadingState()

        /** 加载中状态 */
        data object Loading : LoadingState()
    }

    // 从SavedStateHandle中获取聊天室ID
    private val chatRoomId: Int = checkNotNull(savedStateHandle["chatRoomId"])

    // 从SavedStateHandle中获取启用的平台字符串
    private val enabledPlatformString: String = checkNotNull(savedStateHandle["enabledPlatforms"])

    // 将启用的平台字符串转换为ApiType列表
    val enabledPlatformsInChat = enabledPlatformString.split(',').map { s -> ApiType.valueOf(s) }

    // 聊天室对象，稍后将被初始化
    private lateinit var chatRoom: ChatRoom

    // 获取当前时间戳（秒）
    private val currentTimeStamp: Long
        get() = System.currentTimeMillis() / 1000

    // 应用中启用的AI平台列表
    private val _enabledPlatformsInApp = MutableStateFlow(listOf<ApiType>())
    val enabledPlatformsInApp = _enabledPlatformsInApp.asStateFlow()

    // 消息列表
    private val _messages = MutableStateFlow(listOf<Message>())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    // 用户输入的问题
    private val _question = MutableStateFlow("")
    val question: StateFlow<String> = _question.asStateFlow()

    // 各AI平台的加载状态
    private val _openaiLoadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val openaiLoadingState = _openaiLoadingState.asStateFlow()

    private val _anthropicLoadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val anthropicLoadingState = _anthropicLoadingState.asStateFlow()

    private val _googleLoadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val googleLoadingState = _googleLoadingState.asStateFlow()

    private val _ollamaLoadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val ollamaLoadingState = _ollamaLoadingState.asStateFlow()

    // 整体空闲状态
    private val _isIdle = MutableStateFlow(true)
    val isIdle = _isIdle.asStateFlow()

    // 用户消息
    private val _userMessage = MutableStateFlow(Message(chatId = chatRoomId, content = "", platformType = null))
    val userMessage = _userMessage.asStateFlow()

    // 各AI平台的消息
    private val _openAIMessage = MutableStateFlow(Message(chatId = chatRoomId, content = "", platformType = ApiType.OPENAI))
    val openAIMessage = _openAIMessage.asStateFlow()

    private val _anthropicMessage = MutableStateFlow(Message(chatId = chatRoomId, content = "", platformType = ApiType.ANTHROPIC))
    val anthropicMessage = _anthropicMessage.asStateFlow()

    private val _googleMessage = MutableStateFlow(Message(chatId = chatRoomId, content = "", platformType = ApiType.GOOGLE))
    val googleMessage = _googleMessage.asStateFlow()

    private val _ollamaMessage = MutableStateFlow(Message(chatId = chatRoomId, content = "", platformType = ApiType.OLLAMA))
    val ollamaMessage = _ollamaMessage.asStateFlow()

    // 各AI平台的API状态流
    private val openAIFlow = MutableSharedFlow<ApiState>()
    private val anthropicFlow = MutableSharedFlow<ApiState>()
    private val googleFlow = MutableSharedFlow<ApiState>()
    private val ollamaFlow = MutableSharedFlow<ApiState>()

    /**
     * 初始化函数
     *
     * 在ViewModel创建时执行，用于初始化数据和设置观察者
     */
    init {
        Log.d("ViewModel", "$chatRoomId")
        Log.d("ViewModel", "$enabledPlatformsInChat")
        fetchChatRoom()
        fetchMessages()
        fetchEnabledPlatformsInApp()
        observeFlow()
    }

    /**
     * 发送用户问题
     *
     * 这个函数处理用户提交的问题，更新用户消息状态，清空问题输入框，
     * 然后调用completeChat()函数来获取AI的回答。
     */
    fun askQuestion() {
        Log.d("Question: ", _question.value)
        _userMessage.update { it.copy(content = _question.value, createdAt = currentTimeStamp) }
        _question.update { "" }
        completeChat()
    }

    /**
     * 重试特定平台的问题
     *
     * @param message 需要重试的消息
     *
     * 这个函数用于重新发送之前失败或需要重新生成的AI回答。
     * 它会找到最近的用户问题，移除相关的AI回答，然后重新请求指定平台的回答。
     */
    fun retryQuestion(message: Message) {
        // 找到最后一个用户问题的索引
        val latestQuestionIndex = _messages.value.indexOfLast { it.platformType == null }

        // 如果找到了用户问题，更新用户消息
        if (latestQuestionIndex != -1) {
            _userMessage.update { _messages.value[latestQuestionIndex] }
        }

        // 获取之前的AI回答
        val previousAnswers = enabledPlatformsInChat.mapNotNull { apiType -> _messages.value.lastOrNull { it.platformType == apiType } }

        // 从消息列表中移除用户问题和之前的AI回答
        _messages.update {
            if (latestQuestionIndex != -1) {
                it - setOf(_messages.value[latestQuestionIndex]) - previousAnswers.toSet()
            } else {
                it - previousAnswers.toSet()
            }
        }

        // 更新指定平台的加载状态
        message.platformType?.let { updateLoadingState(it, LoadingState.Loading) }

        // 恢复不需要重试的平台的消息状态
        enabledPlatformsInChat.forEach { apiType ->
            when (apiType) {
                message.platformType -> {}
                else -> restoreMessageState(apiType, previousAnswers)
            }
        }

        // 根据消息类型调用相应的完成聊天函数
        when (message.platformType) {
            ApiType.OPENAI -> {
                _openAIMessage.update { it.copy(id = message.id, content = "", createdAt = currentTimeStamp) }
                completeOpenAIChat()
            }

            ApiType.ANTHROPIC -> {
                _anthropicMessage.update { it.copy(id = message.id, content = "", createdAt = currentTimeStamp) }
                completeAnthropicChat()
            }

            ApiType.GOOGLE -> {
                _googleMessage.update { it.copy(id = message.id, content = "", createdAt = currentTimeStamp) }
                completeGoogleChat()
            }

            ApiType.OLLAMA -> {
                _ollamaMessage.update { it.copy(id = message.id, content = "", createdAt = currentTimeStamp) }
                completeOllamaChat()
            }

            else -> {
                // 处理未知的消息类型
            }
        }
    }

    /**
     * 更新用户输入的问题
     *
     * @param q 新的问题内容
     */
    fun updateQuestion(q: String) = _question.update { q }

    /**
     * 添加新消息到消息列表
     *
     * @param message 要添加的消息
     */
    private fun addMessage(message: Message) = _messages.update { it + listOf(message) }

    /**
     * 清空当前的问题和答案
     *
     * 重置用户消息和所有AI平台的消息状态
     */
    private fun clearQuestionAndAnswers() {
        _userMessage.update { it.copy(id = 0, content = "") }
        _openAIMessage.update { it.copy(id = 0, content = "") }
        _anthropicMessage.update { it.copy(id = 0, content = "") }
        _googleMessage.update { it.copy(id = 0, content = "") }
    }

    /**
     * 完成聊天
     *
     * 这个函数会为所有启用的AI平台发起聊天请求
     */
    private fun completeChat() {
        enabledPlatformsInChat.forEach { apiType -> updateLoadingState(apiType, LoadingState.Loading) }
        val enabledPlatforms = enabledPlatformsInChat.toSet()

        if (ApiType.OPENAI in enabledPlatforms) {
            completeOpenAIChat()
        }

        if (ApiType.ANTHROPIC in enabledPlatforms) {
            completeAnthropicChat()
        }

        if (ApiType.GOOGLE in enabledPlatforms) {
            completeGoogleChat()
        }

        if (ApiType.OLLAMA in enabledPlatforms) {
            completeOllamaChat()
        }
    }

    /**
     * 完成Anthropic聊天
     *
     * 发起Anthropic API请求并收集响应
     */
    private fun completeAnthropicChat() {
        viewModelScope.launch {
            val chatFlow = chatRepository.completeAnthropicChat(question = _userMessage.value, history = _messages.value)
            chatFlow.collect { chunk -> anthropicFlow.emit(chunk) }
        }
    }

    /**
     * 完成Google聊天
     *
     * 发起Google API请求并收集响应
     */
    private fun completeGoogleChat() {
        viewModelScope.launch {
            val chatFlow = chatRepository.completeGoogleChat(question = _userMessage.value, history = _messages.value)
            chatFlow.collect { chunk -> googleFlow.emit(chunk) }
        }
    }

    /**
     * 完成OpenAI聊天
     *
     * 发起OpenAI API请求并收集响应
     */
    private fun completeOpenAIChat() {
        viewModelScope.launch {
            val chatFlow = chatRepository.completeOpenAIChat(question = _userMessage.value, history = _messages.value)
            chatFlow.collect { chunk -> openAIFlow.emit(chunk) }
        }
    }

    /**
     * 完成Ollama聊天
     *
     * 发起Ollama API请求并收集响应
     */
    private fun completeOllamaChat() {
        viewModelScope.launch {
            val chatFlow = chatRepository.completeOllamaChat(question = _userMessage.value, history = _messages.value)
            chatFlow.collect { chunk -> ollamaFlow.emit(chunk) }
        }
    }

    /**
     * 获取聊天消息
     *
     * 从数据库中获取当前聊天室的所有消息
     */
    private fun fetchMessages() {
        viewModelScope.launch {
            // 如果聊天室不是新创建的
            if (chatRoomId != 0) {
                _messages.update { chatRepository.fetchMessages(chatRoomId) }
                return@launch
            }

            // 当消息ID应该在保存聊天后同步时
            if (chatRoom.id != 0) {
                _messages.update { chatRepository.fetchMessages(chatRoom.id) }
                return@launch
            }
        }
    }

    /**
     * 获取聊天室信息
     *
     * 从数据库中获取当前聊天室的信息，或创建一个新的聊天室
     */
    private fun fetchChatRoom() {
        viewModelScope.launch {
            chatRoom = if (chatRoomId == 0) {
                ChatRoom(title = "Untitled Chat", enabledPlatform = enabledPlatformsInChat)
            } else {
                chatRepository.fetchChatList().first { it.id == chatRoomId }
            }
            Log.d("ViewModel", "chatroom: $chatRoom")
        }
    }

    /**
     * 获取应用中启用的AI平台
     *
     * 从设置中获取用户启用的AI平台列表
     */
    private fun fetchEnabledPlatformsInApp() {
        viewModelScope.launch {
            val enabled = settingRepository.fetchPlatforms().filter { it.enabled }.map { it.name }
            _enabledPlatformsInApp.update { enabled }
        }
    }

    /**
     * 观察各种流的方法
     * 这个方法负责观察不同API的响应流，并相应地更新UI状态
     */
    private fun observeFlow() {
        // 观察OpenAI的响应流
        viewModelScope.launch {
            openAIFlow.collect { chunk ->
                when (chunk) {
                    is ApiState.Success -> _openAIMessage.update { it.copy(content = it.content + chunk.textChunk) }
                    ApiState.Done -> {
                        _openAIMessage.update { it.copy(createdAt = currentTimeStamp) }
                        updateLoadingState(ApiType.OPENAI, LoadingState.Idle)
                    }

                    is ApiState.Error -> {
                        _openAIMessage.update { it.copy(content = "Error: ${chunk.message}", createdAt = currentTimeStamp) }
                        updateLoadingState(ApiType.OPENAI, LoadingState.Idle)
                    }

                    else -> {}
                }
            }
        }

        // 观察Anthropic的响应流
        viewModelScope.launch {
            anthropicFlow.collect { chunk ->
                when (chunk) {
                    is ApiState.Success -> _anthropicMessage.update { it.copy(content = it.content + chunk.textChunk) }
                    ApiState.Done -> {
                        _anthropicMessage.update { it.copy(createdAt = currentTimeStamp) }
                        updateLoadingState(ApiType.ANTHROPIC, LoadingState.Idle)
                    }

                    is ApiState.Error -> {
                        _anthropicMessage.update { it.copy(content = "Error: ${chunk.message}", createdAt = currentTimeStamp) }
                        updateLoadingState(ApiType.ANTHROPIC, LoadingState.Idle)
                    }

                    else -> {}
                }
            }
        }

        // 观察Google的响应流
        viewModelScope.launch {
            googleFlow.collect { chunk ->
                when (chunk) {
                    is ApiState.Success -> _googleMessage.update { it.copy(content = it.content + chunk.textChunk) }
                    ApiState.Done -> {
                        _googleMessage.update { it.copy(createdAt = currentTimeStamp) }
                        updateLoadingState(ApiType.GOOGLE, LoadingState.Idle)
                    }

                    is ApiState.Error -> {
                        _googleMessage.update { it.copy(content = "Error: ${chunk.message}", createdAt = currentTimeStamp) }
                        updateLoadingState(ApiType.GOOGLE, LoadingState.Idle)
                    }

                    else -> {}
                }
            }
        }

        // 观察Ollama的响应流
        viewModelScope.launch {
            ollamaFlow.collect { chunk ->
                when (chunk) {
                    is ApiState.Success -> _ollamaMessage.update { it.copy(content = it.content + chunk.textChunk) }
                    ApiState.Done -> {
                        _ollamaMessage.update { it.copy(createdAt = currentTimeStamp) }
                        updateLoadingState(ApiType.OLLAMA, LoadingState.Idle)
                    }

                    is ApiState.Error -> {
                        _ollamaMessage.update { it.copy(content = "Error: ${chunk.message}", createdAt = currentTimeStamp) }
                        updateLoadingState(ApiType.OLLAMA, LoadingState.Idle)
                    }

                    else -> {}
                }
            }
        }

        // 观察空闲状态
        viewModelScope.launch {
            _isIdle.collect { status ->
                if (status) {
                    Log.d("status", "val: ${_userMessage.value}")
                    if (::chatRoom.isInitialized && _userMessage.value.content.isNotBlank()) {
                        syncQuestionAndAnswers()
                        Log.d("message", "${_messages.value}")
                        chatRoom = chatRepository.saveChat(chatRoom, _messages.value)
                        fetchMessages() // 同步消息ID
                    }
                    clearQuestionAndAnswers()
                }
            }
        }
    }

    /**
     * 恢复消息状态
     * @param apiType API类型
     * @param previousAnswers 之前的回答列表
     */
    private fun restoreMessageState(apiType: ApiType, previousAnswers: List<Message>) {
        val message = previousAnswers.firstOrNull { it.platformType == apiType }

        if (message == null) return

        when (apiType) {
            ApiType.OPENAI -> _openAIMessage.update { message }
            ApiType.ANTHROPIC -> _anthropicMessage.update { message }
            ApiType.GOOGLE -> _googleMessage.update { message }
            ApiType.OLLAMA -> _ollamaMessage.update { message }
        }
    }

    /**
     * 同步问题和答案
     * 将用户问题和各平台的回答添加到消息列表中
     */
    private fun syncQuestionAndAnswers() {
        addMessage(_userMessage.value)
        val enabledPlatforms = enabledPlatformsInChat.toSet()

        if (ApiType.OPENAI in enabledPlatforms) {
            addMessage(_openAIMessage.value)
        }

        if (ApiType.ANTHROPIC in enabledPlatforms) {
            addMessage(_anthropicMessage.value)
        }

        if (ApiType.GOOGLE in enabledPlatforms) {
            addMessage(_googleMessage.value)
        }

        if (ApiType.OLLAMA in enabledPlatforms) {
            addMessage(_ollamaMessage.value)
        }
    }

    /**
     * 更新加载状态
     * @param apiType API类型
     * @param loadingState 加载状态
     */
    private fun updateLoadingState(apiType: ApiType, loadingState: LoadingState) {
        when (apiType) {
            ApiType.OPENAI -> _openaiLoadingState.update { loadingState }
            ApiType.ANTHROPIC -> _anthropicLoadingState.update { loadingState }
            ApiType.GOOGLE -> _googleLoadingState.update { loadingState }
            ApiType.OLLAMA -> _ollamaLoadingState.update { loadingState }
        }

        // 检查所有启用的平台是否都处于空闲状态
        var result = true
        enabledPlatformsInChat.forEach {
            val state = when (it) {
                ApiType.OPENAI -> _openaiLoadingState
                ApiType.ANTHROPIC -> _anthropicLoadingState
                ApiType.GOOGLE -> _googleLoadingState
                ApiType.OLLAMA -> _ollamaLoadingState
            }

            result = result && (state.value is LoadingState.Idle)
        }

        _isIdle.update { result }
    }
}
