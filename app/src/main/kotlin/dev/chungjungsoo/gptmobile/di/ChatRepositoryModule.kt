package dev.chungjungsoo.gptmobile.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.chungjungsoo.gptmobile.data.database.dao.ChatRoomDao
import dev.chungjungsoo.gptmobile.data.database.dao.MessageDao
import dev.chungjungsoo.gptmobile.data.network.AnthropicAPI
import dev.chungjungsoo.gptmobile.data.repository.ChatRepository
import dev.chungjungsoo.gptmobile.data.repository.ChatRepositoryImpl
import dev.chungjungsoo.gptmobile.data.repository.SettingRepository
import javax.inject.Singleton

/**
 * 聊天仓库模块
 *
 * 该模块负责提供聊天仓库的依赖注入。
 * 它使用Dagger Hilt框架来实现依赖注入，并将ChatRepository的实现绑定到SingletonComponent。
 */
@Module
@InstallIn(SingletonComponent::class)
object ChatRepositoryModule {

    /**
     * 提供ChatRepository的实例
     *
     * @param chatRoomDao 聊天室数据访问对象，用于处理聊天室相关的数据库操作
     * @param messageDao 消息数据访问对象，用于处理消息相关的数据库操作
     * @param settingRepository 设置仓库，用于获取和管理应用设置
     * @param anthropicAPI Anthropic API接口，用于与Anthropic服务进行通信
     * @return ChatRepository的实现实例
     */
    @Provides
    @Singleton
    fun provideChatRepository(
        chatRoomDao: ChatRoomDao,
        messageDao: MessageDao,
        settingRepository: SettingRepository,
        anthropicAPI: AnthropicAPI
    ): ChatRepository = ChatRepositoryImpl(chatRoomDao, messageDao, settingRepository, anthropicAPI)
}
