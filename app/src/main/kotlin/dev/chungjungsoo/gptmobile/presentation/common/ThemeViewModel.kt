package dev.chungjungsoo.gptmobile.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.chungjungsoo.gptmobile.data.dto.ThemeSetting
import dev.chungjungsoo.gptmobile.data.model.DynamicTheme
import dev.chungjungsoo.gptmobile.data.model.ThemeMode
import dev.chungjungsoo.gptmobile.data.repository.SettingRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 主题视图模型
 *
 * 该类负责管理应用的主题设置，包括动态主题和主题模式。
 * 它使用 Hilt 进行依赖注入，并继承自 ViewModel。
 *
 * @property settingRepository 设置仓库，用于获取和更新主题设置
 */
@HiltViewModel
class ThemeViewModel @Inject constructor(private val settingRepository: SettingRepository) : ViewModel() {

    // 私有的可变状态流，用于存储主题设置
    private val _themeSetting = MutableStateFlow(ThemeSetting())
    // 公开的不可变状态流，用于观察主题设置
    val themeSetting = _themeSetting.asStateFlow()

    /**
     * 初始化块
     * 在 ViewModel 创建时获取主题设置
     */
    init {
        fetchThemes()
    }

    /**
     * 获取主题设置
     *
     * 从设置仓库获取主题设置并更新状态流
     */
    private fun fetchThemes() {
        viewModelScope.launch {
            _themeSetting.update { settingRepository.fetchThemes() }
        }
    }

    /**
     * 更新动态主题
     *
     * @param theme 新的动态主题
     */
    fun updateDynamicTheme(theme: DynamicTheme) {
        // 更新状态流中的动态主题
        _themeSetting.update { setting ->
            setting.copy(dynamicTheme = theme)
        }
        // 在协程中更新设置仓库
        viewModelScope.launch {
            settingRepository.updateThemes(_themeSetting.value)
        }
    }

    /**
     * 更新主题模式
     *
     * @param theme 新的主题模式
     */
    fun updateThemeMode(theme: ThemeMode) {
        // 更新状态流中的主题模式
        _themeSetting.update { setting ->
            setting.copy(themeMode = theme)
        }
        // 在协程中更新设置仓库
        viewModelScope.launch {
            settingRepository.updateThemes(_themeSetting.value)
        }
    }
}
