package dev.chungjungsoo.gptmobile.data.dto.anthropic.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 内容块数据类
 *
 * 这个数据类代表了一个内容块，通常用于表示消息或对话中的一个部分。
 * 它包含了内容的类型和实际的文本内容。
 *
 * @property type 内容块的类型，使用 [ContentBlockType] 枚举来表示
 * @property text 内容块的实际文本内容
 */
@Serializable
data class ContentBlock(

    /**
     * 内容块的类型
     *
     * 这个属性使用 [ContentBlockType] 枚举来表示内容块的类型。
     * 例如，它可能是文本、图像或其他类型的内容。
     */
    @SerialName("type")
    val type: ContentBlockType,

    /**
     * 内容块的文本内容
     *
     * 这个属性包含了内容块的实际文本。
     * 根据 [type] 的不同，这个文本的解释可能会有所不同。
     */
    @SerialName("text")
    val text: String
)
