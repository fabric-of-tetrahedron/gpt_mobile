package dev.chungjungsoo.gptmobile.data.dto.anthropic.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 内容停止响应块
 *
 * 这个数据类表示一个内容块停止的响应。当AI生成的内容块结束时，会返回这种类型的响应。
 *
 * @property index 内容块的索引，表示停止的是第几个内容块
 */
@Serializable
@SerialName("content_block_stop")
data class ContentStopResponseChunk(

    /**
     * 内容块的索引
     *
     * 这个属性表示停止的内容块在整个响应中的位置。
     * 例如，如果index为2，则表示第3个内容块已停止（因为索引从0开始）。
     */
    @SerialName("index")
    val index: Int
) : MessageResponseChunk()
