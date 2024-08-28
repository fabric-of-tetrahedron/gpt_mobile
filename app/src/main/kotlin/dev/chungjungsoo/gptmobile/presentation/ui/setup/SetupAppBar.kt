package dev.chungjungsoo.gptmobile.presentation.ui.setup

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.chungjungsoo.gptmobile.R

/**
 * 设置界面的顶部应用栏
 *
 * @param backAction 点击返回按钮时执行的操作
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupAppBar(
    backAction: () -> Unit
) {
    TopAppBar(
        // 标题为空，因为这个设置界面可能不需要标题
        title = { },
        // 导航图标，这里是一个返回箭头
        navigationIcon = {
            IconButton(onClick = backAction) {
                // 使用自动镜像的返回箭头图标，以支持从右到左的布局
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    // 使用字符串资源作为内容描述，以支持本地化
                    contentDescription = stringResource(R.string.go_back)
                )
            }
        }
    )
}
