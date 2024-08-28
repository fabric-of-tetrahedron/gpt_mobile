package dev.chungjungsoo.gptmobile.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.json.Json

/**
 * 网络客户端类
 *
 * 这个类负责创建和配置HTTP客户端，用于处理网络请求。
 * 它使用Ktor客户端库，并配置了内容协商、超时、日志记录等功能。
 *
 * @property httpEngine HTTP客户端引擎工厂
 */
@Singleton
class NetworkClient @Inject constructor(
    private val httpEngine: HttpClientEngineFactory<*>
) {

    /**
     * 懒加载的HTTP客户端实例
     *
     * 这个客户端配置了以下功能：
     * 1. 内容协商：使用JSON序列化
     * 2. 超时设置
     * 3. 日志记录
     * 4. 默认请求头
     */
    private val client by lazy {
        HttpClient(httpEngine) {
            // 期望请求成功
            expectSuccess = true

            // 安装内容协商插件，配置JSON序列化
            install(ContentNegotiation) {
                json(
                    Json {
                        isLenient = true // 宽松模式
                        ignoreUnknownKeys = true // 忽略未知键
                        allowSpecialFloatingPointValues = true // 允许特殊浮点值
                        useArrayPolymorphism = true // 使用数组多态
                    }
                )
            }

            // 安装超时插件，设置请求超时时间
            install(HttpTimeout) {
                requestTimeoutMillis = TIMEOUT.toLong()
            }

            // 安装日志插件，配置日志级别和敏感信息处理
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
                sanitizeHeader { header -> header == HttpHeaders.Authorization }
            }

            // 安装默认请求插件，设置默认的内容类型头
            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
        }
    }

    /**
     * 重载调用运算符，返回HTTP客户端实例
     *
     * @return 配置好的HTTP客户端实例
     */
    operator fun invoke(): HttpClient = client

    companion object {
        // 超时时间常量，设置为5分钟
        private const val TIMEOUT = 1_000 * 60 * 5
    }
}
