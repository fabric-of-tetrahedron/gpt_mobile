package dev.chungjungsoo.gptmobile.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.chungjungsoo.gptmobile.data.database.entity.Message

/**
 * 消息数据访问对象（DAO）接口
 *
 * 该接口定义了与消息相关的数据库操作方法
 */
@Dao
interface MessageDao {

    /**
     * 根据聊天ID加载消息列表
     *
     * @param chatInt 聊天ID
     * @return 返回指定聊天ID的消息列表
     */
    @Query("SELECT * FROM messages WHERE chat_id=:chatInt")
    suspend fun loadMessages(chatInt: Int): List<Message>

    /**
     * 添加一个或多个消息到数据库
     *
     * @param messages 要添加的消息对象，可以是一个或多个
     */
    @Insert
    suspend fun addMessages(vararg messages: Message)

    /**
     * 编辑一个或多个已存在的消息
     *
     * @param message 要编辑的消息对象，可以是一个或多个
     */
    @Update
    suspend fun editMessages(vararg message: Message)

    /**
     * 删除一个或多个消息
     *
     * @param message 要删除的消息对象，可以是一个或多个
     */
    @Delete
    suspend fun deleteMessages(vararg message: Message)
}
