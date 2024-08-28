package dev.chungjungsoo.gptmobile

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.ktor.http.Url
import love.forte.simbot.application.Application
import love.forte.simbot.application.listeners
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBotConfiguration
import love.forte.simbot.component.onebot.v11.core.bot.firstOneBotBotManager
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotFriendMessageEvent
import love.forte.simbot.component.onebot.v11.core.useOneBot11
import love.forte.simbot.core.application.launchSimpleApplication
import love.forte.simbot.event.ChatGroupMessageEvent
import love.forte.simbot.event.EventResult
import love.forte.simbot.event.listen
import love.forte.simbot.event.process
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class OneBotTest {
    @Test
    suspend fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("dev.chungjungsoo.gptmobile", appContext.packageName)

        val app = launchSimpleApplication {
            // 使用OneBot组件相关的内容。
            useOneBot11()
            // 其他配置...
        }

        app.configure()
        app.join() // 挂起app直到cancel它
    }

    suspend fun Application.configure() {
        // 寻找、获得所需的BotManager
        val botManager = botManagers.firstOneBotBotManager()
        // 注册你所需的bot
        val bot = botManager.register(
            OneBotBotConfiguration().apply {
                // 这几个是必选属性
                /// 在OneBot组件中用于区分不同Bot的唯一ID， 建议可以直接使用QQ号。
                botUniqueId = "11112222"
                apiServerHost = Url("http://127.0.0.1:3000")
                eventServerHost = Url("ws://127.0.0.1:3001")
                // 其他配置, 一般都是可选属性
                /// token
                accessToken = null
                /// ...
            }
        )

        // 启动你的bot
        bot.start()

        // Kotlin 中，可以使用 Application.listeners 扩展函数。
        listeners {
            // 使用 listen 监听一个事件
            // 此处是一个标准库中通用的类型：聊天群消息事件
            listen<ChatGroupMessageEvent> { event ->
                println("Event: $event")
                if (event.messageContent.plainText?.trim() == "你好") {
                    event.reply("你也好")
                }

                // 使用listen时必须返回一个EventResult类型的结果
                EventResult.empty()
            }

            // 使用 process 监听一个事件
            // 此处监听的是OneBot组件中的专属类型：OneBot的好友消息事件
            process<OneBotFriendMessageEvent> { event ->
                println("Event: $event")
                if (event.messageContent.plainText?.trim() == "你好") {
                    event.reply("你也好")
                }

                // 使用 process 不需要返回 EventResult，默认视为返回 EventResult.empty()
            }
        }
    }

}
