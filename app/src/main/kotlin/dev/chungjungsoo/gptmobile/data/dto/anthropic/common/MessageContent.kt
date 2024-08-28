package dev.chungjungsoo.gptmobile.data.dto.anthropic.common

import kotlinx.serialization.Serializable

/**
 * 消息内容的密封类，用于表示不同类型的消息内容
 */
@Serializable
sealed class MessageContent {
    // 这个密封类将被用作基类，其子类将代表不同类型的消息内容
    // 使用@Serializable注解以支持序列化和反序列化
}
