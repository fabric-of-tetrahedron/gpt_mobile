package dev.chungjungsoo.gptmobile.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.chungjungsoo.gptmobile.data.datastore.SettingDataSource
import dev.chungjungsoo.gptmobile.data.repository.SettingRepository
import dev.chungjungsoo.gptmobile.data.repository.SettingRepositoryImpl
import javax.inject.Singleton

/**
 * 设置仓库模块
 *
 * 这个模块负责提供设置仓库的依赖注入。
 * 它使用Dagger Hilt框架来实现依赖注入，并被安装在SingletonComponent中，
 * 确保在整个应用程序生命周期内只有一个实例。
 */
@Module
@InstallIn(SingletonComponent::class)
object SettingRepositoryModule {

    /**
     * 提供设置仓库的实例
     *
     * @param settingDataSource 设置数据源，用于访问和管理设置数据
     * @return SettingRepository 的实现，用于管理应用程序的设置
     */
    @Provides
    @Singleton
    fun provideSettingRepository(
        settingDataSource: SettingDataSource
    ): SettingRepository = SettingRepositoryImpl(settingDataSource)
}
