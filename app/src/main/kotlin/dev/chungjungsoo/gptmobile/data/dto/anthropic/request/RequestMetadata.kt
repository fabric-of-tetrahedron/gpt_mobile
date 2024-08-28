package dev.chungjungsoo.gptmobile.data.dto.anthropic.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 请求元数据类
 *
 * 这个数据类用于表示Anthropic API请求中的元数据信息。
 * 它包含了可选的用户ID字段，用于在API请求中标识用户。
 *
 * @property userId 用户ID，可为null
 */
@Serializable
data class RequestMetadata(
    /** 用户ID字段，使用@SerialName注解指定JSON序列化时的名称为"user_id" */
    @SerialName("user_id")
    val userId: String? = null
)
