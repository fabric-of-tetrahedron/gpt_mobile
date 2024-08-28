package dev.chungjungsoo.gptmobile.data.dto

import dev.chungjungsoo.gptmobile.data.ModelConstants.getDefaultAPIUrl
import dev.chungjungsoo.gptmobile.data.model.ApiType

/**
 * 平台数据类
 *
 * 该类表示一个API平台的配置信息。
 *
 * @property name API类型，表示平台的名称
 * @property selected 是否被选中
 * @property enabled 是否启用
 * @property apiUrl API的URL地址
 * @property token API的访问令牌
 * @property model 使用的模型名称
 * @property temperature 温度参数，用于控制生成文本的随机性
 * @property topP Top-p采样参数，用于控制生成文本的多样性
 * @property systemPrompt 系统提示信息
 */
data class Platform(
    /** API类型，表示平台的名称 */
    val name: ApiType,
    /** 是否被选中 */
    val selected: Boolean = false,
    /** 是否启用 */
    val enabled: Boolean = false,
    /** API的URL地址，默认使用getDefaultAPIUrl函数获取 */
    val apiUrl: String = getDefaultAPIUrl(name),
    /** API的访问令牌，可为空 */
    val token: String? = null,
    /** 使用的模型名称，可为空 */
    val model: String? = null,
    /** 温度参数，用于控制生成文本的随机性，可为空 */
    val temperature: Float? = null,
    /** Top-p采样参数，用于控制生成文本的多样性，可为空 */
    val topP: Float? = null,
    /** 系统提示信息，可为空 */
    val systemPrompt: String? = null
)
