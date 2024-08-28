package dev.chungjungsoo.gptmobile.data.dto.anthropic.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 内容增量响应块
 *
 * 这个数据类表示Anthropic API返回的内容增量响应块。
 * 它包含了消息内容的部分更新信息。
 */
@Serializable
@SerialName("content_block_delta")
data class ContentDeltaResponseChunk(

    /**
     * 索引
     *
     * 表示此内容块在整个消息中的位置。
     */
    @SerialName("index")
    val index: Int,

    /**
     * 增量内容
     *
     * 包含此次更新的实际内容。
     */
    @SerialName("delta")
    val delta: ContentBlock
) : MessageResponseChunk()
