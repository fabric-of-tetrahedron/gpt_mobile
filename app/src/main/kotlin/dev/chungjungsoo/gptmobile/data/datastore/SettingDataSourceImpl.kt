package dev.chungjungsoo.gptmobile.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import dev.chungjungsoo.gptmobile.data.model.ApiType
import dev.chungjungsoo.gptmobile.data.model.DynamicTheme
import dev.chungjungsoo.gptmobile.data.model.ThemeMode
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * 设置数据源实现类，用于管理应用的各种设置
 * @property dataStore 数据存储对象，用于持久化存储设置
 */
class SettingDataSourceImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingDataSource {
    // API状态映射，用于存储各API的启用状态
    private val apiStatusMap = mapOf(
        ApiType.OPENAI to booleanPreferencesKey("openai_status"),
        ApiType.ANTHROPIC to booleanPreferencesKey("anthropic_status"),
        ApiType.GOOGLE to booleanPreferencesKey("google_status"),
        ApiType.OLLAMA to booleanPreferencesKey("ollama_status")
    )

    // API URL映射，用于存储各API的URL
    private val apiUrlMap = mapOf(
        ApiType.OPENAI to stringPreferencesKey("openai_url"),
        ApiType.ANTHROPIC to stringPreferencesKey("anthropic_url"),
        ApiType.GOOGLE to stringPreferencesKey("google_url"),
        ApiType.OLLAMA to stringPreferencesKey("ollama_url")
    )

    // API令牌映射，用于存储各API的访问令牌
    private val apiTokenMap = mapOf(
        ApiType.OPENAI to stringPreferencesKey("openai_token"),
        ApiType.ANTHROPIC to stringPreferencesKey("anthropic_token"),
        ApiType.GOOGLE to stringPreferencesKey("google_token"),
        ApiType.OLLAMA to stringPreferencesKey("ollama_token")
    )

    // API模型映射，用于存储各API使用的模型
    private val apiModelMap = mapOf(
        ApiType.OPENAI to stringPreferencesKey("openai_model"),
        ApiType.ANTHROPIC to stringPreferencesKey("anthropic_model"),
        ApiType.GOOGLE to stringPreferencesKey("google_model"),
        ApiType.OLLAMA to stringPreferencesKey("ollama_model")
    )

    // API温度映射，用于存储各API的温度设置
    private val apiTemperatureMap = mapOf(
        ApiType.OPENAI to floatPreferencesKey("openai_temperature"),
        ApiType.ANTHROPIC to floatPreferencesKey("anthropic_temperature"),
        ApiType.GOOGLE to floatPreferencesKey("google_temperature"),
        ApiType.OLLAMA to floatPreferencesKey("ollama_temperature")
    )

    // API top_p映射，用于存储各API的top_p设置
    private val apiTopPMap = mapOf(
        ApiType.OPENAI to floatPreferencesKey("openai_top_p"),
        ApiType.ANTHROPIC to floatPreferencesKey("anthropic_top_p"),
        ApiType.GOOGLE to floatPreferencesKey("google_top_p"),
        ApiType.OLLAMA to floatPreferencesKey("ollama_top_p")
    )

    // API系统提示映射，用于存储各API的系统提示
    private val apiSystemPromptMap = mapOf(
        ApiType.OPENAI to stringPreferencesKey("openai_system_prompt"),
        ApiType.ANTHROPIC to stringPreferencesKey("anthropic_system_prompt"),
        ApiType.GOOGLE to stringPreferencesKey("google_system_prompt"),
        ApiType.OLLAMA to stringPreferencesKey("ollama_system_prompt")
    )

    // 动态主题和主题模式的键
    private val dynamicThemeKey = intPreferencesKey("dynamic_mode")
    private val themeModeKey = intPreferencesKey("theme_mode")

    /** 更新动态主题设置 */
    override suspend fun updateDynamicTheme(theme: DynamicTheme) {
        dataStore.edit { pref ->
            pref[dynamicThemeKey] = theme.ordinal
        }
    }

