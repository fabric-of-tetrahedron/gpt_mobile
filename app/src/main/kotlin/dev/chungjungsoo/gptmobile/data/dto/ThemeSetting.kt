package dev.chungjungsoo.gptmobile.data.dto

import dev.chungjungsoo.gptmobile.data.model.DynamicTheme
import dev.chungjungsoo.gptmobile.data.model.ThemeMode

/**
 * 主题设置数据类
 *
 * @property dynamicTheme 动态主题设置，默认为关闭
 * @property themeMode 主题模式设置，默认为跟随系统
 */
data class ThemeSetting(
    /** 动态主题设置，默认为关闭 */
    val dynamicTheme: DynamicTheme = DynamicTheme.OFF,
    /** 主题模式设置，默认为跟随系统 */
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)
