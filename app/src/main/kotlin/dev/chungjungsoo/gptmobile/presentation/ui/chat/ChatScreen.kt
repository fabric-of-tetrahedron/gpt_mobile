package dev.chungjungsoo.gptmobile.presentation.ui.chat

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.chungjungsoo.gptmobile.R
import dev.chungjungsoo.gptmobile.data.database.entity.Message
import dev.chungjungsoo.gptmobile.data.model.ApiType
import dev.chungjungsoo.gptmobile.util.collectManagedState
import dev.chungjungsoo.gptmobile.util.multiScrollStateSaver

/**
 * 聊天界面的主要组件
 *
 * @param chatViewModel 聊天视图模型，用于管理聊天状态和逻辑
 * @param onBackAction 返回操作的回调函数
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel = hiltViewModel(),
    onBackAction: () -> Unit
) {
    // 获取屏幕宽度
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val focusManager = LocalFocusManager.current
    val clipboardManager = LocalClipboardManager.current
    val systemChatMargin = 32.dp
    // 计算聊天气泡的最大宽度
    val maximumChatBubbleWidth = screenWidth - 48.dp - systemChatMargin
    val listState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    // 从ViewModel收集状态
    val isIdle by chatViewModel.isIdle.collectManagedState()
    val messages by chatViewModel.messages.collectManagedState()
    val question by chatViewModel.question.collectManagedState()
    val appEnabledPlatforms by chatViewModel.enabledPlatformsInApp.collectManagedState()

    // 各平台的加载状态
    val openaiLoadingState by chatViewModel.openaiLoadingState.collectManagedState()
    val anthropicLoadingState by chatViewModel.anthropicLoadingState.collectManagedState()
    val googleLoadingState by chatViewModel.googleLoadingState.collectManagedState()
    val ollamaLoadingState by chatViewModel.ollamaLoadingState.collectManagedState()

    // 用户消息和各平台的回复消息
    val userMessage by chatViewModel.userMessage.collectManagedState()
    val openAIMessage by chatViewModel.openAIMessage.collectManagedState()
    val anthropicMessage by chatViewModel.anthropicMessage.collectManagedState()
    val googleMessage by chatViewModel.googleMessage.collectManagedState()
    val ollamaMessage by chatViewModel.ollamaMessage.collectManagedState()

    // 检查是否可以使用聊天功能
    val canUseChat = (chatViewModel.enabledPlatformsInChat.toSet() - appEnabledPlatforms.toSet()).isEmpty()
    // 对消息进行分组
    val groupedMessages = remember(messages) { groupMessages(messages) }
    val latestMessageIndex = groupedMessages.keys.maxOrNull() ?: 0
    // 为每个聊天气泡创建滚动状态
    val chatBubbleScrollStates = rememberSaveable(saver = multiScrollStateSaver) { MutableList(latestMessageIndex + 2) { ScrollState(0) } }

    // 当最新消息索引变化时，添加新的滚动状态
    LaunchedEffect(latestMessageIndex) {
        val opponentBubbles = ((latestMessageIndex + 1) / 2) + 1
        val scrollStatesToAdd = opponentBubbles - chatBubbleScrollStates.size

        if (scrollStatesToAdd > 0) {
            repeat(scrollStatesToAdd) {
                chatBubbleScrollStates.add(ScrollState(0))
            }
        }
    }

    // 当聊天状态变为空闲时，滚动到最新消息
    LaunchedEffect(isIdle) {
        listState.animateScrollToItem(groupedMessages.keys.size)
    }

    // 主界面结构
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { focusManager.clearFocus() },
        topBar = { ChatTopBar(onBackAction, scrollBehavior) },
        bottomBar = {
            ChatInputBox(
                value = question,
                onValueChange = { s -> chatViewModel.updateQuestion(s) },
                chatEnabled = canUseChat,
                sendButtonEnabled = question.trim().isNotBlank() && isIdle
            ) {
                chatViewModel.askQuestion()
                focusManager.clearFocus()
            }
        }
    ) { innerPadding ->
        // 打印分组消息的日志
        groupedMessages.forEach { (i, k) -> Log.d("grouped", "idx: $i, data: $k") }
        // 聊天消息列表
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            state = listState
        ) {
            groupedMessages.keys.sorted().forEach { key ->
                if (key % 2 == 0) {
                    // 用户消息
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            UserChatBubble(
                                modifier = Modifier.widthIn(max = maximumChatBubbleWidth),
                                text = groupedMessages[key]!![0].content
                            )
                        }
                    }
                } else {
                    // 助手消息
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(chatBubbleScrollStates[(key - 1) / 2])
                        ) {
                            Spacer(modifier = Modifier.width(8.dp))
                            groupedMessages[key]!!.sortedBy { it.platformType }.forEach { m ->
                                m.platformType?.let { apiType ->
                                    OpponentChatBubble(
                                        modifier = Modifier
                                            .padding(horizontal = 8.dp, vertical = 12.dp)
                                            .widthIn(max = maximumChatBubbleWidth),
                                        canRetry = canUseChat && isIdle && key >= latestMessageIndex,
                                        isLoading = false,
                                        apiType = apiType,
                                        text = m.content,
                                        onCopyClick = { clipboardManager.setText(AnnotatedString(m.content.trim())) },
                                        onRetryClick = { chatViewModel.retryQuestion(m) }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(systemChatMargin))
                        }
                    }
                }
            }

            // 显示正在进行的对话
            if (!isIdle) {
                // 显示用户当前输入的消息
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        UserChatBubble(modifier = Modifier.widthIn(max = maximumChatBubbleWidth), text = userMessage.content)
                    }
                }

                // 显示各平台的回复
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(chatBubbleScrollStates[(latestMessageIndex + 1) / 2])
                    ) {
                        Spacer(modifier = Modifier.width(8.dp))
                        chatViewModel.enabledPlatformsInChat.sorted().forEach { apiType ->
                            val message = when (apiType) {
                                ApiType.OPENAI -> openAIMessage
                                ApiType.ANTHROPIC -> anthropicMessage
                                ApiType.GOOGLE -> googleMessage
                                ApiType.OLLAMA -> ollamaMessage
                            }

                            val loadingState = when (apiType) {
                                ApiType.OPENAI -> openaiLoadingState
                                ApiType.ANTHROPIC -> anthropicLoadingState
                                ApiType.GOOGLE -> googleLoadingState
                                ApiType.OLLAMA -> ollamaLoadingState
                            }

                            OpponentChatBubble(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 12.dp)
                                    .widthIn(max = maximumChatBubbleWidth),
                                canRetry = canUseChat,
                                isLoading = loadingState == ChatViewModel.LoadingState.Loading,
                                apiType = apiType,
                                text = message.content,
                                onCopyClick = { clipboardManager.setText(AnnotatedString(message.content.trim())) },
                                onRetryClick = { chatViewModel.retryQuestion(message) }
                            )
                        }
                        Spacer(modifier = Modifier.width(systemChatMargin))
                    }
                }
            }
        }
    }
}

/**
 * 对消息列表进行分组
 *
 * @param messages 需要分组的消息列表
 * @return 分组后的消息，以HashMap形式返回，键为分组索引，值为该组的消息列表
 */
