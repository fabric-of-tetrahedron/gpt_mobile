package dev.chungjungsoo.gptmobile.util

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.saveable.Saver

/**
 * 多个ScrollState的保存器
 *
 * 这个Saver用于保存和恢复多个ScrollState对象的状态。
 * 它将ScrollState列表转换为整数列表进行保存，并在恢复时将整数列表转换回ScrollState列表。
 *
 * @property save 将ScrollState列表转换为整数列表的函数
 * @property restore 将整数列表转换回ScrollState列表的函数
 */
val multiScrollStateSaver: Saver<MutableList<ScrollState>, *> = Saver(
    // 保存函数：将每个ScrollState的value值映射到一个整数列表
    save = { it.map { scrollState -> scrollState.value } },
    // 恢复函数：将整数列表转换回ScrollState对象的可变列表
    restore = { it.map { i -> ScrollState(i) }.toMutableList() }
)
