package dev.chungjungsoo.gptmobile.data.repository

import dev.chungjungsoo.gptmobile.data.ModelConstants
import dev.chungjungsoo.gptmobile.data.datastore.SettingDataSource
import dev.chungjungsoo.gptmobile.data.dto.Platform
import dev.chungjungsoo.gptmobile.data.dto.ThemeSetting
import dev.chungjungsoo.gptmobile.data.model.ApiType
import dev.chungjungsoo.gptmobile.data.model.DynamicTheme
import dev.chungjungsoo.gptmobile.data.model.ThemeMode
import javax.inject.Inject

/**
 * 设置仓库实现类
 *
 * 该类实现了SettingRepository接口，负责管理应用程序的设置数据。
 * 它使用SettingDataSource来存储和检索设置信息。
 *
 * @property settingDataSource 设置数据源，用于访问和修改设置数据
 */
class SettingRepositoryImpl @Inject constructor(
    private val settingDataSource: SettingDataSource
) : SettingRepository {

    /**
     * 获取所有平台的设置信息
     *
     * @return 包含所有平台设置信息的列表
     */
    override suspend fun fetchPlatforms(): List<Platform> = ApiType.entries.map { apiType ->
        // 获取平台状态
        val status = settingDataSource.getStatus(apiType)
        // 根据API类型获取对应的API URL
        val apiUrl = when (apiType) {
            ApiType.OPENAI -> settingDataSource.getAPIUrl(apiType) ?: ModelConstants.OPENAI_API_URL
            ApiType.ANTHROPIC -> settingDataSource.getAPIUrl(apiType) ?: ModelConstants.ANTHROPIC_API_URL
            ApiType.GOOGLE -> settingDataSource.getAPIUrl(apiType) ?: ModelConstants.GOOGLE_API_URL
            ApiType.OLLAMA -> settingDataSource.getAPIUrl(apiType) ?: ModelConstants.OLLAMA_API_URL
        }
        // 获取平台的token
        val token = settingDataSource.getToken(apiType)
        // 获取平台的模型
        val model = settingDataSource.getModel(apiType)
        // 获取平台的temperature参数
        val temperature = settingDataSource.getTemperature(apiType)
        // 获取平台的topP参数
        val topP = settingDataSource.getTopP(apiType)
        // 根据API类型获取对应的系统提示词
        val systemPrompt = when (apiType) {
            ApiType.OPENAI -> settingDataSource.getSystemPrompt(ApiType.OPENAI) ?: ModelConstants.OPENAI_PROMPT
            ApiType.ANTHROPIC -> settingDataSource.getSystemPrompt(ApiType.ANTHROPIC) ?: ModelConstants.DEFAULT_PROMPT
            ApiType.GOOGLE -> settingDataSource.getSystemPrompt(ApiType.GOOGLE) ?: ModelConstants.DEFAULT_PROMPT
            ApiType.OLLAMA -> settingDataSource.getSystemPrompt(ApiType.OLLAMA) ?: ModelConstants.DEFAULT_PROMPT
        }

        // 创建并返回Platform对象
        Platform(
            name = apiType,
            enabled = status ?: false,
            apiUrl = apiUrl,
            token = token,
            model = model,
            temperature = temperature,
            topP = topP,
            systemPrompt = systemPrompt
        )
    }

    /**
     * 获取主题设置
     *
     * @return 包含动态主题和主题模式的ThemeSetting对象
     */
    override suspend fun fetchThemes(): ThemeSetting = ThemeSetting(
        dynamicTheme = settingDataSource.getDynamicTheme() ?: DynamicTheme.OFF,
        themeMode = settingDataSource.getThemeMode() ?: ThemeMode.SYSTEM
    )

    /**
     * 更新平台设置
     *
     * @param platforms 包含更新后的平台设置的列表
     */
    override suspend fun updatePlatforms(platforms: List<Platform>) {
        platforms.forEach { platform ->
            // 更新平台状态
            settingDataSource.updateStatus(platform.name, platform.enabled)
            // 更新API URL
            settingDataSource.updateAPIUrl(platform.name, platform.apiUrl)

            // 更新token（如果有）
            platform.token?.let { settingDataSource.updateToken(platform.name, it) }
            // 更新模型（如果有）
            platform.model?.let { settingDataSource.updateModel(platform.name, it) }
            // 更新temperature参数（如果有）
            platform.temperature?.let { settingDataSource.updateTemperature(platform.name, it) }
            // 更新topP参数（如果有）
            platform.topP?.let { settingDataSource.updateTopP(platform.name, it) }
            // 更新系统提示词（如果有）
            platform.systemPrompt?.let { settingDataSource.updateSystemPrompt(platform.name, it.trim()) }
        }
    }

    /**
     * 更新主题设置
     *
     * @param themeSetting 包含更新后的主题设置的ThemeSetting对象
     */
    override suspend fun updateThemes(themeSetting: ThemeSetting) {
        // 更新动态主题设置
        settingDataSource.updateDynamicTheme(themeSetting.dynamicTheme)
        // 更新主题模式设置
        settingDataSource.updateThemeMode(themeSetting.themeMode)
    }
}
