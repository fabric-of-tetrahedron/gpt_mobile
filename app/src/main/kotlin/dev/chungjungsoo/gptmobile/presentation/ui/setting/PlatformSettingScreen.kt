package dev.chungjungsoo.gptmobile.presentation.ui.setting

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.chungjungsoo.gptmobile.R
import dev.chungjungsoo.gptmobile.data.ModelConstants
import dev.chungjungsoo.gptmobile.data.model.ApiType
import dev.chungjungsoo.gptmobile.presentation.common.SettingItem
import dev.chungjungsoo.gptmobile.util.collectManagedState
import dev.chungjungsoo.gptmobile.util.getPlatformSettingTitle
import dev.chungjungsoo.gptmobile.util.pinnedExitUntilCollapsedScrollBehavior

/**
 * 平台设置界面
 *
 * 此界面用于显示和管理特定API平台的设置，如OpenAI、Anthropic等。
 * 用户可以在此界面启用/禁用API，设置API URL、密钥、模型等参数。
 *
 * @param modifier Modifier对象，用于自定义组件的外观和行为
 * @param apiType API类型，用于确定当前正在配置的平台
 * @param settingViewModel 设置视图模型，用于管理设置状态和操作
 * @param onNavigationClick 导航点击回调，用于处理返回操作
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlatformSettingScreen(
    modifier: Modifier = Modifier,
    apiType: ApiType,
    settingViewModel: SettingViewModel = hiltViewModel(),
    onNavigationClick: () -> Unit
) {
    // 创建滚动状态
    val scrollState = rememberScrollState()
    // 创建滚动行为，用于处理顶部应用栏的折叠效果
    val scrollBehavior = pinnedExitUntilCollapsedScrollBehavior(
        canScroll = { scrollState.canScrollForward || scrollState.canScrollBackward }
    )
    // 获取平台设置标题
    val title = getPlatformSettingTitle(apiType)
    // 收集平台状态
    val platformState by settingViewModel.platformState.collectManagedState()
    // 收集对话框状态
    val dialogState by settingViewModel.dialogState.collectManagedState()

    Scaffold(
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            // 设置顶部应用栏
            PlatformTopAppBar(
                title = title,
                onNavigationClick = onNavigationClick,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            // 获取当前平台的设置
            val platform = platformState.firstOrNull { it.name == apiType }
            val url = platform?.apiUrl ?: ModelConstants.getDefaultAPIUrl(apiType)
            val enabled = platform?.enabled ?: false
            val model = platform?.model
            val token = platform?.token
            val temperature = platform?.temperature ?: 1F
            val topP = platform?.topP
            // 根据API类型设置默认系统提示
            val systemPrompt = platform?.systemPrompt ?: when (apiType) {
                ApiType.OPENAI -> ModelConstants.OPENAI_PROMPT
                ApiType.ANTHROPIC -> ModelConstants.DEFAULT_PROMPT
                ApiType.GOOGLE -> ModelConstants.DEFAULT_PROMPT
                ApiType.OLLAMA -> ModelConstants.DEFAULT_PROMPT
            }

            // API 启用/禁用开关
            PreferenceSwitchWithContainer(
                title = stringResource(R.string.enable_api),
                isChecked = enabled
            ) { settingViewModel.toggleAPI(apiType) }

            // API URL 设置项
            SettingItem(
                modifier = Modifier.height(64.dp),
                title = stringResource(R.string.api_url),
                description = url,
                enabled = enabled && platform?.name != ApiType.GOOGLE,
                onItemClick = settingViewModel::openApiUrlDialog,
                showTrailingIcon = false,
                showLeadingIcon = true,
                leadingIcon = {
                    Icon(
                        ImageVector.vectorResource(id = R.drawable.ic_link),
                        contentDescription = stringResource(R.string.url_icon)
                    )
                }
            )

            // API 密钥设置项
            SettingItem(
                modifier = Modifier.height(64.dp),
                title = stringResource(R.string.api_key),
                description = token?.let { stringResource(R.string.token_set, it[0]) } ?: stringResource(R.string.token_not_set),
                enabled = enabled,
                onItemClick = settingViewModel::openApiTokenDialog,
                showTrailingIcon = false,
                showLeadingIcon = true,
                leadingIcon = {
                    Icon(
                        ImageVector.vectorResource(id = R.drawable.ic_key),
                        contentDescription = stringResource(R.string.key_icon)
                    )
                }
            )

            // API 模型设置项
            SettingItem(
                modifier = Modifier.height(64.dp),
                title = stringResource(R.string.api_model),
                description = model,
                enabled = enabled,
                onItemClick = settingViewModel::openApiModelDialog,
                showTrailingIcon = false,
                showLeadingIcon = true,
                leadingIcon = {
                    Icon(
                        ImageVector.vectorResource(id = R.drawable.ic_model),
                        contentDescription = stringResource(R.string.model_icon)
                    )
                }
            )

            // 温度设置项
            SettingItem(
                modifier = Modifier.height(64.dp),
                title = stringResource(R.string.temperature),
                description = temperature.toString(),
                enabled = enabled,
                onItemClick = settingViewModel::openTemperatureDialog,
                showTrailingIcon = false,
                showLeadingIcon = true,
                leadingIcon = {
                    Icon(
                        ImageVector.vectorResource(id = R.drawable.ic_temperature),
                        contentDescription = stringResource(R.string.temperature_icon)
                    )
                }
            )

            // Top P 设置项
            SettingItem(
                modifier = Modifier.height(64.dp),
                title = stringResource(R.string.top_p),
                description = topP?.toString(),
                enabled = enabled,
                onItemClick = settingViewModel::openTopPDialog,
                showTrailingIcon = false,
                showLeadingIcon = true,
                leadingIcon = {
                    Icon(
                        ImageVector.vectorResource(id = R.drawable.ic_chart),
                        contentDescription = stringResource(R.string.top_p_icon)
                    )
                }
            )

            // 系统提示设置项
            SettingItem(
                modifier = Modifier.height(64.dp),
                title = stringResource(R.string.system_prompt),
                description = systemPrompt,
                enabled = enabled,
                onItemClick = settingViewModel::openSystemPromptDialog,
                showTrailingIcon = false,
                showLeadingIcon = true,
                leadingIcon = {
                    Icon(
                        ImageVector.vectorResource(id = R.drawable.ic_instructions),
                        contentDescription = stringResource(R.string.system_prompt_icon)
                    )
                }
            )

            // 各种设置对话框
            APIUrlDialog(dialogState, apiType, url, settingViewModel)
            APIKeyDialog(dialogState, apiType, settingViewModel)
            ModelDialog(dialogState, apiType, model, settingViewModel)
            TemperatureDialog(dialogState, apiType, temperature, settingViewModel)
            TopPDialog(dialogState, apiType, topP, settingViewModel)
            SystemPromptDialog(dialogState, apiType, systemPrompt, settingViewModel)
        }
    }
}

/**
 * 平台顶部应用栏组件
 *
 * @param title 顶部应用栏标题
 * @param onNavigationClick 导航图标点击回调
 * @param scrollBehavior 滚动行为
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlatformTopAppBar(
    title: String,
    onNavigationClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    LargeTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        ),
        title = {
            // 标题文本
            Text(
                modifier = Modifier.padding(4.dp),
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            // 导航图标按钮
            IconButton(
                modifier = Modifier.padding(4.dp),
                onClick = onNavigationClick
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.go_back))
            }
        },
        scrollBehavior = scrollBehavior
    )
}

/**
 * 带容器的首选项开关组件
 *
 * @param title 开关标题
 * @param icon 可选的图标
 * @param isChecked 开关状态
 * @param onClick 点击回调
 */
@Composable
fun PreferenceSwitchWithContainer(
    title: String,
    icon: ImageVector? = null,
    isChecked: Boolean,
    onClick: () -> Unit
) {
    // 根据开关状态决定是否显示勾选图标
    val thumbContent: (@Composable () -> Unit)? = remember(isChecked) {
        if (isChecked) {
            {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize)
                )
            }
        } else {
            null
        }
    }

    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(
                MaterialTheme.colorScheme.primaryContainer
            )
            .toggleable(
                value = isChecked,
                onValueChange = { onClick() },
                interactionSource = interactionSource,
                indication = LocalIndication.current
            )
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 可选图标
        icon?.let {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 8.dp, end = 16.dp)
                    .size(24.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        // 标题文本
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = if (icon == null) 12.dp else 0.dp, end = 12.dp)
        ) {
            Text(
                text = title,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        // 开关组件
        Switch(
            checked = isChecked,
            interactionSource = interactionSource,
            onCheckedChange = null,
            modifier = Modifier.padding(start = 12.dp, end = 6.dp),
            thumbContent = thumbContent
        )
    }
}
