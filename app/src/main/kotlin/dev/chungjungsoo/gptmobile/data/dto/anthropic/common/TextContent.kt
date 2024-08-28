package dev.chungjungsoo.gptmobile.data.dto.anthropic.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** 表示文本内容的数据类 */
@Serializable
@SerialName("text")
data class TextContent(

    /** 文本内容 */
    @SerialName("text")
    val text: String
) : MessageContent()
