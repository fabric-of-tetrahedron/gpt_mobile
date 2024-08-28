package dev.chungjungsoo.gptmobile.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.chungjungsoo.gptmobile.data.model.ApiType
import dev.chungjungsoo.gptmobile.presentation.ui.chat.ChatScreen
import dev.chungjungsoo.gptmobile.presentation.ui.home.HomeScreen
import dev.chungjungsoo.gptmobile.presentation.ui.setting.*
import dev.chungjungsoo.gptmobile.presentation.ui.setup.*
import dev.chungjungsoo.gptmobile.presentation.ui.startscreen.StartScreen

/**
 * 设置导航图
 *
 * @param navController 导航控制器
 */
@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        navController = navController,
        startDestination = Route.CHAT_LIST
    ) {
        // 添加各个导航目标
        homeScreenNavigation(navController)
        startScreenNavigation(navController)
        setupNavigation(navController)
        settingNavigation(navController)
        chatScreenNavigation(navController)
    }
}

/**
 * 开始屏幕导航
 *
 * @param navController 导航控制器
 */
fun NavGraphBuilder.startScreenNavigation(navController: NavHostController) {
    composable(Route.GET_STARTED) {
        StartScreen { navController.navigate(Route.SETUP_ROUTE) }
    }
}

/**
 * 设置导航
 *
 * @param navController 导航控制器
 */
fun NavGraphBuilder.setupNavigation(
    navController: NavHostController
) {
    navigation(startDestination = Route.SELECT_PLATFORM, route = Route.SETUP_ROUTE) {
        // 选择平台界面
        composable(route = Route.SELECT_PLATFORM) {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(Route.SETUP_ROUTE)
            }
            val setupViewModel: SetupViewModel = hiltViewModel(parentEntry)
            SelectPlatformScreen(
                setupViewModel = setupViewModel,
                onNavigate = { route -> navController.navigate(route) },
                onBackAction = { navController.navigateUp() }
            )
        }

        // 输入令牌界面
        composable(route = Route.TOKEN_INPUT) {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(Route.SETUP_ROUTE)
            }
            val setupViewModel: SetupViewModel = hiltViewModel(parentEntry)
            TokenInputScreen(
                setupViewModel = setupViewModel,
                onNavigate = { route -> navController.navigate(route) },
                onBackAction = { navController.navigateUp() }
            )
        }

        // OpenAI模型选择界面
        composable(route = Route.OPENAI_MODEL_SELECT) {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(Route.SETUP_ROUTE)
            }
            val setupViewModel: SetupViewModel = hiltViewModel(parentEntry)
            SelectModelScreen(
                setupViewModel = setupViewModel,
                currentRoute = Route.OPENAI_MODEL_SELECT,
                platformType = ApiType.OPENAI,
                onNavigate = { route -> navController.navigate(route) },
                onBackAction = { navController.navigateUp() }
            )
        }

        // Anthropic模型选择界面
        composable(route = Route.ANTHROPIC_MODEL_SELECT) {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(Route.SETUP_ROUTE)
            }
            val setupViewModel: SetupViewModel = hiltViewModel(parentEntry)
            SelectModelScreen(
                setupViewModel = setupViewModel,
                currentRoute = Route.ANTHROPIC_MODEL_SELECT,
                platformType = ApiType.ANTHROPIC,
                onNavigate = { route -> navController.navigate(route) },
                onBackAction = { navController.navigateUp() }
            )
        }

        // Google模型选择界面
        composable(route = Route.GOOGLE_MODEL_SELECT) {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(Route.SETUP_ROUTE)
            }
            val setupViewModel: SetupViewModel = hiltViewModel(parentEntry)
            SelectModelScreen(
                setupViewModel = setupViewModel,
                currentRoute = Route.GOOGLE_MODEL_SELECT,
                platformType = ApiType.GOOGLE,
                onNavigate = { route -> navController.navigate(route) },
                onBackAction = { navController.navigateUp() }
            )
        }

        // Ollama模型选择界面
        composable(route = Route.OLLAMA_MODEL_SELECT) {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(Route.SETUP_ROUTE)
            }
            val setupViewModel: SetupViewModel = hiltViewModel(parentEntry)
            SelectModelScreen(
                setupViewModel = setupViewModel,
                currentRoute = Route.OLLAMA_MODEL_SELECT,
                platformType = ApiType.OLLAMA,
                onNavigate = { route -> navController.navigate(route) },
                onBackAction = { navController.navigateUp() }
            )
        }

        // 设置完成界面
        composable(route = Route.SETUP_COMPLETE) {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(Route.SETUP_ROUTE)
            }
            val setupViewModel: SetupViewModel = hiltViewModel(parentEntry)
            SetupCompleteScreen(
                setupViewModel = setupViewModel,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Route.GET_STARTED) { inclusive = true }
                    }
                },
                onBackAction = { navController.navigateUp() }
            )
        }
    }
}

/**
 * 定义导航图构建器的扩展函数，用于设置主屏幕导航
 *
 * @param navController 导航控制器，用于管理应用内的导航
 */
