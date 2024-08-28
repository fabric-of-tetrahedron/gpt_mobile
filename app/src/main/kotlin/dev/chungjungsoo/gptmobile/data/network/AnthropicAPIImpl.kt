package dev.chungjungsoo.gptmobile.data.network

import dev.chungjungsoo.gptmobile.data.ModelConstants
import dev.chungjungsoo.gptmobile.data.dto.anthropic.request.MessageRequest
import dev.chungjungsoo.gptmobile.data.dto.anthropic.response.ErrorDetail
import dev.chungjungsoo.gptmobile.data.dto.anthropic.response.ErrorResponseChunk
import dev.chungjungsoo.gptmobile.data.dto.anthropic.response.MessageResponseChunk
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.accept
import io.ktor.client.request.headers
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.HttpStatement
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.cancel
import io.ktor.utils.io.readUTF8Line
import javax.inject.Inject
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

/**
 * Anthropic API的实现类
 *
 * @property networkClient 网络客户端
 */
class AnthropicAPIImpl @Inject constructor(
    private val networkClient: NetworkClient
) : AnthropicAPI {

    // API令牌
    private var token: String? = null
    // API URL
    private var apiUrl: String = ModelConstants.ANTHROPIC_API_URL

    /**
     * 设置API令牌
     *
     * @param token API令牌
     */
    override fun setToken(token: String?) {
        this.token = token
    }

    /**
     * 设置API URL
     *
     * @param url API URL
     */
    override fun setAPIUrl(url: String) {
        this.apiUrl = url
    }

    /**
     * 流式传输聊天消息
     *
     * @param messageRequest 消息请求
     * @return 消息响应流
     */
    override fun streamChatMessage(messageRequest: MessageRequest): Flow<MessageResponseChunk> {
        val body = Json.encodeToJsonElement(messageRequest)

        // 构建HTTP请求
        val builder = HttpRequestBuilder().apply {
            method = HttpMethod.Post
            url("$apiUrl/v1/messages")
            contentType(ContentType.Application.Json)
            setBody(body)
            accept(ContentType.Text.EventStream)
            headers {
                append(API_KEY_HEADER, token ?: "")
                append(VERSION_HEADER, ANTHROPIC_VERSION)
            }
        }

        return flow {
            try {
                HttpStatement(builder = builder, client = networkClient()).execute {
                    streamEventsFrom(it)
                }
            } catch (e: Exception) {
                // 发生异常时发送错误响应
                emit(ErrorResponseChunk(error = ErrorDetail(type = "network_error", message = e.message ?: "")))
            }
        }
    }

    /**
     * 从HTTP响应中流式读取事件
     *
     * @param response HTTP响应
     */
    private suspend inline fun <reified T> FlowCollector<T>.streamEventsFrom(response: HttpResponse) {
        val channel: ByteReadChannel = response.body()
        try {
            while (currentCoroutineContext().isActive && !channel.isClosedForRead) {
                val line = channel.readUTF8Line() ?: continue
                val value: T = when {
                    line.startsWith(STREAM_END_TOKEN) -> break
                    line.startsWith(STREAM_PREFIX) -> Json.decodeFromString(line.removePrefix(STREAM_PREFIX))
                    else -> continue
                }
                emit(value)
            }
        } finally {
            channel.cancel()
        }
    }

    companion object {
        // 流前缀
        private const val STREAM_PREFIX = "data:"
        // 流结束标记
        private const val STREAM_END_TOKEN = "event: message_stop"
        // API密钥头
        private const val API_KEY_HEADER = "x-api-key"
        // 版本头
        private const val VERSION_HEADER = "anthropic-version"
        // Anthropic版本
        private const val ANTHROPIC_VERSION = "2023-06-01"
    }
}
