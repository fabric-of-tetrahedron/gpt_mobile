package dev.chungjungsoo.gptmobile.data.dto

/**
 * API模型数据类
 *
 * 此数据类用于表示API模型的基本信息
 *
 * @property name 模型名称
 * @property description 模型描述
 * @property aliasValue 模型别名值
 */
data class APIModel(
    // 模型名称
    val name: String,
    // 模型描述
    val description: String,
    // 模型别名值
    val aliasValue: String
)
