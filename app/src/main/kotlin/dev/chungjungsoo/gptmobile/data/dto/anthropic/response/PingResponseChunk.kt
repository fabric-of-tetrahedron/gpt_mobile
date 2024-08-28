package dev.chungjungsoo.gptmobile.data.dto.anthropic.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 表示Anthropic API的ping响应块
 *
 * 这个数据对象用于表示从Anthropic API接收到的ping响应块。
 * 它继承自MessageResponseChunk类，专门用于处理ping类型的响应。
 *
 * @property PingResponseChunk 一个单例对象，代表ping响应块
 */
@Serializable
@SerialName("ping")
data object PingResponseChunk : MessageResponseChunk()
