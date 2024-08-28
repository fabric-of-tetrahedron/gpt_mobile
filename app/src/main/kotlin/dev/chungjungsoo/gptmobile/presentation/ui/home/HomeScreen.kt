package dev.chungjungsoo.gptmobile.presentation.ui.home

import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import dev.chungjungsoo.gptmobile.R
import dev.chungjungsoo.gptmobile.data.database.entity.ChatRoom
import dev.chungjungsoo.gptmobile.data.dto.Platform
import dev.chungjungsoo.gptmobile.data.model.ApiType
import dev.chungjungsoo.gptmobile.presentation.common.PlatformCheckBoxItem
import dev.chungjungsoo.gptmobile.util.collectManagedState
import dev.chungjungsoo.gptmobile.util.getPlatformTitleResources

/**
 * 主页屏幕组件
 *
 * 该组件显示聊天室列表，并提供创建新聊天、删除聊天等功能。
 *
 * @param homeViewModel 主页视图模型，用于管理UI状态和业务逻辑
 * @param settingOnClick 设置按钮点击回调
 * @param onExistingChatClick 现有聊天室点击回调
 * @param navigateToNewChat 导航到新聊天的回调函数
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    settingOnClick: () -> Unit,
    onExistingChatClick: (ChatRoom) -> Unit,
    navigateToNewChat: (enabledPlatforms: List<ApiType>) -> Unit
) {
    /** 获取平台标题资源 */
    val platformTitles = getPlatformTitleResources()
    /** 创建懒加载列表状态 */
    val listState = rememberLazyListState()
    /** 创建顶部应用栏滚动行为 */
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    /** 收集聊天列表状态 */
    val chatListState by homeViewModel.chatListState.collectManagedState()
    /** 收集是否显示选择模型对话框的状态 */
    val showSelectModelDialog by homeViewModel.showSelectModelDialog.collectManagedState()
    /** 收集是否显示删除警告对话框的状态 */
    val showDeleteWarningDialog by homeViewModel.showDeleteWarningDialog.collectManagedState()
    /** 收集平台状态 */
    val platformState by homeViewModel.platformState.collectManagedState()
    /** 获取生命周期所有者 */
    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    /** 收集生命周期状态 */
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectManagedState()
    /** 获取上下文 */
    val context = LocalContext.current

    // 当生命周期状态变化时执行的副作用
    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED && !chatListState.isSelectionMode) {
            // 获取聊天列表和平台状态
            homeViewModel.fetchChats()
            homeViewModel.fetchPlatformStatus()
        }
    }

    // 处理返回按键事件
    BackHandler(enabled = chatListState.isSelectionMode) {
        homeViewModel.disableSelectionMode()
    }

    // 主界面脚手架
    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            // 主页顶部应用栏
            HomeTopAppBar(
                chatListState.isSelectionMode,
                selectedChats = chatListState.selected.count { it },
                scrollBehavior,
                actionOnClick = {
                    if (chatListState.isSelectionMode) {
                        homeViewModel.openDeleteWarningDialog()
                    } else {
                        settingOnClick()
                    }
                },
                navigationOnClick = {
                    homeViewModel.disableSelectionMode()
                }
            )
        },
        floatingActionButton = { NewChatButton(expanded = listState.isScrollingUp(), onClick = homeViewModel::openSelectModelDialog) }
    ) { innerPadding ->
        // 聊天室列表
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            state = listState
        ) {
            item { ChatsTitle(scrollBehavior) }
            itemsIndexed(chatListState.chats, key = { _, it -> it.id }) { idx, chatRoom ->
                val usingPlatform = chatRoom.enabledPlatform.joinToString(", ") { platformTitles[it] ?: "" }
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onLongClick = {
                                // 长按启用选择模式
                                homeViewModel.enableSelectionMode()
                                homeViewModel.selectChat(idx)
                            },
                            onClick = {
                                if (chatListState.isSelectionMode) {
                                    // 选择模式下点击选择聊天室
                                    homeViewModel.selectChat(idx)
                                } else {
                                    // 非选择模式下点击进入聊天室
                                    onExistingChatClick(chatRoom)
                                }
                            }
                        )
                        .padding(start = 8.dp, end = 8.dp)
                        .animateItemPlacement(),
                    headlineContent = { Text(text = chatRoom.title) },
                    leadingContent = {
                        if (chatListState.isSelectionMode) {
                            // 选择模式下显示复选框
                            Checkbox(
                                checked = chatListState.selected[idx],
                                onCheckedChange = { homeViewModel.selectChat(idx) }
                            )
                        } else {
                            // 非选择模式下显示聊天图标
                            Icon(
                                ImageVector.vectorResource(id = R.drawable.ic_rounded_chat),
                                contentDescription = stringResource(R.string.chat_icon)
                            )
                        }
                    },
                    supportingContent = { Text(text = stringResource(R.string.using_certain_platform, usingPlatform)) }
                )
            }
        }

        // 选择平台对话框
        if (showSelectModelDialog) {
            SelectPlatformDialog(
                platformState,
                onDismissRequest = { homeViewModel.closeSelectModelDialog() },
                onConfirmation = {
                    homeViewModel.closeSelectModelDialog()
                    navigateToNewChat(it)
                },
                onPlatformSelect = { homeViewModel.updateCheckedState(it) }
            )
        }

        // 删除警告对话框
        if (showDeleteWarningDialog) {
            DeleteWarningDialog(
                onDismissRequest = homeViewModel::closeDeleteWarningDialog,
                onConfirm = {
                    val deletedChatRoomCount = chatListState.selected.count { it }
                    homeViewModel.deleteSelectedChats()
                    Toast.makeText(context, context.getString(R.string.deleted_chats, deletedChatRoomCount), Toast.LENGTH_SHORT).show()
                    homeViewModel.closeDeleteWarningDialog()
                }
            )
        }
    }
}

