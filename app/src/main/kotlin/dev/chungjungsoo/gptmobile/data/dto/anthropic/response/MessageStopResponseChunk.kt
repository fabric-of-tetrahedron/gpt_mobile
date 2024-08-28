package dev.chungjungsoo.gptmobile.data.dto.anthropic.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 表示消息停止响应块的数据类
 *
 * 这个类用于表示Anthropic API返回的消息停止响应块。
 * 当API完成消息生成并停止时，会返回这种类型的响应块。
 */
@Serializable
@SerialName("message_stop")
data object MessageStopResponseChunk : MessageResponseChunk()
