package dev.chungjungsoo.gptmobile.data.repository

import dev.chungjungsoo.gptmobile.data.database.entity.ChatRoom
import dev.chungjungsoo.gptmobile.data.database.entity.Message
import dev.chungjungsoo.gptmobile.data.dto.ApiState
import kotlinx.coroutines.flow.Flow

/**
 * 聊天仓库接口
 *
 * 该接口定义了与聊天相关的各种操作，包括与不同AI服务的通信、聊天记录的管理等。
 */
interface ChatRepository {

    /**
     * 完成OpenAI聊天
     *
     * @param question 用户的问题消息
     * @param history 聊天历史记录
     * @return 返回一个Flow，包含API调用的状态
     */
    suspend fun completeOpenAIChat(question: Message, history: List<Message>): Flow<ApiState>

    /**
     * 完成Anthropic聊天
     *
     * @param question 用户的问题消息
     * @param history 聊天历史记录
     * @return 返回一个Flow，包含API调用的状态
     */
    suspend fun completeAnthropicChat(question: Message, history: List<Message>): Flow<ApiState>

    /**
     * 完成Google聊天
     *
     * @param question 用户的问题消息
     * @param history 聊天历史记录
     * @return 返回一个Flow，包含API调用的状态
     */
    suspend fun completeGoogleChat(question: Message, history: List<Message>): Flow<ApiState>

    /**
     * 完成Ollama聊天
     *
     * @param question 用户的问题消息
     * @param history 聊天历史记录
     * @return 返回一个Flow，包含API调用的状态
     */
    suspend fun completeOllamaChat(question: Message, history: List<Message>): Flow<ApiState>

    /**
     * 获取聊天列表
     *
     * @return 返回所有聊天室的列表
     */
    suspend fun fetchChatList(): List<ChatRoom>

    /**
     * 获取特定聊天室的消息
     *
     * @param chatId 聊天室ID
     * @return 返回指定聊天室的所有消息列表
     */
    suspend fun fetchMessages(chatId: Int): List<Message>

    /**
     * 更新聊天室标题
     *
     * @param chatRoom 要更新的聊天室
     * @param title 新的标题
     */
    suspend fun updateChatTitle(chatRoom: ChatRoom, title: String)

    /**
     * 保存聊天记录
     *
     * @param chatRoom 聊天室信息
     * @param messages 要保存的消息列表
     * @return 返回保存后的聊天室信息
     */
    suspend fun saveChat(chatRoom: ChatRoom, messages: List<Message>): ChatRoom

    /**
     * 删除多个聊天室
     *
     * @param chatRooms 要删除的聊天室列表
     */
    suspend fun deleteChats(chatRooms: List<ChatRoom>)
}
