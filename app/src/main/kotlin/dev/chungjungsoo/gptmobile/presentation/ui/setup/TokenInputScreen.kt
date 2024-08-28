package dev.chungjungsoo.gptmobile.presentation.ui.setup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.chungjungsoo.gptmobile.R
import dev.chungjungsoo.gptmobile.data.dto.Platform
import dev.chungjungsoo.gptmobile.presentation.common.PrimaryLongButton
import dev.chungjungsoo.gptmobile.presentation.common.Route
import dev.chungjungsoo.gptmobile.presentation.common.TokenInputField
import dev.chungjungsoo.gptmobile.util.collectManagedState
import dev.chungjungsoo.gptmobile.util.getPlatformAPILabelResources
import dev.chungjungsoo.gptmobile.util.getPlatformHelpLinkResources

/**
 * Token输入屏幕组件
 *
 * @param modifier 修饰符
 * @param currentRoute 当前路由
 * @param setupViewModel 设置视图模型
 * @param onNavigate 导航回调函数
 * @param onBackAction 返回操作回调函数
 */
@Composable
fun TokenInputScreen(
    modifier: Modifier = Modifier,
    currentRoute: String = Route.TOKEN_INPUT,
    setupViewModel: SetupViewModel = hiltViewModel(),
    onNavigate: (route: String) -> Unit,
    onBackAction: () -> Unit
) {
    // 获取焦点管理器
    val focusManager = LocalFocusManager.current
    // 获取软键盘控制器
    val keyboardController = LocalSoftwareKeyboardController.current
    // 获取平台状态
    val platformState by setupViewModel.platformState.collectManagedState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { SetupAppBar(onBackAction) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    // 点击空白区域隐藏键盘并清除焦点
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
        ) {
            TokenInputText()
            TokenInput(
                platforms = platformState,
                onChangeEvent = { platform, s -> setupViewModel.updateToken(platform, s) },
                onClearEvent = { platform -> setupViewModel.updateToken(platform, "") }
            )
            Spacer(modifier = Modifier.weight(1f))
            PrimaryLongButton(
                enabled = platformState.filter { it.selected }.all { platform -> platform.token != null },
                onClick = {
                    val nextStep = setupViewModel.getNextSetupRoute(currentRoute)
                    onNavigate(nextStep)
                },
                text = stringResource(R.string.next)
            )
        }
    }
}

/**
 * Token输入文本组件
 *
 * @param modifier 修饰符
 */
@Preview
@Composable
fun TokenInputText(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            modifier = Modifier
                .padding(4.dp)
                .semantics { heading() },
            text = stringResource(R.string.enter_api_key),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            modifier = Modifier.padding(4.dp),
            text = stringResource(R.string.token_input_description),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/**
 * Token输入组件
 *
 * @param modifier 修饰符
 * @param platforms 平台列表
 * @param onChangeEvent Token变更回调函数
 * @param onClearEvent Token清除回调函数
 */
@Preview
@Composable
fun TokenInput(
    modifier: Modifier = Modifier,
    platforms: List<Platform> = listOf(),
    onChangeEvent: (Platform, String) -> Unit = { _, _ -> },
    onClearEvent: (Platform) -> Unit = {}
) {
    // 获取平台API标签资源
    val labels = getPlatformAPILabelResources()
    // 获取平台帮助链接资源
    val helpLinks = getPlatformHelpLinkResources()

    Column(modifier = modifier) {
        platforms.filter { it.selected }.forEachIndexed { i, platform ->
            val isLast = platforms.filter { it.selected }.size - 1 == i
            TokenInputField(
                value = platform.token ?: "",
                onValueChange = { onChangeEvent(platform, it) },
                onClearClick = { onClearEvent(platform) },
                label = labels[platform.name]!!,
                keyboardOptions = KeyboardOptions(imeAction = if (isLast) ImeAction.Done else ImeAction.Next),
                helpLink = helpLinks[platform.name]!!
            )
        }
    }
}
