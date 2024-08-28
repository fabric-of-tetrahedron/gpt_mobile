package dev.chungjungsoo.gptmobile.data.dto.anthropic.request

import dev.chungjungsoo.gptmobile.data.dto.anthropic.common.MessageContent
import dev.chungjungsoo.gptmobile.data.dto.anthropic.common.MessageRole
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** 输入消息数据类，用于表示Anthropic API请求中的消息结构 */
@Serializable
data class InputMessage(
    /** 消息的角色，例如用户或系统 */
    @SerialName("role")
    val role: MessageRole,

    /** 消息的内容，可能包含文本、图像等多种类型 */
    @SerialName("content")
    val content: List<MessageContent>
)
