package dev.chungjungsoo.gptmobile.presentation.ui.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.halilibo.richtext.commonmark.CommonmarkAstNodeParser
import com.halilibo.richtext.commonmark.MarkdownParseOptions
import com.halilibo.richtext.markdown.BasicMarkdown
import com.halilibo.richtext.ui.material3.RichText
import dev.chungjungsoo.gptmobile.R
import dev.chungjungsoo.gptmobile.data.model.ApiType
import dev.chungjungsoo.gptmobile.presentation.theme.GPTMobileTheme
import dev.chungjungsoo.gptmobile.util.getPlatformAPIBrandText

/**
 * 用户聊天气泡组件
 *
 * @param modifier 修饰符
 * @param text 聊天内容文本
 */
@Composable
fun UserChatBubble(
    modifier: Modifier = Modifier,
    text: String
) {
    // 创建Markdown解析选项，禁用自动链接
    val markdownParseOptions = remember { MarkdownParseOptions(autolink = false) }
    // 创建Markdown解析器
    val parser = remember(markdownParseOptions) { CommonmarkAstNodeParser(markdownParseOptions) }
    // 解析文本内容为AST节点
    val astNode = remember(text) { parser.parse(text.trimIndent()) }
    // 定义卡片颜色
    val cardColor = CardColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        disabledContentColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.38f),
        disabledContainerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.38f)
    )

    // 创建卡片组件
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(32.dp),
        colors = cardColor
    ) {
        // 使用RichText显示Markdown内容
        RichText(modifier = Modifier.padding(16.dp)) {
            BasicMarkdown(astNode = astNode)
        }
    }
}

/**
 * 对手（AI）聊天气泡组件
 *
 * @param modifier 修饰符
 * @param canRetry 是否可以重试
 * @param isLoading 是否正在加载
 * @param isError 是否出错
 * @param apiType API类型
 * @param text 聊天内容文本
 * @param onCopyClick 复制按钮点击回调
 * @param onRetryClick 重试按钮点击回调
 */
@Composable
fun OpponentChatBubble(
    modifier: Modifier = Modifier,
    canRetry: Boolean,
    isLoading: Boolean,
    isError: Boolean = false,
    apiType: ApiType,
    text: String,
    onCopyClick: () -> Unit = {},
    onRetryClick: () -> Unit = {}
) {
    // 创建Markdown解析选项，禁用自动链接
    val markdownParseOptions = remember { MarkdownParseOptions(autolink = false) }
    // 创建Markdown解析器
    val parser = remember(markdownParseOptions) { CommonmarkAstNodeParser(markdownParseOptions) }
    // 解析文本内容为AST节点，如果正在加载则添加光标
    val astNode = remember(text) { parser.parse(text.trimIndent() + if (isLoading) "▊" else "") }
    // 定义卡片颜色
    val cardColor = CardColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        disabledContentColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.38f),
        disabledContainerColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.38f)
    )

    Column(modifier = modifier) {
        Column(horizontalAlignment = Alignment.End) {
            // 创建卡片组件
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = cardColor
            ) {
                // 使用RichText显示Markdown内容
                RichText(modifier = Modifier.padding(24.dp)) {
                    BasicMarkdown(astNode = astNode)
                }
                // 如果不在加载中，显示品牌文本
                if (!isLoading) {
                    BrandText(apiType)
                }
            }

            // 如果不在加载中，显示操作按钮
            if (!isLoading) {
                Row {
                    // 如果没有错误，显示复制按钮
                    if (!isError) {
                        AssistChip(
                            onClick = onCopyClick,
                            label = { Text(stringResource(R.string.copy_text)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_copy),
                                    contentDescription = stringResource(R.string.copy_text),
                                    modifier = Modifier.size(AssistChipDefaults.IconSize)
                                )
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    // 如果可以重试，显示重试按钮
                    if (canRetry) {
                        AssistChip(
                            onClick = onRetryClick,
                            label = { Text(stringResource(R.string.retry)) },
                            leadingIcon = {
                                Icon(
                                    Icons.Rounded.Refresh,
                                    contentDescription = stringResource(R.string.retry),
                                    modifier = Modifier.size(AssistChipDefaults.IconSize)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 品牌文本组件
 *
 * @param apiType API类型
 */
@Composable
private fun BrandText(apiType: ApiType) {
    Box(
        modifier = Modifier
            .padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterEnd),
            text = getPlatformAPIBrandText(apiType),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * 用户聊天气泡预览
 */
@Preview
@Composable
fun UserChatBubblePreview() {
    val sampleText = """
        How can I print hello world
        in Python?
    """.trimIndent()
    GPTMobileTheme {
        UserChatBubble(text = sampleText)
    }
}

/**
 * 对手（AI）聊天气泡预览
 */
@Preview
@Composable
fun OpponentChatBubblePreview() {
    val sampleText = """
        # Demo
    
        Emphasis, aka italics, with *asterisks* or _underscores_. Strong emphasis, aka bold, with **asterisks** or __underscores__. Combined emphasis with **asterisks and _underscores_**. [Links with two blocks, text in square-brackets, destination is in parentheses.](https://www.example.com). Inline `code` has `back-ticks around` it.
    
        1. First ordered list item
        2. Another item
            * Unordered sub-list.
        3. And another item.
            You can have properly indented paragraphs within list items. Notice the blank line above, and the leading spaces (at least one, but we'll use three here to also align the raw Markdown).
    
        * Unordered list can use asterisks
        - Or minuses
        + Or pluses
    """.trimIndent()
    GPTMobileTheme {
        OpponentChatBubble(
            text = sampleText,
            canRetry = true,
            isLoading = false,
            apiType = ApiType.OPENAI,
            onCopyClick = {},
            onRetryClick = {}
        )
    }
}
