package dev.chungjungsoo.gptmobile.presentation.common

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.chungjungsoo.gptmobile.R

/**
 * 自定义单选按钮项组件
 *
 * @param modifier 应用于整个组件的修饰符
 * @param value 单选按钮的值
 * @param selected 是否被选中
 * @param title 单选按钮的标题
 * @param description 单选按钮的描述（可选）
 * @param onSelected 选中时的回调函数
 */
@Preview
@Composable
fun RadioItem(
    modifier: Modifier = Modifier,
    value: String = stringResource(R.string.sample_item_title),
    selected: Boolean = false,
    title: String = stringResource(R.string.sample_item_title),
    description: String? = stringResource(R.string.sample_item_description),
    onSelected: (String) -> Unit = { }
) {
    // 创建一个可变的交互源
    val interactionSource = remember { MutableInteractionSource() }

    // 创建一个行布局
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = { onSelected(value) },
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                role = Role.RadioButton
            )
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 添加单选按钮
        RadioButton(
            selected = selected,
            onClick = null,
            interactionSource = interactionSource
        )

        // 创建一个列布局，包含标题和描述
        Column(
            modifier = Modifier.padding(start = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // 显示标题
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            // 如果有描述，则显示描述
            description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
