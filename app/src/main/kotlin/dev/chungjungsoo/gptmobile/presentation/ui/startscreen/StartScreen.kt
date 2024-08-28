package dev.chungjungsoo.gptmobile.presentation.ui.startscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.chungjungsoo.gptmobile.R
import dev.chungjungsoo.gptmobile.presentation.common.PrimaryLongButton
import dev.chungjungsoo.gptmobile.presentation.icons.GptMobileStartScreen

/**
 * 启动屏幕组件
 *
 * @param onStartClick 开始按钮点击回调函数
 */
@Composable
fun StartScreen(onStartClick: () -> Unit) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StartScreenLogo() // 显示启动屏幕logo
            Spacer(modifier = Modifier.weight(1f)) // 添加弹性空间
            WelcomeText() // 显示欢迎文本
            PrimaryLongButton(
                onClick = onStartClick,
                text = stringResource(R.string.get_started)
            ) // 显示"开始"按钮
        }
    }
}

/**
 * 启动屏幕logo组件
 *
 * @param modifier Modifier对象，用于自定义组件样式
 */
@Preview
@Composable
fun StartScreenLogo(modifier: Modifier = Modifier) {
    Image(
        imageVector = GptMobileStartScreen,
        contentDescription = stringResource(R.string.gpt_mobile_introduction_logo),
        contentScale = ContentScale.FillHeight,
        modifier = modifier
            .padding(top = 50.dp)
            .height(400.dp)
    )
}

/**
 * 欢迎文本组件
 *
 * @param modifier Modifier对象，用于自定义组件样式
 */
@Preview
@Composable
fun WelcomeText(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            modifier = Modifier
                .padding(4.dp)
                .semantics { heading() },
            text = stringResource(R.string.welcome_title),
            style = MaterialTheme.typography.headlineMedium
        ) // 显示欢迎标题
        Text(
            modifier = Modifier.padding(4.dp),
            text = stringResource(R.string.welcome_description),
            style = MaterialTheme.typography.bodyLarge
        ) // 显示欢迎描述
    }
}
