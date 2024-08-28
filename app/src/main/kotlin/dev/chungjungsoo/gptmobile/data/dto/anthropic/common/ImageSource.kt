package dev.chungjungsoo.gptmobile.data.dto.anthropic.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 表示图像源的数据类
 *
 * @property type 图像源的类型
 * @property mediaType 图像的媒体类型
 * @property data 图像数据（通常是Base64编码的字符串）
 */
@Serializable
data class ImageSource(
    /** 图像源的类型，例如上传的图片或生成的图片 */
    @SerialName("type")
    val type: ImageSourceType,

    /** 图像的媒体类型，如JPEG、PNG等 */
    @SerialName("media_type")
    val mediaType: MediaType,

    /** 图像数据，通常是Base64编码的字符串 */
    @SerialName("data")
    val data: String
)
