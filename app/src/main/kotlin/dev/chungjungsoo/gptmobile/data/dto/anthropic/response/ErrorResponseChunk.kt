package dev.chungjungsoo.gptmobile.data.dto.anthropic.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 表示Anthropic API错误响应的数据类
 *
 * 这个类用于序列化和反序列化API返回的错误信息
 */
@Serializable
@SerialName("error")
data class ErrorResponseChunk(

    /**
     * 错误详情
     *
     * 包含具体的错误信息，如错误类型、错误描述等
     */
    @SerialName("error")
    val error: ErrorDetail
) : MessageResponseChunk()
