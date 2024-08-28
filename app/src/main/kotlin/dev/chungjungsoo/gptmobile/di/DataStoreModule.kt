package dev.chungjungsoo.gptmobile.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 定义存储令牌的首选项文件名
 */
private const val TOKEN_PREF_FILE = "token"

/**
 * DataStore模块
 *
 * 该模块提供了DataStore相关的依赖注入
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    /**
     * 提供PreferencesDataStore实例
     *
     * @param applicationContext 应用程序上下文
     * @return DataStore<Preferences> 首选项数据存储实例
     */
    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext applicationContext: Context): DataStore<Preferences> = PreferenceDataStoreFactory.create(
        // 设置文件损坏处理器
        corruptionHandler = ReplaceFileCorruptionHandler(
            // 如果文件损坏，生成空的首选项
            produceNewData = { emptyPreferences() }
        ),
        // 指定首选项文件的生成方式
        produceFile = { applicationContext.preferencesDataStoreFile(TOKEN_PREF_FILE) }
    )
}
