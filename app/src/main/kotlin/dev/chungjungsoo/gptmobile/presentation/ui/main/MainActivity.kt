package dev.chungjungsoo.gptmobile.presentation.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.chungjungsoo.gptmobile.presentation.common.LocalDynamicTheme
import dev.chungjungsoo.gptmobile.presentation.common.LocalThemeMode
import dev.chungjungsoo.gptmobile.presentation.common.Route
import dev.chungjungsoo.gptmobile.presentation.common.SetupNavGraph
import dev.chungjungsoo.gptmobile.presentation.common.ThemeSettingProvider
import dev.chungjungsoo.gptmobile.presentation.theme.GPTMobileTheme
import kotlinx.coroutines.launch

/**
 * MainActivity 是应用程序的主要入口点。
 * 它负责设置用户界面、处理主题设置和导航。
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // 使用 ViewModel 委托初始化 MainViewModel
    private val mainViewModel: MainViewModel by viewModels()

    /**
     * 在活动创建时调用此方法。
     * 它设置启动画面、边缘到边缘显示，并初始化用户界面。
     *
     * @param savedInstanceState 如果活动被重新初始化，则包含之前保存的状态；否则为null
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        // 安装并配置启动画面
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                !mainViewModel.isReady.value
            }
        }
        // 启用边缘到边缘显示
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // 设置界面内容
        setContent {
            // 创建导航控制器
            val navController = rememberNavController()
            // 检查现有设置
            navController.checkForExistingSettings()

            // 提供主题设置
            ThemeSettingProvider {
                // 应用 GPTMobile 主题
                GPTMobileTheme(
                    dynamicTheme = LocalDynamicTheme.current,
                    themeMode = LocalThemeMode.current
                ) {
                    // 设置导航图
                    SetupNavGraph(navController)
                }
            }
        }
    }

    /**
     * 检查是否存在现有设置，并在必要时导航到介绍页面。
     *
     * 此方法扩展了 NavHostController，用于处理应用程序的初始导航逻辑。
     */
    private fun NavHostController.checkForExistingSettings() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                // 收集 ViewModel 中的事件
                mainViewModel.event.collect { event ->
                    if (event == MainViewModel.SplashEvent.OpenIntro) {
                        // 如果需要打开介绍页面，则导航到 GET_STARTED 路由
                        navigate(Route.GET_STARTED) {
                            // 从导航栈中移除 CHAT_LIST 路由
                            popUpTo(Route.CHAT_LIST) { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}
