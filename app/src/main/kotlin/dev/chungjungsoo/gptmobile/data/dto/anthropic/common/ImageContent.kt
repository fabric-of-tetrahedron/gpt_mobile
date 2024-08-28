package dev.chungjungsoo.gptmobile.data.dto.anthropic.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * ImageContent 类表示图像内容
 *
 * 这个类是 MessageContent 的子类，用于表示消息中的图像内容。
 * 它使用 Kotlin 的序列化注解来指定序列化时的名称。
 */
@Serializable
@SerialName("image")
data class ImageContent(

    /**
     * 图像源
     *
     * 这个属性表示图像的来源，类型为 ImageSource。
     * 在序列化时，这个属性将被命名为 "source"。
     */
    @SerialName("source")
    val source: ImageSource
) : MessageContent()
