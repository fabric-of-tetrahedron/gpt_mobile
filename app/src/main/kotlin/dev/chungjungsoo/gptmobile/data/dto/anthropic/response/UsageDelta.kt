package dev.chungjungsoo.gptmobile.data.dto.anthropic.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * UsageDelta 数据类
 *
 * 该类用于表示Anthropic API响应中的使用量增量信息。
 * 它包含了输出令牌的数量，用于跟踪API调用的资源消耗。
 */
@Serializable
data class UsageDelta(

    /**
     * 输出令牌的数量
     *
     * 这个属性表示在API响应中生成的输出令牌数量。
     * 令牌是文本的基本单位，用于计算API的使用量。
     */
    @SerialName("output_tokens")
    val outputTokens: Int
)
