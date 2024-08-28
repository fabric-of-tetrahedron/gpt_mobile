package dev.chungjungsoo.gptmobile.presentation.ui.setup

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.chungjungsoo.gptmobile.R
import dev.chungjungsoo.gptmobile.presentation.common.PrimaryLongButton
import dev.chungjungsoo.gptmobile.presentation.common.Route
import dev.chungjungsoo.gptmobile.presentation.icons.Done

/**
 * 设置完成屏幕
 *
 * @param modifier 修饰符
 * @param currentRoute 当前路由
 * @param setupViewModel 设置视图模型
 * @param onNavigate 导航回调函数
 * @param onBackAction 返回操作回调函数
 */
@Composable
fun SetupCompleteScreen(
    modifier: Modifier = Modifier,
    currentRoute: String = Route.SETUP_COMPLETE,
    setupViewModel: SetupViewModel = hiltViewModel(),
    onNavigate: (route: String) -> Unit,
    onBackAction: () -> Unit
) {
    // 获取当前屏幕配置
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // 创建Scaffold布局
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { SetupAppBar(onBackAction) }
    ) { innerPadding ->
        // 主要内容列
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // 显示设置完成文本
            SetupCompleteText()
            // 显示设置完成图标
            SetupCompleteLogo(
                Modifier
                    .widthIn(min = screenWidth)
                    .heightIn(min = screenWidth)
                    .padding(screenWidth * 0.1f)
            )
            // 添加弹性空间
            Spacer(modifier = Modifier.weight(1f))
            // 添加完成按钮
            PrimaryLongButton(
                onClick = {
                    // 保存平台状态
                    setupViewModel.savePlatformState()
                    // 获取下一个设置路由
                    val nextStep = setupViewModel.getNextSetupRoute(currentRoute)
                    // 导航到下一步
                    onNavigate(nextStep)
                },
                text = stringResource(R.string.done)
            )
        }
    }
}

/**
 * 设置完成文本组件
 *
 * @param modifier 修饰符
 */
@Preview
@Composable
private fun SetupCompleteText(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        // 标题文本
        Text(
            modifier = Modifier
                .padding(4.dp)
                .semantics { heading() },
            text = stringResource(R.string.setup_complete),
            style = MaterialTheme.typography.headlineMedium
        )
        // 描述文本
        Text(
            modifier = Modifier.padding(4.dp),
            text = stringResource(R.string.setup_complete_description),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/**
 * 设置完成图标组件
 *
 * @param modifier 修饰符
 */
@Preview
@Composable
private fun SetupCompleteLogo(modifier: Modifier = Modifier) {
    Image(
        imageVector = Done,
        contentDescription = stringResource(R.string.setup_complete_logo),
        modifier = modifier
            .padding(64.dp)
    )
}
