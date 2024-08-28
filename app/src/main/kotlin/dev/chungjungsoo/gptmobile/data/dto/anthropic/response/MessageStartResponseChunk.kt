package dev.chungjungsoo.gptmobile.data.dto.anthropic.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 表示消息开始响应块的数据类
 *
 * 这个类用于序列化和反序列化Anthropic API的消息开始响应块
 * @property message 包含消息响应的详细信息
 */
@Serializable
@SerialName("message_start")
data class MessageStartResponseChunk(

    // 消息响应的详细信息
    @SerialName("message")
    val message: MessageResponse
) : MessageResponseChunk()
