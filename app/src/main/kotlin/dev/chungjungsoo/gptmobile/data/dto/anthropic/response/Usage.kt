package dev.chungjungsoo.gptmobile.data.dto.anthropic.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 表示Anthropic API使用情况的数据类
 *
 * @property inputTokens 输入的令牌数量
 * @property outputTokens 输出的令牌数量
 */
@Serializable
data class Usage(
    // 输入的令牌数量
    @SerialName("input_tokens")
    val inputTokens: Int,

    // 输出的令牌数量
    @SerialName("output_tokens")
    val outputTokens: Int
)
