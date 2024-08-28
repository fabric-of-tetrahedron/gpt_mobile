package dev.chungjungsoo.gptmobile.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow

/**
 * 为StateFlow提供一个扩展函数，用于在Composable环境中安全地收集状态。
 *
 * @param T 状态流中的数据类型
 * @return 返回一个可组合的State对象，包含StateFlow的最新值
 */
@Composable
fun <T> StateFlow<T>.collectManagedState(): State<T> {
    // 当这个问题修复后，移除此注释：https://issuetracker.google.com/issues/336842920
    // 使用collectAsStateWithLifecycle函数来收集StateFlow的值
    // 这个方法会自动处理生命周期相关的问题，避免在非活动状态下不必要的更新
    return this.collectAsStateWithLifecycle(
        lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    )
}
