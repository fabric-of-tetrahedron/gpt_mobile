package dev.chungjungsoo.gptmobile.data.dto.anthropic.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 表示内容块开始的响应块
 *
 * @property index 内容块的索引
 * @property contentBlock 内容块的详细信息
 */
@Serializable
@SerialName("content_block_start")
data class ContentStartResponseChunk(

    /** 内容块的索引 */
    @SerialName("index")
    val index: Int,

    /** 内容块的详细信息 */
    @SerialName("content_block")
    val contentBlock: ContentBlock
) : MessageResponseChunk() // 继承自MessageResponseChunk类