private fun groupMessages(messages: List<Message>): HashMap<Int, MutableList<Message>> {
    val classifiedMessages = hashMapOf<Int, MutableList<Message>>()
    var counter = 0

    messages.sortedBy { it.createdAt }.forEach { message ->
        if (message.platformType == null) {
            // 如果是用户消息（没有平台类型）
            if (classifiedMessages.containsKey(counter) || counter % 2 == 1) {
                counter++
            }

            classifiedMessages[counter] = mutableListOf(message)
            counter++
        } else {
            // 如果是平台消息
            if (counter % 2 == 0) {
                counter++
            }

            if (classifiedMessages.containsKey(counter)) {
                classifiedMessages[counter]?.add(message)
            } else {
                classifiedMessages[counter] = mutableListOf(message)
            }
        }
    }
    return classifiedMessages
}

/**
 * 聊天界面的顶部应用栏
 *
 * @param onBackAction 返回按钮点击时的回调函数
 * @param scrollBehavior 滚动行为
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ChatTopBar(
    onBackAction: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = { /*TODO*/ },
        navigationIcon = {
            IconButton(
                onClick = onBackAction
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.go_back))
            }
        },
        scrollBehavior = scrollBehavior
    )
}

/**
 * 聊天输入框组件
 *
 * @param value 输入框的当前值
 * @param onValueChange 输入值变化时的回调函数
 * @param chatEnabled 是否启用聊天功能
 * @param sendButtonEnabled 是否启用发送按钮
 * @param onSendButtonClick 发送按钮点击时的回调函数
 */
@Preview
@Composable
fun ChatInputBox(
    value: String = "",
    onValueChange: (String) -> Unit = {},
    chatEnabled: Boolean = true,
    sendButtonEnabled: Boolean = true,
    onSendButtonClick: (String) -> Unit = {}
) {
    val localStyle = LocalTextStyle.current
    val mergedStyle = localStyle.merge(TextStyle(color = LocalContentColor.current))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(BottomAppBarDefaults.windowInsets)
            .padding(BottomAppBarDefaults.ContentPadding)
            .background(color = MaterialTheme.colorScheme.surface)
    ) {
        BasicTextField(
            modifier = Modifier
                .heightIn(max = 120.dp),
            value = value,
            enabled = chatEnabled,
            textStyle = mergedStyle,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            onValueChange = { if (chatEnabled) onValueChange(it) },
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(size = 24.dp))
                        .padding(all = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                            .padding(start = 16.dp)
                    ) {
                        // 显示占位符文本
                        if (value.isEmpty()) {
                            Text(
                                modifier = Modifier.alpha(0.38f),
                                text = if (chatEnabled) stringResource(R.string.ask_a_question) else stringResource(R.string.some_platforms_disabled)
                            )
                        }
                        innerTextField()
                    }
                    // 发送按钮
                    IconButton(
                        enabled = chatEnabled && sendButtonEnabled,
                        onClick = { onSendButtonClick(value) }
                    ) {
                        Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_send), contentDescription = stringResource(R.string.send))
                    }
                }
            }
        )
    }
}
