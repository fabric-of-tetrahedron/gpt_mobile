package dev.chungjungsoo.gptmobile.data.dto.anthropic.response

import kotlinx.serialization.Serializable

/**
 * 消息响应块
 *
 * 这个密封类代表了来自Anthropic API的消息响应块。
 * 它是一个基类，用于表示不同类型的消息响应块。
 *
 * @property MessageResponseChunk 消息响应块的基类
 */
@Serializable
sealed class MessageResponseChunk
