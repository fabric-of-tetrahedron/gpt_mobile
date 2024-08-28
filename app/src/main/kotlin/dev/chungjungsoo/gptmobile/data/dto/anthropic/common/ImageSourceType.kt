package dev.chungjungsoo.gptmobile.data.dto.anthropic.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 图像源类型枚举类
 * 用于表示图像数据的来源类型
 */
@Serializable
enum class ImageSourceType {

    /**
     * Base64 编码的图像数据
     * 使用 Base64 字符串表示的图像
     */
    @SerialName("base64")
    BASE64
}
