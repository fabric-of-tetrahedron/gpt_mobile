package dev.chungjungsoo.gptmobile.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.chungjungsoo.gptmobile.data.network.AnthropicAPI
import dev.chungjungsoo.gptmobile.data.network.AnthropicAPIImpl
import dev.chungjungsoo.gptmobile.data.network.NetworkClient
import io.ktor.client.engine.okhttp.OkHttp
import javax.inject.Singleton

/**
 * 网络模块
 *
 * 这个模块负责提供网络相关的依赖注入。
 * 它使用Dagger Hilt进行依赖注入，并被安装在SingletonComponent中，
 * 确保提供的实例在整个应用程序生命周期内是单例的。
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * 提供NetworkClient实例
     *
     * @return 返回一个使用OkHttp引擎的NetworkClient实例
     */
    @Provides
    @Singleton
    fun provideNetworkClient(): NetworkClient = NetworkClient(OkHttp)

    /**
     * 提供AnthropicAPI实例
     *
     * @return 返回一个AnthropicAPIImpl实例，该实例使用provideNetworkClient()方法提供的NetworkClient
     */
    @Provides
    @Singleton
    fun provideAnthropicAPI(): AnthropicAPI = AnthropicAPIImpl(provideNetworkClient())
}