/**
 * 主页顶部应用栏组件
 *
 * @param isSelectionMode 是否处于选择模式
 * @param selectedChats 已选择的聊天数量
 * @param scrollBehavior 顶部应用栏的滚动行为
 * @param actionOnClick 动作按钮点击回调
 * @param navigationOnClick 导航按钮点击回调
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    isSelectionMode: Boolean,
    selectedChats: Int,
    scrollBehavior: TopAppBarScrollBehavior,
    actionOnClick: () -> Unit,
    navigationOnClick: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            // 根据是否处于选择模式设置不同的颜色
            scrolledContainerColor = if (isSelectionMode) MaterialTheme.colorScheme.primaryContainer else Color.Unspecified,
            containerColor = if (isSelectionMode) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background,
            titleContentColor = if (isSelectionMode) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onBackground
        ),
        title = {
            if (isSelectionMode) {
                // 选择模式下显示已选择的聊天数量
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = stringResource(R.string.chats_selected, selectedChats),
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                // 非选择模式下显示"聊天"标题
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = stringResource(R.string.chats),
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = scrollBehavior.state.overlappedFraction),
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        navigationIcon = {
            if (isSelectionMode) {
                // 选择模式下显示关闭按钮
                IconButton(
                    modifier = Modifier.padding(4.dp),
                    onClick = navigationOnClick
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentDescription = stringResource(R.string.close)
                    )
                }
            }
        },
        actions = {
            if (isSelectionMode) {
                // 选择模式下显示删除按钮
                IconButton(
                    modifier = Modifier.padding(4.dp),
                    onClick = actionOnClick
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentDescription = stringResource(R.string.delete)
                    )
                }
            } else {
                // 非选择模式下显示设置按钮
                IconButton(
                    modifier = Modifier.padding(4.dp),
                    onClick = actionOnClick
                ) {
                    Icon(imageVector = Icons.Outlined.Settings, contentDescription = stringResource(R.string.settings))
                }
            }
        },
        scrollBehavior = scrollBehavior
    )
}

/**
 * 聊天标题组件
 *
 * @param scrollBehavior 顶部应用栏滚动行为
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatsTitle(scrollBehavior: TopAppBarScrollBehavior) {
    Text(
        modifier = Modifier
            .padding(top = 32.dp)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        text = stringResource(R.string.chats),
        // 根据滚动状态调整文本透明度
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 1.0F - scrollBehavior.state.overlappedFraction),
        style = MaterialTheme.typography.headlineLarge
    )
}

/**
 * 扩展函数：检查LazyListState是否正在向上滚动
 *
 * @return 如果列表正在向上滚动，则返回true；否则返回false
 */
