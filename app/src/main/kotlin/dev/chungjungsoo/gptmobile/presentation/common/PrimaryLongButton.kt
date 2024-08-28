package dev.chungjungsoo.gptmobile.presentation.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * 主要长按钮组件
 *
 * 这个组件创建一个宽度填满父容器、高度固定的按钮。
 * 按钮的样式和行为可以通过参数进行自定义。
 *
 * @param modifier 应用于按钮的Modifier
 * @param enabled 按钮是否可用
 * @param onClick 点击按钮时触发的回调函数
 * @param text 按钮上显示的文本
 */
@Preview
@Composable
fun PrimaryLongButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
    text: String = ""
) {
    Button(
        modifier = modifier
            .padding(20.dp) // 设置按钮的内边距为20dp
            .fillMaxWidth() // 使按钮宽度填满父容器
            .height(56.dp), // 设置按钮高度为56dp
        onClick = onClick, // 设置点击事件回调
        enabled = enabled // 设置按钮是否可用
    ) {
        // 在按钮内显示文本
        Text(text = text)
    }
}
