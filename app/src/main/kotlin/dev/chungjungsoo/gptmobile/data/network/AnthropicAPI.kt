package dev.chungjungsoo.gptmobile.data.network

import dev.chungjungsoo.gptmobile.data.dto.anthropic.request.MessageRequest
import dev.chungjungsoo.gptmobile.data.dto.anthropic.response.MessageResponseChunk
import kotlinx.coroutines.flow.Flow

/**
 * Anthropic API 接口
 *
 * 该接口定义了与 Anthropic API 交互的方法。
 */
interface AnthropicAPI {
    /**
     * 设置 API 令牌
     *
     * @param token API 令牌字符串，可以为 null
     */
    fun setToken(token: String?)

    /**
     * 设置 API 的 URL
     *
     * @param url API 的 URL 字符串
     */
    fun setAPIUrl(url: String)

    /**
     * 流式传输聊天消息
     *
     * 该方法用于向 Anthropic API 发送消息请求，并以流的形式接收响应。
     *
     * @param messageRequest 消息请求对象
     * @return 返回一个 Flow，其中包含消息响应的分块数据
     */
    fun streamChatMessage(messageRequest: MessageRequest): Flow<MessageResponseChunk>
}
