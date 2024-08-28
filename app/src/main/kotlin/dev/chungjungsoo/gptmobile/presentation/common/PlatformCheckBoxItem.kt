package dev.chungjungsoo.gptmobile.presentation.common

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.chungjungsoo.gptmobile.R
import dev.chungjungsoo.gptmobile.data.dto.Platform

/**
 * 平台选择复选框项
 *
 * @param modifier 修饰符
 * @param platform 平台对象
 * @param enabled 是否启用
 * @param title 标题文本
 * @param description 描述文本（可选）
 * @param onClickEvent 点击事件回调
 */
@Composable
fun PlatformCheckBoxItem(
    modifier: Modifier = Modifier,
    platform: Platform,
    enabled: Boolean = true,
    title: String = stringResource(R.string.sample_item_title),
    description: String? = stringResource(R.string.sample_item_description),
    onClickEvent: (Platform) -> Unit
) {
    // 创建交互源
    val interactionSource = remember { MutableInteractionSource() }

    // 根据是否启用设置不同的修饰符
    val rowModifier = if (enabled) {
        modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current
            ) { onClickEvent.invoke(platform) }
            .padding(top = 12.dp, bottom = 12.dp, start = 16.dp, end = 16.dp)
    } else {
        modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 12.dp, start = 16.dp, end = 16.dp)
    }

    // 设置文本透明度
    val textModifier = Modifier.alpha(if (enabled) 1.0f else 0.38f)

    // 创建行布局
    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 复选框
        Checkbox(
            enabled = enabled,
            checked = platform.selected,
            interactionSource = interactionSource,
            onCheckedChange = { onClickEvent.invoke(platform) }
        )

        // 标题和描述列
        Column(horizontalAlignment = Alignment.Start) {
            // 标题文本
            Text(
                text = title,
                modifier = textModifier,
                style = MaterialTheme.typography.titleMedium
            )
            // 描述文本（如果有）
            description?.let {
                Text(
                    text = it,
                    modifier = textModifier,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