@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    // 记住上一次的索引和滚动偏移量
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                // 如果索引变化，检查是否向上滚动
                previousIndex > firstVisibleItemIndex
            } else {
                // 如果索引相同，比较滚动偏移量
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                // 更新前一次的值
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

/**
 * 新建聊天按钮组件
 *
 * @param modifier 修饰符
 * @param expanded 是否展开按钮
 * @param onClick 点击事件处理函数
 */
@Preview
@Composable
fun NewChatButton(
    modifier: Modifier = Modifier,
    expanded: Boolean = true,
    onClick: () -> Unit = { }
) {
    val orientation = LocalConfiguration.current.orientation
    // 根据屏幕方向调整修饰符
    val fabModifier = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        modifier.systemBarsPadding()
    } else {
        modifier
    }
    ExtendedFloatingActionButton(
        modifier = fabModifier,
        onClick = { onClick() },
        expanded = expanded,
        icon = { Icon(Icons.Filled.Add, stringResource(R.string.new_chat)) },
        text = { Text(text = stringResource(R.string.new_chat)) }
    )
}

/**
 * 选择平台对话框组件
 *
 * @param platforms 可选平台列表
 * @param onDismissRequest 对话框关闭请求处理函数
 * @param onConfirmation 确认选择处理函数
 * @param onPlatformSelect 平台选择处理函数
 */
@Composable
fun SelectPlatformDialog(
    platforms: List<Platform>,
    onDismissRequest: () -> Unit,
    onConfirmation: (enabledPlatforms: List<ApiType>) -> Unit,
    onPlatformSelect: (Platform) -> Unit
) {
    val titles = getPlatformTitleResources()
    val configuration = LocalConfiguration.current

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 40.dp),
        onDismissRequest = onDismissRequest,
        title = {
            Column {
                Text(
                    text = stringResource(R.string.select_platform),
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = stringResource(R.string.select_platform_description),
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        text = {
            HorizontalDivider()
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                if (platforms.any { it.enabled }) {
                    // 显示已启用的平台列表
                    platforms.forEach { platform ->
                        PlatformCheckBoxItem(
                            platform = platform,
                            title = titles[platform.name]!!,
                            enabled = platform.enabled,
                            description = null,
                            onClickEvent = { onPlatformSelect(platform) }
                        )
                    }
                } else {
                    // 如果没有启用的平台，显示警告文本
                    EnablePlatformWarningText()
                }
                HorizontalDivider(Modifier.padding(top = 8.dp))
            }
        },
        confirmButton = {
            TextButton(
                enabled = platforms.any { it.selected },
                onClick = { onConfirmation(platforms.filter { it.selected }.map { it.name }) }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismissRequest() }
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

/**
 * 启用平台警告文本组件
 */
@Preview
@Composable
fun EnablePlatformWarningText() {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .wrapContentHeight(align = Alignment.CenterVertically)
            .padding(16.dp),
        textAlign = TextAlign.Center,
        text = stringResource(R.string.enable_at_leat_one_platform)
    )
}

/**
 * 选择平台对话框预览组件
 */
@Preview
@Composable
private fun SelectPlatformDialogPreview() {
    val platforms = listOf(
        Platform(ApiType.OPENAI, enabled = true),
        Platform(ApiType.ANTHROPIC, enabled = false),
        Platform(ApiType.GOOGLE, enabled = false)
    )
    SelectPlatformDialog(
        platforms = platforms,
        onDismissRequest = {},
        onConfirmation = {},
        onPlatformSelect = {}
    )
}

/**
 * 删除警告对话框组件
 *
 * @param onDismissRequest 对话框关闭请求处理函数
 * @param onConfirm 确认删除处理函数
 */
@Composable
fun DeleteWarningDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    val configuration = LocalConfiguration.current
    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.width(configuration.screenWidthDp.dp - 40.dp),
        title = {
            Text(
                text = stringResource(R.string.delete_selected_chats),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(stringResource(R.string.this_operation_can_t_be_undone))
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
