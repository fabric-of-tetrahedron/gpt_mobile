package dev.chungjungsoo.gptmobile.presentation.ui.setup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.chungjungsoo.gptmobile.R
import dev.chungjungsoo.gptmobile.data.dto.Platform
import dev.chungjungsoo.gptmobile.presentation.common.PlatformCheckBoxItem
import dev.chungjungsoo.gptmobile.presentation.common.PrimaryLongButton
import dev.chungjungsoo.gptmobile.presentation.common.Route
import dev.chungjungsoo.gptmobile.util.collectManagedState
import dev.chungjungsoo.gptmobile.util.getPlatformDescriptionResources
import dev.chungjungsoo.gptmobile.util.getPlatformTitleResources

/**
 * 选择平台屏幕的可组合函数
 *
 * @param modifier 修饰符
 * @param setupViewModel 设置视图模型
 * @param currentRoute 当前路由
 * @param onNavigate 导航回调函数
 * @param onBackAction 返回操作回调函数
 */
@Composable
fun SelectPlatformScreen(
    modifier: Modifier = Modifier,
    setupViewModel: SetupViewModel = hiltViewModel(),
    currentRoute: String = Route.SELECT_PLATFORM,
    onNavigate: (route: String) -> Unit = {},
    onBackAction: () -> Unit
) {
    // 收集平台状态
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
        ) {
            GetStartedText()
            SelectPlatform(
                platforms = platformState,
                onClickEvent = { setupViewModel.updateCheckedState(it) }
            )
            Spacer(modifier = Modifier.weight(1f))
            PrimaryLongButton(
                enabled = platformState.any { it.selected },
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
 * 显示"开始使用"文本的可组合函数
 *
 * @param modifier 修饰符
 */
@Preview
@Composable
fun GetStartedText(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            modifier = Modifier
                .padding(4.dp)
                .semantics { heading() },
            text = stringResource(R.string.get_started),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            modifier = Modifier.padding(4.dp),
            text = stringResource(R.string.platform_select_description),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/**
 * 选择平台的可组合函数
 *
 * @param modifier 修饰符
 * @param platforms 平台列表
 * @param onClickEvent 点击事件回调函数
 */
@Composable
fun SelectPlatform(
    modifier: Modifier = Modifier,
    platforms: List<Platform>,
    onClickEvent: (Platform) -> Unit
) {
    // 获取平台标题和描述资源
    val titles = getPlatformTitleResources()
    val descriptions = getPlatformDescriptionResources()

    Column(modifier = modifier) {
        platforms.forEach { platform ->
            PlatformCheckBoxItem(
                platform = platform,
                title = titles[platform.name]!!,
                description = descriptions[platform.name]!!,
                onClickEvent = onClickEvent
            )
        }
    }
}
