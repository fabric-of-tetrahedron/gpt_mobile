package dev.chungjungsoo.gptmobile.data.dto.anthropic.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 错误详情数据类
 *
 * 该类用于表示Anthropic API响应中的错误详情。
 * 它包含错误类型和错误消息两个属性。
 */
@Serializable
data class ErrorDetail(

    /**
     * 错误类型
     *
     * 表示错误的分类或类型。
     */
    @SerialName("type")
    val type: String,

    /**
     * 错误消息
     *
     * 包含关于错误的详细描述或解释。
     */
    @SerialName("message")
    val message: String
)
