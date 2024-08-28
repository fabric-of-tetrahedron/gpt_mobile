package dev.chungjungsoo.gptmobile.presentation.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.chungjungsoo.gptmobile.data.database.entity.ChatRoom
import dev.chungjungsoo.gptmobile.data.dto.Platform
import dev.chungjungsoo.gptmobile.data.repository.ChatRepository
import dev.chungjungsoo.gptmobile.data.repository.SettingRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * HomeViewModel 类
 *
 * 这个类是主页面的ViewModel，负责管理聊天列表、平台状态和对话框的显示状态。
 * 它使用Hilt进行依赖注入，并继承自ViewModel类。
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val settingRepository: SettingRepository
) : ViewModel() {

    /**
     * 聊天列表状态数据类
     *
     * @property chats 聊天室列表
     * @property isSelectionMode 是否处于选择模式
     * @property selected 选中状态列表
     */
    data class ChatListState(
        val chats: List<ChatRoom> = listOf(),
        val isSelectionMode: Boolean = false,
        val selected: List<Boolean> = listOf()
    )

    // 聊天列表状态的可变状态流
    private val _chatListState = MutableStateFlow(ChatListState())
    // 聊天列表状态的只读状态流
    val chatListState: StateFlow<ChatListState> = _chatListState.asStateFlow()

    // 平台状态的可变状态流
    private val _platformState = MutableStateFlow(listOf<Platform>())
    // 平台状态的只读状态流
    val platformState: StateFlow<List<Platform>> = _platformState.asStateFlow()

    // 显示选择模型对话框的可变状态流
    private val _showSelectModelDialog = MutableStateFlow(false)
    // 显示选择模型对话框的只读状态流
    val showSelectModelDialog: StateFlow<Boolean> = _showSelectModelDialog.asStateFlow()

    // 显示删除警告对话框的可变状态流
    private val _showDeleteWarningDialog = MutableStateFlow(false)
    // 显示删除警告对话框的只读状态流
    val showDeleteWarningDialog: StateFlow<Boolean> = _showDeleteWarningDialog.asStateFlow()

    /**
     * 更新平台的选中状态
     *
     * @param platform 要更新的平台
     */
    fun updateCheckedState(platform: Platform) {
        val index = _platformState.value.indexOf(platform)

        if (index >= 0) {
            _platformState.update {
                it.mapIndexed { i, p ->
                    if (index == i) {
                        p.copy(selected = p.selected.not())
                    } else {
                        p
                    }
                }
            }
        }
    }

    /**
     * 打开删除警告对话框
     */
    fun openDeleteWarningDialog() {
        closeSelectModelDialog()
        _showDeleteWarningDialog.update { true }
    }

    /**
     * 关闭删除警告对话框
     */
    fun closeDeleteWarningDialog() {
        _showDeleteWarningDialog.update { false }
    }

    /**
     * 打开选择模型对话框
     */
    fun openSelectModelDialog() {
        _showSelectModelDialog.update { true }
        disableSelectionMode()
    }

    /**
     * 关闭选择模型对话框
     */
    fun closeSelectModelDialog() {
        _showSelectModelDialog.update { false }
    }

    /**
     * 删除选中的聊天
     */
    fun deleteSelectedChats() {
        viewModelScope.launch {
            val selectedChats = _chatListState.value.chats.filterIndexed { index, _ ->
                _chatListState.value.selected[index]
            }

            chatRepository.deleteChats(selectedChats)
            _chatListState.update { it.copy(chats = chatRepository.fetchChatList()) }
            disableSelectionMode()
        }
    }

    /**
     * 禁用选择模式
     */
    fun disableSelectionMode() {
        _chatListState.update {
            it.copy(
                selected = List(it.chats.size) { false },
                isSelectionMode = false
            )
        }
    }

    /**
     * 启用选择模式
     */
    fun enableSelectionMode() {
        _chatListState.update { it.copy(isSelectionMode = true) }
    }

    /**
     * 获取聊天列表
     */
    fun fetchChats() {
        viewModelScope.launch {
            val chats = chatRepository.fetchChatList()

            _chatListState.update {
                it.copy(
                    chats = chats,
                    selected = List(chats.size) { false },
                    isSelectionMode = false
                )
            }

            Log.d("chats", "${_chatListState.value.chats}")
        }
    }

    /**
     * 获取平台状态
     */
    fun fetchPlatformStatus() {
        viewModelScope.launch {
            val platforms = settingRepository.fetchPlatforms()
            _platformState.update { platforms }
        }
    }

    /**
     * 选择聊天
     *
     * @param chatRoomIdx 聊天室索引
     */
    fun selectChat(chatRoomIdx: Int) {
        if (chatRoomIdx < 0 || chatRoomIdx > _chatListState.value.chats.size) return

        _chatListState.update {
            it.copy(
                selected = it.selected.mapIndexed { index, b ->
                    if (index == chatRoomIdx) {
                        !b
                    } else {
                        b
                    }
                }
            )
        }

        if (_chatListState.value.selected.count { it } == 0) {
            disableSelectionMode()
        }
    }
}
