package dev.chungjungsoo.gptmobile.data.dto.anthropic.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 消息角色枚举类，用于表示消息的发送者类型
 */
@Serializable
enum class MessageRole {

    /** 用户角色，表示消息由用户发送 */
    @SerialName("user")
    USER,

    /** 助手角色，表示消息由AI助手发送 */
    @SerialName("assistant")
    ASSISTANT
}
