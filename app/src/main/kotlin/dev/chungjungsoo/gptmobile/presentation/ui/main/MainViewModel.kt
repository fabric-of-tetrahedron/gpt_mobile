package dev.chungjungsoo.gptmobile.presentation.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.chungjungsoo.gptmobile.data.repository.SettingRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 主视图模型类
 *
 * 该类负责管理应用程序的主要状态和事件，包括启动画面逻辑和设置初始化。
 *
 * @property settingRepository 设置仓库，用于获取平台设置信息
 */
@HiltViewModel
class MainViewModel @Inject constructor(private val settingRepository: SettingRepository) : ViewModel() {

    /**
     * 启动画面事件密封类
     *
     * 定义了可能发生的启动画面事件类型。
     */
    sealed class SplashEvent {
        /** 打开介绍页面事件 */
        data object OpenIntro : SplashEvent()
        /** 打开主页事件 */
        data object OpenHome : SplashEvent()
    }

    // 表示应用程序是否准备就绪的状态流
    private val _isReady: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    // 用于发送启动画面事件的共享流
    private val _event: MutableSharedFlow<SplashEvent> = MutableSharedFlow()
    val event: SharedFlow<SplashEvent> = _event.asSharedFlow()

    init {
        // 在初始化时检查平台设置并决定下一步操作
        viewModelScope.launch {
            val platforms = settingRepository.fetchPlatforms()

            if (platforms.all { it.token == null || it.model == null }) {
                // 如果所有平台都没有设置token或model，则初始化并打开介绍页面
                sendSplashEvent(SplashEvent.OpenIntro)
            } else {
                // 否则直接打开主页
                sendSplashEvent(SplashEvent.OpenHome)
            }

            // 设置应用程序为准备就绪状态
            setAsReady()
        }
    }

    /**
     * 发送启动画面事件
     *
     * @param event 要发送的启动画面事件
     */
    private suspend fun sendSplashEvent(event: SplashEvent) {
        _event.emit(event)
    }

    /**
     * 将应用程序状态设置为准备就绪
     */
    private fun setAsReady() {
        _isReady.update { true }
    }
}
