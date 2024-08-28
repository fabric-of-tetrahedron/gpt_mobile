package dev.chungjungsoo.gptmobile.data.dto.anthropic.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 停止原因增量数据类
 *
 * 该类表示Anthropic API响应中的停止原因增量信息。
 * 它包含了导致生成停止的原因，以及可能的停止序列。
 */
@Serializable
data class StopReasonDelta(

    /**
     * 停止原因
     *
     * 表示生成过程停止的具体原因。
     */
    @SerialName("stop_reason")
    val stopReason: StopReason,

    /**
     * 停止序列
     *
     * 如果生成过程因遇到特定序列而停止，该字段将包含该序列。
     * 可能为null，表示没有特定的停止序列。
     */
    @SerialName("stop_sequence")
    val stopSequence: String? = null
)
