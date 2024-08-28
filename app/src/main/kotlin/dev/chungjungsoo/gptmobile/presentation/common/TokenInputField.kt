package dev.chungjungsoo.gptmobile.presentation.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.chungjungsoo.gptmobile.R

/**
 * 令牌输入字段组件
 *
 * @param modifier 修饰符
 * @param value 输入字段的当前值
 * @param onValueChange 值变化时的回调函数
 * @param keyboardOptions 键盘选项
 * @param onClearClick 清除按钮点击时的回调函数
 * @param label 输入字段的标签
 * @param helpLink 帮助链接URL
 */
@Preview
@Composable
fun TokenInputField(
    modifier: Modifier = Modifier,
    value: String = "",
    onValueChange: (String) -> Unit = { },
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    onClearClick: () -> Unit = { },
    label: String = "",
    helpLink: String = ""
) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 16.dp, start = 20.dp, end = 20.dp),
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(label)
        },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(), // 使用密码可视化转换，隐藏输入内容
        keyboardOptions = keyboardOptions,
        supportingText = {
            HelpText(helpLink) // 显示帮助文本
        },
        trailingIcon = {
            if (value.isNotBlank()) {
                IconButton(onClick = onClearClick) {
                    Icon(Icons.Outlined.Clear, contentDescription = stringResource(R.string.clear_token))
                }
            }
        }
    )
}

/**
 * 帮助文本组件
 *
 * @param helpLink 帮助链接URL
 */
@Preview
@Composable
fun HelpText(helpLink: String = "") {
    val uriHandler = LocalUriHandler.current
    val annotatedString = buildAnnotatedString {
        val str = stringResource(R.string.need_help)
        append(str)
        addStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.tertiary,
                textDecoration = TextDecoration.Underline
            ),
            0,
            str.length
        )
        addStringAnnotation(
            "URL",
            annotation = helpLink,
            start = 0,
            end = str.length
        )
    }

    ClickableText(
        text = annotatedString,
        onClick = {
            annotatedString
                .getStringAnnotations("URL", it, it)
                .firstOrNull()?.let { stringAnnotation ->
                    uriHandler.openUri(stringAnnotation.item) // 点击时打开帮助链接
                }
        }
    )
}
