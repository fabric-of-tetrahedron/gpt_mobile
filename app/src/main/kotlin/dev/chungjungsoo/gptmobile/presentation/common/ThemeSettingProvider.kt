package dev.chungjungsoo.gptmobile.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.hilt.navigation.compose.hiltViewModel
import dev.chungjungsoo.gptmobile.data.model.DynamicTheme
import dev.chungjungsoo.gptmobile.data.model.ThemeMode
import dev.chungjungsoo.gptmobile.util.collectManagedState

/**
 * 用于提供动态主题设置的CompositionLocal
 * 默认值为DynamicTheme.OFF
 */
val LocalDynamicTheme = compositionLocalOf { DynamicTheme.OFF }

/**
 * 用于提供主题模式设置的CompositionLocal
 * 默认值为ThemeMode.SYSTEM
 */
val LocalThemeMode = compositionLocalOf { ThemeMode.SYSTEM }

/**
 * 用于提供ThemeViewModel的CompositionLocal
 * 如果未设置，将抛出错误
 */
val LocalThemeViewModel = compositionLocalOf<ThemeViewModel> {
    error("CompositionLocal LocalThemeViewModel is not present")
}

/**
 * 主题设置提供者组件
 *
 * @param themeViewModel 主题视图模型，默认通过hiltViewModel()获取
 * @param content 需要应用主题设置的内容
 */
@Composable
fun ThemeSettingProvider(
    themeViewModel: ThemeViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    // 收集主题设置状态
    themeViewModel.themeSetting.collectManagedState().value.run {
        // 使用CompositionLocalProvider提供主题相关的值
        CompositionLocalProvider(
            LocalThemeViewModel provides themeViewModel,
            LocalDynamicTheme provides dynamicTheme,
            LocalThemeMode provides themeMode,
            content = content
        )
    }
}
