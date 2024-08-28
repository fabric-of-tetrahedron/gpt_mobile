package dev.chungjungsoo.gptmobile.presentation.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.chungjungsoo.gptmobile.data.ModelConstants
import dev.chungjungsoo.gptmobile.data.dto.Platform
import dev.chungjungsoo.gptmobile.data.model.ApiType
import dev.chungjungsoo.gptmobile.data.repository.SettingRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 设置视图模型
 *
 * 该类负责管理应用程序设置的状态和逻辑。
 * 它处理平台状态、对话框状态以及各种设置的更新。
 *
 * @property settingRepository 设置仓库，用于获取和更新设置数据
 */
@HiltViewModel
class SettingViewModel @Inject constructor(
    val settingRepository: SettingRepository
) : ViewModel() {

    // 平台状态流
    private val _platformState = MutableStateFlow(listOf<Platform>())
    val platformState: StateFlow<List<Platform>> = _platformState.asStateFlow()

    // 对话框状态流
    private val _dialogState = MutableStateFlow(DialogState())
    val dialogState: StateFlow<DialogState> = _dialogState.asStateFlow()

    init {
        fetchPlatformStatus()
    }

    /**
     * 切换API的启用状态
     *
     * @param apiType API类型
     */
    fun toggleAPI(apiType: ApiType) {
        val index = _platformState.value.indexOfFirst { it.name == apiType }

        if (index >= 0) {
            _platformState.update {
                it.mapIndexed { i, p ->
                    if (index == i) {
                        p.copy(enabled = p.enabled.not())
                    } else {
                        p
                    }
                }
            }
            viewModelScope.launch {
                settingRepository.updatePlatforms(_platformState.value)
            }
        }
    }

    /**
     * 保存平台设置
     */
    fun savePlatformSettings() {
        viewModelScope.launch {
            settingRepository.updatePlatforms(_platformState.value)
        }
    }

    /**
     * 更新API的URL
     *
     * @param apiType API类型
     * @param url 新的URL
     */
    fun updateURL(apiType: ApiType, url: String) {
        val index = _platformState.value.indexOfFirst { it.name == apiType }

        if (index >= 0) {
            _platformState.update {
                it.mapIndexed { i, p ->
                    if (index == i && url.isNotBlank()) {
                        p.copy(apiUrl = url)
                    } else {
                        p
                    }
                }
            }
        }
    }

    /**
     * 更新API的令牌
     *
     * @param apiType API类型
     * @param token 新的令牌
     */
    fun updateToken(apiType: ApiType, token: String) {
        val index = _platformState.value.indexOfFirst { it.name == apiType }

        if (index >= 0) {
            _platformState.update {
                it.mapIndexed { i, p ->
                    if (index == i && token.isNotBlank()) {
                        p.copy(token = token)
                    } else {
                        p
                    }
                }
            }
        }
    }

    /**
     * 更新API的模型
     *
     * @param apiType API类型
     * @param model 新的模型名称
     */
    fun updateModel(apiType: ApiType, model: String) {
        val index = _platformState.value.indexOfFirst { it.name == apiType }
        val models = when (apiType) {
            ApiType.OPENAI -> ModelConstants.openaiModels
            ApiType.ANTHROPIC -> ModelConstants.anthropicModels
            ApiType.GOOGLE -> ModelConstants.googleModels
            ApiType.OLLAMA -> ModelConstants.ollamaModels
        }

        if (index >= 0) {
            _platformState.update {
                it.mapIndexed { i, p ->
                    if (index == i && model in models) {
                        p.copy(model = model)
                    } else {
                        p
                    }
                }
            }
        }
    }

    /**
     * 更新API的温度设置
     *
     * @param apiType API类型
     * @param temperature 新的温度值
     */
    fun updateTemperature(apiType: ApiType, temperature: Float) {
        val index = _platformState.value.indexOfFirst { it.name == apiType }
        val modifiedTemperature = when (apiType) {
            ApiType.ANTHROPIC -> temperature.coerceIn(0F, 1F)
            else -> temperature.coerceIn(0F, 2F)
        }

        if (index >= 0) {
            _platformState.update {
                it.mapIndexed { i, p ->
                    if (index == i) {
                        p.copy(temperature = modifiedTemperature)
                    } else {
                        p
                    }
                }
            }
        }
    }

    /**
     * 更新API的top_p设置
     *
     * @param apiType API类型
     * @param topP 新的top_p值
     */
    fun updateTopP(apiType: ApiType, topP: Float) {
        val index = _platformState.value.indexOfFirst { it.name == apiType }
        val modifiedTopP = topP.coerceIn(0.1F, 1F)

        if (index >= 0) {
            _platformState.update {
                it.mapIndexed { i, p ->
                    if (index == i) {
                        p.copy(topP = modifiedTopP)
                    } else {
                        p
                    }
                }
            }
        }
    }

    /**
     * 更新API的系统提示
     *
     * @param apiType API类型
     * @param prompt 新的系统提示
     */
    fun updateSystemPrompt(apiType: ApiType, prompt: String) {
        val index = _platformState.value.indexOfFirst { it.name == apiType }

        if (index >= 0) {
            _platformState.update {
                it.mapIndexed { i, p ->
                    if (index == i && prompt.isNotBlank()) {
                        p.copy(systemPrompt = prompt)
                    } else {
                        p
                    }
                }
            }
        }
    }

    // 打开各种对话框的函数
    fun openThemeDialog() = _dialogState.update { it.copy(isThemeDialogOpen = true) }
    fun openApiUrlDialog() = _dialogState.update { it.copy(isApiUrlDialogOpen = true) }
    fun openApiTokenDialog() = _dialogState.update { it.copy(isApiTokenDialogOpen = true) }
    fun openApiModelDialog() = _dialogState.update { it.copy(isApiModelDialogOpen = true) }
    fun openTemperatureDialog() = _dialogState.update { it.copy(isTemperatureDialogOpen = true) }
    fun openTopPDialog() = _dialogState.update { it.copy(isTopPDialogOpen = true) }
    fun openSystemPromptDialog() = _dialogState.update { it.copy(isSystemPromptDialogOpen = true) }

    // 关闭各种对话框的函数
    fun closeThemeDialog() = _dialogState.update { it.copy(isThemeDialogOpen = false) }
    fun closeApiUrlDialog() = _dialogState.update { it.copy(isApiUrlDialogOpen = false) }
    fun closeApiTokenDialog() = _dialogState.update { it.copy(isApiTokenDialogOpen = false) }
    fun closeApiModelDialog() = _dialogState.update { it.copy(isApiModelDialogOpen = false) }
    fun closeTemperatureDialog() = _dialogState.update { it.copy(isTemperatureDialogOpen = false) }
    fun closeTopPDialog() = _dialogState.update { it.copy(isTopPDialogOpen = false) }
    fun closeSystemPromptDialog() = _dialogState.update { it.copy(isSystemPromptDialogOpen = false) }

    /**
     * 获取平台状态
     */
    private fun fetchPlatformStatus() {
        viewModelScope.launch {
            val platforms = settingRepository.fetchPlatforms()
            _platformState.update { platforms }
        }
    }

    /**
     * 对话框状态数据类
     *
     * @property isThemeDialogOpen 主题对话框是否打开
     * @property isApiUrlDialogOpen API URL对话框是否打开
     * @property isApiTokenDialogOpen API令牌对话框是否打开
     * @property isApiModelDialogOpen API模型对话框是否打开
     * @property isTemperatureDialogOpen 温度设置对话框是否打开
     * @property isTopPDialogOpen Top P设置对话框是否打开
     * @property isSystemPromptDialogOpen 系统提示对话框是否打开
     */
    data class DialogState(
        val isThemeDialogOpen: Boolean = false,
        val isApiUrlDialogOpen: Boolean = false,
        val isApiTokenDialogOpen: Boolean = false,
        val isApiModelDialogOpen: Boolean = false,
        val isTemperatureDialogOpen: Boolean = false,
        val isTopPDialogOpen: Boolean = false,
        val isSystemPromptDialogOpen: Boolean = false
    )
}
