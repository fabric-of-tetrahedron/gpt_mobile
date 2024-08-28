package dev.chungjungsoo.gptmobile.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.chungjungsoo.gptmobile.data.datastore.SettingDataSource
import dev.chungjungsoo.gptmobile.data.datastore.SettingDataSourceImpl
import javax.inject.Singleton

/**
 * 设置数据源模块
 *
 * 该模块负责提供设置数据源的依赖注入。
 * 它使用Hilt库进行依赖注入，并被安装在SingletonComponent中，
 * 确保在整个应用程序生命周期内只有一个实例。
 */
@Module
@InstallIn(SingletonComponent::class)
object SettingDataSourceModule {

    /**
     * 提供设置数据源的实例
     *
     * @param dataStore DataStore<Preferences>实例，用于存储和检索设置数据
     * @return SettingDataSource 接口的实现，用于管理应用程序设置
     */
    @Provides
    @Singleton
    fun provideSettingDataStore(dataStore: DataStore<Preferences>): SettingDataSource = SettingDataSourceImpl(dataStore)
}
