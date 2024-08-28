package dev.chungjungsoo.gptmobile.util

import androidx.compose.animation.core.*
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import kotlin.math.abs

/**
 * 固定退出直到折叠的滚动行为
 *
 * 这个函数创建一个特殊的TopAppBar滚动行为，它保持固定位置直到完全折叠。
 *
 * @param state TopAppBar的状态，默认使用rememberTopAppBarState()创建
 * @param canScroll 一个返回布尔值的函数，用于确定是否可以滚动，默认总是返回true
 * @param snapAnimationSpec 定义滚动停止时的动画规格，默认使用中低刚度的弹簧动画
 * @param flingAnimationSpec 定义快速滑动时的动画规格，默认使用基于样条的衰减动画
 * @return 返回一个TopAppBarScrollBehavior实例
 *
 * 特别感谢 @BenjyTec: https://stackoverflow.com/a/78538564/8606428
 */
@ExperimentalMaterial3Api
@Composable
fun pinnedExitUntilCollapsedScrollBehavior(
    state: TopAppBarState = rememberTopAppBarState(),
    canScroll: () -> Boolean = { true },
    snapAnimationSpec: AnimationSpec<Float>? = spring(stiffness = Spring.StiffnessMediumLow),
    flingAnimationSpec: DecayAnimationSpec<Float>? = rememberSplineBasedDecay()
): TopAppBarScrollBehavior =
    PinnedExitUntilCollapsedScrollBehavior(
        state = state,
        snapAnimationSpec = snapAnimationSpec,
        flingAnimationSpec = flingAnimationSpec,
        canScroll = canScroll
    )

/**
 * 固定退出直到折叠的滚动行为
 * 这个类实现了一个特殊的顶部应用栏滚动行为，它保持固定位置直到完全折叠
 */
@OptIn(ExperimentalMaterial3Api::class)
private class PinnedExitUntilCollapsedScrollBehavior(
    override val state: TopAppBarState,
    override val snapAnimationSpec: AnimationSpec<Float>?,
    override val flingAnimationSpec: DecayAnimationSpec<Float>?,
    val canScroll: () -> Boolean = { true }
) : TopAppBarScrollBehavior {
    override val isPinned: Boolean = true
    override var nestedScrollConnection =
        object : NestedScrollConnection {
            /**
             * 处理预滚动事件
             * @param available 可用的滚动偏移量
             * @param source 嵌套滚动源
             * @return 消耗的滚动偏移量
             */
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // 如果向下滚动或不能滚动，则不拦截
                if (!canScroll() || available.y > 0f) return Offset.Zero

                val prevHeightOffset = state.heightOffset
                state.heightOffset += available.y
                return if (prevHeightOffset != state.heightOffset) {
                    // 正在折叠或展开顶部应用栏，只消耗Y轴上的滚动
                    available.copy(x = 0f)
                } else {
                    Offset.Zero
                }
            }

            /**
             * 处理后滚动事件
             * @param consumed 已消耗的滚动偏移量
             * @param available 可用的滚动偏移量
             * @param source 嵌套滚动源
             * @return 额外消耗的滚动偏移量
             */
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (!canScroll()) return Offset.Zero
                state.contentOffset += consumed.y

                if (available.y < 0f || consumed.y < 0f) {
                    // 向上滚动时，更新状态的高度偏移
                    val oldHeightOffset = state.heightOffset
                    state.heightOffset += consumed.y
                    return Offset(0f, state.heightOffset - oldHeightOffset)
                }

                if (consumed.y == 0f && available.y > 0) {
                    // 滚动到底部时，重置内容偏移量为零，消除浮点精度误差
                    state.contentOffset = 0f
                }

                if (available.y > 0f) {
                    // 调整高度偏移，以防消耗的deltaY小于预滚动中记录的可用deltaY
                    val oldHeightOffset = state.heightOffset
                    state.heightOffset += available.y
                    return Offset(0f, state.heightOffset - oldHeightOffset)
                }
                return Offset.Zero
            }

            /**
             * 处理惯性滚动结束事件
             * @param consumed 已消耗的速度
             * @param available 可用的速度
             * @return 额外消耗的速度
             */
            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                val superConsumed = super.onPostFling(consumed, available)
                return superConsumed + settleAppBar(
                    state,
                    available.y,
                    flingAnimationSpec,
                    snapAnimationSpec
                )
            }
        }
}

/**
 * 稳定顶部应用栏的位置
 * @param state 顶部应用栏状态
 * @param velocity 当前速度
 * @param flingAnimationSpec 惯性动画规格
 * @param snapAnimationSpec 吸附动画规格
 * @return 剩余速度
 */
@OptIn(ExperimentalMaterial3Api::class)
private suspend fun settleAppBar(
    state: TopAppBarState,
    velocity: Float,
    flingAnimationSpec: DecayAnimationSpec<Float>?,
    snapAnimationSpec: AnimationSpec<Float>?
): Velocity {
    // 检查应用栏是否完全折叠/展开，如果是，无需稳定，直接返回零速度
    if (state.collapsedFraction < 0.01f || state.collapsedFraction == 1f) {
        return Velocity.Zero
    }
    var remainingVelocity = velocity
    // 如果有初始速度，继续动画以展开或折叠应用栏
    if (flingAnimationSpec != null && abs(velocity) > 1f) {
        var lastValue = 0f
        AnimationState(
            initialValue = 0f,
            initialVelocity = velocity
        )
            .animateDecay(flingAnimationSpec) {
                val delta = value - lastValue
                val initialHeightOffset = state.heightOffset
                state.heightOffset = initialHeightOffset + delta
                val consumed = abs(initialHeightOffset - state.heightOffset)
                lastValue = value
                remainingVelocity = this.velocity
                // 避免舍入误差，如果有未消耗的偏移量则停止动画
                if (abs(delta - consumed) > 0.5f) this.cancelAnimation()
            }
    }
    // 如果提供了吸附动画规格，执行吸附
    if (snapAnimationSpec != null) {
        if (state.heightOffset < 0 &&
            state.heightOffset > state.heightOffsetLimit
        ) {
            AnimationState(initialValue = state.heightOffset).animateTo(
                if (state.collapsedFraction < 0.5f) {
                    0f
                } else {
                    state.heightOffsetLimit
                },
                animationSpec = snapAnimationSpec
            ) { state.heightOffset = value }
        }
    }

    return Velocity(0f, remainingVelocity)
}
