package dev.chungjungsoo.gptmobile.presentation

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * GPTMobileApp 类
 *
 * 这是应用程序的主要入口点。它继承自 Android 的 Application 类，
 * 并使用 Hilt 依赖注入框架进行注解。
 *
 * @HiltAndroidApp 注解用于触发 Hilt 的代码生成，
 * 包括生成用于依赖注入的基础组件。
 */
@HiltAndroidApp
class GPTMobileApp : Application() {
    /**
     * 应用程序上下文
     *
     * 这个属性使用 @Inject 注解，表示它将由 Hilt 进行依赖注入。
     * @ApplicationContext 注解指定注入的是应用程序级别的上下文。
     *
     * 注意：这是一个临时解决方案，等待 Dagger 问题 #3601 解决后将被删除。
     */
    // TODO 当 https://github.com/google/dagger/issues/3601 解决后删除此代码
    @Inject
    @ApplicationContext
    lateinit var context: Context
}
