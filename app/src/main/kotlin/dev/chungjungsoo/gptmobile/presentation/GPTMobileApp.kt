package dev.chungjungsoo.gptmobile.presentation

import android.app.Application
import android.content.Context
import com.tddworks.ollama.api.OllamaConfig
import com.tddworks.ollama.di.initOllama
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltAndroidApp
class GPTMobileApp : Application() {
    // TODO Delete when https://github.com/google/dagger/issues/3601 is resolved.
    @Inject
    @ApplicationContext
    lateinit var context: Context

//    override fun onCreate() {
//        super.onCreate()
//
//        initOllama(OllamaConfig())
//    }
}
