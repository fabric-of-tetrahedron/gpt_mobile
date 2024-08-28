package dev.chungjungsoo.gptmobile.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.chungjungsoo.gptmobile.data.database.ChatDatabase
import dev.chungjungsoo.gptmobile.data.database.dao.ChatRoomDao
import dev.chungjungsoo.gptmobile.data.database.dao.MessageDao
import javax.inject.Singleton

/**
 * 数据库模块
 *
 * 该模块提供了与数据库相关的依赖注入。
 * 它使用Dagger Hilt来管理依赖注入，并提供了ChatDatabase及其相关DAO的实例。
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    // 数据库名称常量
    private const val DB_NAME = "chat"

    /**
     * 提供ChatRoomDao实例
     *
     * @param chatDatabase ChatDatabase实例
     * @return ChatRoomDao实例
     */
    @Provides
    fun provideChatRoomDao(chatDatabase: ChatDatabase): ChatRoomDao = chatDatabase.chatRoomDao()

    /**
     * 提供MessageDao实例
     *
     * @param chatDatabase ChatDatabase实例
     * @return MessageDao实例
     */
    @Provides
    fun provideMessageDao(chatDatabase: ChatDatabase): MessageDao = chatDatabase.messageDao()

    /**
     * 提供ChatDatabase实例
     *
     * 这个方法创建并返回一个ChatDatabase的单例实例。
     * 使用Room.databaseBuilder来构建数据库。
     *
     * @param appContext 应用程序上下文
     * @return ChatDatabase实例
     */
    @Provides
    @Singleton
    fun provideChatDatabase(@ApplicationContext appContext: Context): ChatDatabase = Room.databaseBuilder(
        appContext,
        ChatDatabase::class.java,
        DB_NAME
    ).build()
}
