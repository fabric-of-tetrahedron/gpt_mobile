package dev.chungjungsoo.gptmobile.data.dto.anthropic.response

import dev.chungjungsoo.gptmobile.data.dto.anthropic.common.MessageRole
import dev.chungjungsoo.gptmobile.data.dto.anthropic.common.TextContent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 消息响应数据类
 *
 * 该类表示从Anthropic API接收到的消息响应。
 * 它包含了消息的各种属性，如ID、类型、角色、内容等。
 */
@Serializable
data class MessageResponse(
    /**
     * 消息的唯一标识符
     */
    @SerialName("id")
    val id: String,

    /**
     * 消息的类型，默认为"message"
     */
    @SerialName("type")
    val type: String = "message",

    /**
     * 消息的角色，默认为助手（ASSISTANT）
     */
    @SerialName("role")
    val role: MessageRole = MessageRole.ASSISTANT,

    /**
     * 消息的内容，以TextContent列表形式存储
     */
    @SerialName("content")
    val content: List<TextContent>,

    /**
     * 用于生成此消息的模型名称
     */
    @SerialName("model")
    val model: String,

    /**
     * 消息生成停止的原因，可为空
     */
    @SerialName("stop_reason")
    val stopReason: StopReason? = null,

    /**
     * 导致消息生成停止的序列，可为空
     */
    @SerialName("stop_sequence")
    val stopSequence: String? = null,

    /**
     * 消息生成过程中的资源使用情况
     */
    @SerialName("usage")
    val usage: Usage
)
