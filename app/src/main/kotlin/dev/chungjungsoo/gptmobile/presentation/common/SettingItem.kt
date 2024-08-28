package dev.chungjungsoo.gptmobile.presentation.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.chungjungsoo.gptmobile.R

/**
 * 设置项组件
 *
 * @param modifier 修饰符
 * @param title 设置项标题
 * @param description 设置项描述（可选）
 * @param enabled 是否启用点击
 * @param onItemClick 点击事件回调
 * @param showTrailingIcon 是否显示尾部图标
 * @param showLeadingIcon 是否显示前导图标
 * @param leadingIcon 前导图标组件（可选）
 */
@Composable
fun SettingItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String? = null,
    enabled: Boolean = true,
    onItemClick: () -> Unit,
    showTrailingIcon: Boolean,
    showLeadingIcon: Boolean,
    leadingIcon: @Composable () -> Unit? = {}
) {
    // 根据是否启用设置不同的修饰符
    val clickableModifier = if (enabled) {
        modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick)
            .padding(horizontal = 8.dp)
    } else {
        modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    }
    // 获取默认颜色
    val colors = ListItemDefaults.colors()

    if (showLeadingIcon) {
        // 显示前导图标的ListItem
        ListItem(
            modifier = clickableModifier,
            headlineContent = { Text(title, overflow = TextOverflow.Ellipsis) },
            supportingContent = {
                description?.let { Text(it, overflow = TextOverflow.Ellipsis) }
            },
            leadingContent = { leadingIcon() },
            trailingContent = {
                if (showTrailingIcon) {
                    Icon(
                        ImageVector.vectorResource(id = R.drawable.ic_round_arrow_right),
                        contentDescription = stringResource(R.string.arrow_icon)
                    )
                }
            },
            colors = ListItemDefaults.colors(
                headlineColor = if (enabled) colors.headlineColor else colors.disabledHeadlineColor,
                supportingColor = if (enabled) colors.supportingTextColor else colors.disabledHeadlineColor,
                trailingIconColor = if (enabled) colors.trailingIconColor else colors.disabledTrailingIconColor
            )
        )
    } else {
        // 不显示前导图标的ListItem
        ListItem(
            modifier = clickableModifier,
            headlineContent = { Text(title) },
            supportingContent = {
                description?.let { Text(it) }
            },
            trailingContent = {
                if (showTrailingIcon) {
                    Icon(
                        ImageVector.vectorResource(id = R.drawable.ic_round_arrow_right),
                        contentDescription = stringResource(R.string.arrow_icon)
                    )
                }
            },
            colors = ListItemDefaults.colors(
                headlineColor = if (enabled) colors.headlineColor else colors.disabledHeadlineColor,
                supportingColor = if (enabled) colors.supportingTextColor else colors.disabledHeadlineColor,
                trailingIconColor = if (enabled) colors.trailingIconColor else colors.disabledTrailingIconColor
            )
        )
    }
}
