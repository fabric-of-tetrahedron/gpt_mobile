package dev.chungjungsoo.gptmobile.data.dto.anthropic.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 媒体类型枚举类，用于表示不同的图片格式
 */
@Serializable
enum class MediaType {

    /** JPEG 图片格式 */
    @SerialName("image/jpeg")
    JPEG,

    /** PNG 图片格式 */
    @SerialName("image/png")
    PNG,

    /** GIF 动图格式 */
    @SerialName("image/gif")
    GIF,

    /** WebP 图片格式 */
    @SerialName("image/webp")
    WEBP
}
