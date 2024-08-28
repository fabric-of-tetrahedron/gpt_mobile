package dev.chungjungsoo.gptmobile.data.repository

import dev.chungjungsoo.gptmobile.data.dto.Platform
import dev.chungjungsoo.gptmobile.data.dto.ThemeSetting

/**
 * 设置仓库接口
 *
 * 该接口定义了与应用程序设置相关的操作，包括平台和主题的获取和更新。
 */
interface SettingRepository {
    /**
     * 获取平台列表
     *
     * @return 返回平台列表
     */
    suspend fun fetchPlatforms(): List<Platform>

    /**
     * 获取主题设置
     *
     * @return 返回主题设置对象
     */
    suspend fun fetchThemes(): ThemeSetting

    /**
     * 更新平台列表
     *
     * @param platforms 要更新的平台列表
     */
    suspend fun updatePlatforms(platforms: List<Platform>)

    /**
     * 更新主题设置
     *
     * @param themeSetting 要更新的主题设置对象
     */
    suspend fun updateThemes(themeSetting: ThemeSetting)
}
