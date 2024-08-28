package dev.chungjungsoo.gptmobile.data.dto.anthropic.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 消息增量响应块
 *
 * 这个数据类表示Anthropic API返回的消息增量响应块。
 * 它包含了消息的增量更新信息，如停止原因和使用情况。
 *
 * @property delta 停止原因的增量信息
 * @property usage 使用情况的增量信息
 */
@Serializable
@SerialName("message_delta")
data class MessageDeltaResponseChunk(

    // 停止原因的增量信息
    @SerialName("delta")
    val delta: StopReasonDelta,

    // 使用情况的增量信息
    @SerialName("usage")
    val usage: UsageDelta
) : MessageResponseChunk()