    /** 更新主题模式设置 */
    override suspend fun updateThemeMode(themeMode: ThemeMode) {
        dataStore.edit { pref ->
            pref[themeModeKey] = themeMode.ordinal
        }
    }

    /** 更新指定API的状态 */
    override suspend fun updateStatus(apiType: ApiType, status: Boolean) {
        dataStore.edit { pref ->
            pref[apiStatusMap[apiType]!!] = status
        }
    }

    /** 更新指定API的URL */
    override suspend fun updateAPIUrl(apiType: ApiType, url: String) {
        dataStore.edit { pref ->
            pref[apiUrlMap[apiType]!!] = url
        }
    }

    /** 更新指定API的令牌 */
    override suspend fun updateToken(apiType: ApiType, token: String) {
        dataStore.edit { pref ->
            pref[apiTokenMap[apiType]!!] = token
        }
    }

    /** 更新指定API的模型 */
    override suspend fun updateModel(apiType: ApiType, model: String) {
        dataStore.edit { pref ->
            pref[apiModelMap[apiType]!!] = model
        }
    }

    /** 更新指定API的温度设置 */
    override suspend fun updateTemperature(apiType: ApiType, temperature: Float) {
        dataStore.edit { pref ->
            pref[apiTemperatureMap[apiType]!!] = temperature
        }
    }

    /** 更新指定API的top_p设置 */
    override suspend fun updateTopP(apiType: ApiType, topP: Float) {
        dataStore.edit { pref ->
            pref[apiTopPMap[apiType]!!] = topP
        }
    }

    /** 更新指定API的系统提示 */
    override suspend fun updateSystemPrompt(apiType: ApiType, prompt: String) {
        dataStore.edit { pref ->
            pref[apiSystemPromptMap[apiType]!!] = prompt
        }
    }

    /** 获取当前的动态主题设置 */
    override suspend fun getDynamicTheme(): DynamicTheme? {
        val mode = dataStore.data.map { pref ->
            pref[dynamicThemeKey]
        }.first() ?: return null

        return DynamicTheme.getByValue(mode)
    }

    /** 获取当前的主题模式设置 */
    override suspend fun getThemeMode(): ThemeMode? {
        val mode = dataStore.data.map { pref ->
            pref[themeModeKey]
        }.first() ?: return null

        return ThemeMode.getByValue(mode)
    }

    /** 获取指定API的状态 */
    override suspend fun getStatus(apiType: ApiType): Boolean? = dataStore.data.map { pref ->
        pref[apiStatusMap[apiType]!!]
    }.first()

    /** 获取指定API的URL */
    override suspend fun getAPIUrl(apiType: ApiType): String? = dataStore.data.map { pref ->
        pref[apiUrlMap[apiType]!!]
    }.first()

    /** 获取指定API的令牌 */
    override suspend fun getToken(apiType: ApiType): String? = dataStore.data.map { pref ->
        pref[apiTokenMap[apiType]!!]
    }.first()

    /** 获取指定API的模型 */
    override suspend fun getModel(apiType: ApiType): String? = dataStore.data.map { pref ->
        pref[apiModelMap[apiType]!!]
    }.first()

    /** 获取指定API的温度设置 */
    override suspend fun getTemperature(apiType: ApiType): Float? = dataStore.data.map { pref ->
        pref[apiTemperatureMap[apiType]!!]
    }.first()

    /** 获取指定API的top_p设置 */
    override suspend fun getTopP(apiType: ApiType): Float? = dataStore.data.map { pref ->
        pref[apiTopPMap[apiType]!!]
    }.first()

    /** 获取指定API的系统提示 */
    override suspend fun getSystemPrompt(apiType: ApiType): String? = dataStore.data.map { pref ->
        pref[apiSystemPromptMap[apiType]!!]
    }.first()
}
