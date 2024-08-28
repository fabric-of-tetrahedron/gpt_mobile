package dev.chungjungsoo.gptmobile.data.dto.anthropic.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 内容类型枚举类
 * 用于表示不同类型的内容，如文本或图像
 */
@Serializable
enum class ContentType {

    /**
     * 文本类型
     * 用于表示纯文本内容
     */
    @SerialName("text")
    TEXT,

    /**
     * 图像类型
     * 用于表示图片内容
     */
    @SerialName("image")
    IMAGE
}
