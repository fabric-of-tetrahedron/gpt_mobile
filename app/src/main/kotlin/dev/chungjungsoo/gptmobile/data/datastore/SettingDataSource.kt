package dev.chungjungsoo.gptmobile.data.datastore

import dev.chungjungsoo.gptmobile.data.model.ApiType
import dev.chungjungsoo.gptmobile.data.model.DynamicTheme
import dev.chungjungsoo.gptmobile.data.model.ThemeMode

/**
 * 设置数据源接口，定义了各种设置操作的方法
 */
interface SettingDataSource {
    /** 更新动态主题 */
    suspend fun updateDynamicTheme(theme: DynamicTheme)

    /** 更新主题模式 */
    suspend fun updateThemeMode(themeMode: ThemeMode)

    /** 更新指定API类型的状态 */
    suspend fun updateStatus(apiType: ApiType, status: Boolean)

    /** 更新指定API类型的URL */
    suspend fun updateAPIUrl(apiType: ApiType, url: String)

    /** 更新指定API类型的令牌 */
    suspend fun updateToken(apiType: ApiType, token: String)

    /** 更新指定API类型的模型 */
    suspend fun updateModel(apiType: ApiType, model: String)

    /** 更新指定API类型的温度参数 */
    suspend fun updateTemperature(apiType: ApiType, temperature: Float)

    /** 更新指定API类型的top_p参数 */
    suspend fun updateTopP(apiType: ApiType, topP: Float)

    /** 更新指定API类型的系统提示 */
    suspend fun updateSystemPrompt(apiType: ApiType, prompt: String)

    /** 获取当前的动态主题设置 */
    suspend fun getDynamicTheme(): DynamicTheme?

    /** 获取当前的主题模式设置 */
    suspend fun getThemeMode(): ThemeMode?

    /** 获取指定API类型的状态 */
    suspend fun getStatus(apiType: ApiType): Boolean?

    /** 获取指定API类型的URL */
    suspend fun getAPIUrl(apiType: ApiType): String?

    /** 获取指定API类型的令牌 */
    suspend fun getToken(apiType: ApiType): String?

    /** 获取指定API类型的模型 */
    suspend fun getModel(apiType: ApiType): String?

    /** 获取指定API类型的温度参数 */
    suspend fun getTemperature(apiType: ApiType): Float?

    /** 获取指定API类型的top_p参数 */
    suspend fun getTopP(apiType: ApiType): Float?

    /** 获取指定API类型的系统提示 */
    suspend fun getSystemPrompt(apiType: ApiType): String?
}
