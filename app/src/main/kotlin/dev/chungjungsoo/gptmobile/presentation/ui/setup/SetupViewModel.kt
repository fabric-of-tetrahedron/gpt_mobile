package dev.chungjungsoo.gptmobile.presentation.ui.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.chungjungsoo.gptmobile.data.ModelConstants.anthropicModels
import dev.chungjungsoo.gptmobile.data.ModelConstants.googleModels
import dev.chungjungsoo.gptmobile.data.ModelConstants.ollamaModels
import dev.chungjungsoo.gptmobile.data.ModelConstants.openaiModels
import dev.chungjungsoo.gptmobile.data.dto.Platform
import dev.chungjungsoo.gptmobile.data.model.ApiType
import dev.chungjungsoo.gptmobile.data.repository.SettingRepository
import dev.chungjungsoo.gptmobile.presentation.common.Route
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 设置视图模型
 *
 * 该类负责管理设置界面的状态和逻辑
 *
 * @property settingRepository 设置仓库，用于保存和获取设置
 */
@HiltViewModel
class SetupViewModel @Inject constructor(private val settingRepository: SettingRepository) : ViewModel() {

    // 平台状态的可变状态流
    private val _platformState = MutableStateFlow(
        listOf(
            Platform(ApiType.OPENAI),
            Platform(ApiType.ANTHROPIC),
            Platform(ApiType.GOOGLE),
            Platform(ApiType.OLLAMA)
        )
    )
    // 对外暴露的不可变平台状态流
    val platformState: StateFlow<List<Platform>> = _platformState.asStateFlow()

    /**
     * 更新平台的选中状态
     *
     * @param platform 要更新的平台
     */
    fun updateCheckedState(platform: Platform) {
        val index = _platformState.value.indexOf(platform)

        if (index >= 0) {
            _platformState.update {
                it.mapIndexed { i, p ->
                    if (index == i) {
                        p.copy(selected = p.selected.not())
                    } else {
                        p
                    }
                }
            }
        }
    }

    /**
     * 更新平台的令牌
     *
     * @param platform 要更新的平台
     * @param token 新的令牌值
     */
    fun updateToken(platform: Platform, token: String) {
        val index = _platformState.value.indexOf(platform)

        if (index >= 0) {
            _platformState.update {
                it.mapIndexed { i, p ->
                    if (index == i) {
                        p.copy(token = token.ifBlank { null })
                    } else {
                        p
                    }
                }
            }
        }
    }

    /**
     * 更新平台的模型
     *
     * @param apiType API类型
     * @param model 新的模型名称
     */
    fun updateModel(apiType: ApiType, model: String) {
        val index = _platformState.value.indexOfFirst { it.name == apiType }
        val models = when (apiType) {
            ApiType.OPENAI -> openaiModels
            ApiType.ANTHROPIC -> anthropicModels
            ApiType.GOOGLE -> googleModels
            ApiType.OLLAMA -> ollamaModels
        }

        if (index >= 0) {
            _platformState.update {
                it.mapIndexed { i, p ->
                    if (index == i) {
                        p.copy(model = if (model in models) model else null)
                    } else {
                        p
                    }
                }
            }
        }
    }

    /**
     * 保存平台状态
     */
    fun savePlatformState() {
        _platformState.update { platforms ->
            // 更新平台启用状态
            platforms.map { p ->
                p.copy(enabled = p.selected, selected = false)
            }
        }
        viewModelScope.launch {
            settingRepository.updatePlatforms(_platformState.value)
        }
    }

    /**
     * 获取下一个设置路由
     *
     * @param currentRoute 当前路由
     * @return 下一个路由
     */
    fun getNextSetupRoute(currentRoute: String?): String {
        val steps = listOf(
            Route.SELECT_PLATFORM,
            Route.TOKEN_INPUT,
            Route.OPENAI_MODEL_SELECT,
            Route.ANTHROPIC_MODEL_SELECT,
            Route.GOOGLE_MODEL_SELECT,
            Route.SETUP_COMPLETE
        )
        val commonSteps = setOf(Route.SELECT_PLATFORM, Route.TOKEN_INPUT, Route.SETUP_COMPLETE)
        val platformStep = mapOf(
            Route.OPENAI_MODEL_SELECT to ApiType.OPENAI,
            Route.ANTHROPIC_MODEL_SELECT to ApiType.ANTHROPIC,
            Route.GOOGLE_MODEL_SELECT to ApiType.GOOGLE,
            Route.OLLAMA_MODEL_SELECT to ApiType.OLLAMA
        )

        val currentIndex = steps.indexOfFirst { it == currentRoute }
        val enabledPlatform = platformState.value.filter { it.selected }.map { it.name }.toSet()
        val remainingSteps = steps.filterIndexed { index, setupStep ->
            index > currentIndex &&
                (setupStep in commonSteps || platformStep[setupStep] in enabledPlatform)
        }

        if (remainingSteps.isEmpty()) {
            // 设置完成
            return Route.CHAT_LIST
        }

        return remainingSteps.first()
    }

    /**
     * 设置默认模型
     *
     * @param apiType API类型
     * @param defaultModelIndex 默认模型索引
     * @return 设置的模型名称
     */
    fun setDefaultModel(apiType: ApiType, defaultModelIndex: Int): String {
        val modelList = when (apiType) {
            ApiType.OPENAI -> openaiModels
            ApiType.ANTHROPIC -> anthropicModels
            ApiType.GOOGLE -> googleModels
            ApiType.OLLAMA -> ollamaModels
        }.toList()

        val model = modelList[defaultModelIndex]
        updateModel(apiType, model)

        return model
    }
}
