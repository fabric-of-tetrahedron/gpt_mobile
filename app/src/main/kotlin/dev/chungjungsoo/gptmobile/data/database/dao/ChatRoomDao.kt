package dev.chungjungsoo.gptmobile.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.chungjungsoo.gptmobile.data.database.entity.ChatRoom

/**
 * 聊天室数据访问对象（DAO）
 *
 * 该接口定义了与聊天室相关的数据库操作方法。
 * 使用Room数据库库的@Dao注解标记此接口。
 */
@Dao
interface ChatRoomDao {

    /**
     * 获取所有聊天室
     *
     * @return 返回按创建时间降序排列的聊天室列表
     */
    @Query("SELECT * FROM chats ORDER BY created_at DESC")
    suspend fun getChatRooms(): List<ChatRoom>

    /**
     * 添加新的聊天室
     *
     * @param chatRoom 要添加的聊天室对象
     * @return 返回新插入记录的行ID
     */
    @Insert
    suspend fun addChatRoom(chatRoom: ChatRoom): Long

    /**
     * 编辑现有的聊天室
     *
     * @param chatRoom 要更新的聊天室对象
     */
    @Update
    suspend fun editChatRoom(chatRoom: ChatRoom)

    /**
     * 删除一个或多个聊天室
     *
     * @param chatRooms 要删除的聊天室对象数组
     */
    @Delete
    suspend fun deleteChatRooms(vararg chatRooms: ChatRoom)
}