fun NavGraphBuilder.homeScreenNavigation(navController: NavHostController) {
    composable(Route.CHAT_LIST) {
        HomeScreen(
            // 设置点击事件，导航到设置页面
            settingOnClick = { navController.navigate(Route.SETTING_ROUTE) { launchSingleTop = true } },
            // 设置点击现有聊天室的事件
            onExistingChatClick = { chatRoom ->
                // 将启用的平台转换为字符串
                val enabledPlatformString = chatRoom.enabledPlatform.joinToString(",") { v -> v.name }
                // 导航到聊天室页面，替换路由中的参数
                navController.navigate(
                    Route.CHAT_ROOM
                        .replace(oldValue = "{chatRoomId}", newValue = "${chatRoom.id}")
                        .replace(oldValue = "{enabledPlatforms}", newValue = enabledPlatformString)
                )
            },
            // 设置导航到新聊天的事件
            navigateToNewChat = {
                // 将启用的平台转换为字符串
                val enabledPlatformString = it.joinToString(",") { v -> v.name }
                // 导航到新的聊天室页面，使用"0"作为聊天室ID
                navController.navigate(
                    Route.CHAT_ROOM
                        .replace(oldValue = "{chatRoomId}", newValue = "0")
                        .replace(oldValue = "{enabledPlatforms}", newValue = enabledPlatformString)
                )
            }
        )
    }
}

/**
 * 定义导航图构建器的扩展函数，用于设置聊天屏幕导航
 *
 * @param navController 导航控制器，用于管理应用内的导航
 */
fun NavGraphBuilder.chatScreenNavigation(navController: NavHostController) {
    composable(
        Route.CHAT_ROOM,
        // 定义路由参数
        arguments = listOf(
            navArgument("chatRoomId") { type = NavType.IntType },
            navArgument("enabledPlatforms") { defaultValue = "" }
        )
    ) {
        ChatScreen(
            // 设置返回操作
            onBackAction = { navController.navigateUp() }
        )
    }
}

/**
 * 定义导航图构建器的扩展函数，用于设置设置页面导航
 *
 * @param navController 导航控制器，用于管理应用内的导航
 */
fun NavGraphBuilder.settingNavigation(navController: NavHostController) {
    navigation(startDestination = Route.SETTINGS, route = Route.SETTING_ROUTE) {
        composable(Route.SETTINGS) {
            // 获取父级导航条目
            val parentEntry = remember(it) {
                navController.getBackStackEntry(Route.SETTING_ROUTE)
            }
            // 获取SettingViewModel实例
            val settingViewModel: SettingViewModel = hiltViewModel(parentEntry)
            SettingScreen(
                settingViewModel = settingViewModel,
                // 设置导航点击事件
                onNavigationClick = { navController.navigateUp() },
                // 设置导航到平台设置的事件
                onNavigateToPlatformSetting = { apiType ->
                    when (apiType) {
                        ApiType.OPENAI -> navController.navigate(Route.OPENAI_SETTINGS)
                        ApiType.ANTHROPIC -> navController.navigate(Route.ANTHROPIC_SETTINGS)
                        ApiType.GOOGLE -> navController.navigate(Route.GOOGLE_SETTINGS)
                        ApiType.OLLAMA -> navController.navigate(Route.OLLAMA_SETTINGS)
                    }
                },
                // 设置导航到关于页面的事件
                onNavigateToAboutPage = { navController.navigate(Route.ABOUT_PAGE) }
            )
        }
        // OpenAI设置页面
        composable(Route.OPENAI_SETTINGS) {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(Route.SETTING_ROUTE)
            }
            val settingViewModel: SettingViewModel = hiltViewModel(parentEntry)
            PlatformSettingScreen(
                settingViewModel = settingViewModel,
                apiType = ApiType.OPENAI
            ) { navController.navigateUp() }
        }
        // Anthropic设置页面
        composable(Route.ANTHROPIC_SETTINGS) {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(Route.SETTING_ROUTE)
            }
            val settingViewModel: SettingViewModel = hiltViewModel(parentEntry)
            PlatformSettingScreen(
                settingViewModel = settingViewModel,
                apiType = ApiType.ANTHROPIC
            ) { navController.navigateUp() }
        }
        // Google设置页面
        composable(Route.GOOGLE_SETTINGS) {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(Route.SETTING_ROUTE)
            }
            val settingViewModel: SettingViewModel = hiltViewModel(parentEntry)
            PlatformSettingScreen(
                settingViewModel = settingViewModel,
                apiType = ApiType.GOOGLE
            ) { navController.navigateUp() }
        }
        // Ollama设置页面
        composable(Route.OLLAMA_SETTINGS) {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(Route.SETTING_ROUTE)
            }
            val settingViewModel: SettingViewModel = hiltViewModel(parentEntry)
            PlatformSettingScreen(
                settingViewModel = settingViewModel,
                apiType = ApiType.OLLAMA
            ) { navController.navigateUp() }
        }
        // 关于页面
        composable(Route.ABOUT_PAGE) {
            AboutScreen(
                onNavigationClick = { navController.navigateUp() },
                onNavigationToLicense = { navController.navigate(Route.LICENSE) }
            )
        }
        // 许可证页面
        composable(Route.LICENSE) {
            LicenseScreen(onNavigationClick = { navController.navigateUp() })
        }
    }
}
