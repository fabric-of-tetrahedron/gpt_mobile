package dev.chungjungsoo.gptmobile.data.dto.anthropic.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 内容块类型枚举
 *
 * 此枚举定义了内容块的不同类型，用于表示Anthropic API响应中的内容格式。
 */
@Serializable
enum class ContentBlockType {

    /**
     * 文本类型
     *
     * 表示完整的文本内容块。
     */
    @SerialName("text")
    TEXT,

    /**
     * 增量文本类型
     *
     * 表示部分或增量的文本内容块，通常用于流式传输或实时更新。
     */
    @SerialName("text_delta")
    DELTA
}
