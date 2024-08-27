package dev.chungjungsoo.gptmobile.presentation.ui.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.tddworks.ollama.api.OllamaModel
import dev.chungjungsoo.gptmobile.R
import dev.chungjungsoo.gptmobile.data.ModelConstants.anthropicModels
import dev.chungjungsoo.gptmobile.data.ModelConstants.getDefaultAPIUrl
import dev.chungjungsoo.gptmobile.data.ModelConstants.googleModels
import dev.chungjungsoo.gptmobile.data.ModelConstants.ollamaModelDescriptions
import dev.chungjungsoo.gptmobile.data.ModelConstants.ollamaModels
import dev.chungjungsoo.gptmobile.data.ModelConstants.openaiModels
import dev.chungjungsoo.gptmobile.data.datastore.SettingDataSourceImpl_Factory
import dev.chungjungsoo.gptmobile.data.model.ApiType
import dev.chungjungsoo.gptmobile.data.repository.SettingRepository
import dev.chungjungsoo.gptmobile.data.repository.SettingRepositoryImpl
import dev.chungjungsoo.gptmobile.data.repository.SettingRepositoryImpl_Factory
import dev.chungjungsoo.gptmobile.presentation.common.RadioItem
import dev.chungjungsoo.gptmobile.presentation.common.TokenInputField
import dev.chungjungsoo.gptmobile.util.*
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlin.math.roundToInt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.internal.wait
import org.json.JSONObject
import org.jsoup.Jsoup

@Composable
fun APIUrlDialog(
    dialogState: SettingViewModel.DialogState,
    apiType: ApiType,
    initialValue: String,
    settingViewModel: SettingViewModel
) {
    if (dialogState.isApiUrlDialogOpen) {
        APIUrlDialog(
            apiType = apiType,
            initialValue = initialValue,
            onDismissRequest = settingViewModel::closeApiUrlDialog,
            onResetRequest = {
                settingViewModel.updateURL(apiType, getDefaultAPIUrl(apiType))
                settingViewModel.savePlatformSettings()
                settingViewModel.closeApiUrlDialog()
            },
            onConfirmRequest = { apiUrl ->
                settingViewModel.updateURL(apiType, apiUrl)
                settingViewModel.savePlatformSettings()
                settingViewModel.closeApiUrlDialog()
            }
        )
    }
}

@Composable
fun APIKeyDialog(
    dialogState: SettingViewModel.DialogState,
    apiType: ApiType,
    settingViewModel: SettingViewModel
) {
    if (dialogState.isApiTokenDialogOpen) {
        APIKeyDialog(
            apiType = apiType,
            onDismissRequest = settingViewModel::closeApiTokenDialog
        ) { apiToken ->
            settingViewModel.updateToken(apiType, apiToken)
            settingViewModel.savePlatformSettings()
            settingViewModel.closeApiTokenDialog()
        }
    }
}

@Composable
fun ModelDialog(
    dialogState: SettingViewModel.DialogState,
    apiType: ApiType,
    model: String?,
    settingViewModel: SettingViewModel
) {
    if (dialogState.isApiModelDialogOpen) {
        ModelDialog(
            apiType = apiType,
            model = model ?: "",
            onModelSelected = { m -> settingViewModel.updateModel(apiType, m) },
            onDismissRequest = settingViewModel::closeApiModelDialog,
            settingViewModel.settingRepository
        ) { m ->
            settingViewModel.updateModel(apiType, m)
            settingViewModel.savePlatformSettings()
            settingViewModel.closeApiModelDialog()
        }
    }
}

@Composable
fun TemperatureDialog(
    dialogState: SettingViewModel.DialogState,
    apiType: ApiType,
    temperature: Float,
    settingViewModel: SettingViewModel
) {
    if (dialogState.isTemperatureDialogOpen) {
        TemperatureDialog(
            apiType = apiType,
            temperature = temperature,
            onDismissRequest = settingViewModel::closeTemperatureDialog
        ) { temp ->
            settingViewModel.updateTemperature(apiType, temp)
            settingViewModel.savePlatformSettings()
            settingViewModel.closeTemperatureDialog()
        }
    }
}

@Composable
fun TopPDialog(
    dialogState: SettingViewModel.DialogState,
    apiType: ApiType,
    topP: Float?,
    settingViewModel: SettingViewModel
) {
    if (dialogState.isTopPDialogOpen) {
        TopPDialog(
            topP = topP,
            onDismissRequest = settingViewModel::closeTopPDialog
        ) { p ->
            settingViewModel.updateTopP(apiType, p)
            settingViewModel.savePlatformSettings()
            settingViewModel.closeTopPDialog()
        }
    }
}

@Composable
fun SystemPromptDialog(
    dialogState: SettingViewModel.DialogState,
    apiType: ApiType,
    systemPrompt: String,
    settingViewModel: SettingViewModel
) {
    if (dialogState.isSystemPromptDialogOpen) {
        SystemPromptDialog(
            prompt = systemPrompt,
            onDismissRequest = settingViewModel::closeSystemPromptDialog
        ) {
            settingViewModel.updateSystemPrompt(apiType, it)
            settingViewModel.savePlatformSettings()
            settingViewModel.closeSystemPromptDialog()
        }
    }
}

@Composable
private fun APIUrlDialog(
    apiType: ApiType,
    initialValue: String,
    onDismissRequest: () -> Unit,
    onResetRequest: () -> Unit,
    onConfirmRequest: (url: String) -> Unit
) {
    var apiUrl by remember { mutableStateOf(initialValue) }
    val configuration = LocalConfiguration.current

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 40.dp),
        title = { Text(text = stringResource(R.string.api_url)) },
        text = {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                value = apiUrl,
                isError = apiUrl.isValidUrl().not(),
                onValueChange = { apiUrl = it },
                label = {
                    Text(stringResource(R.string.api_url))
                },
                supportingText = {
                    if (apiUrl.isValidUrl().not()) {
                        Text(text = stringResource(R.string.invalid_api_url))
                    }
                }
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                enabled = apiUrl.isNotBlank() && apiUrl.isValidUrl(),
                onClick = { onConfirmRequest(apiUrl) }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            Row {
                TextButton(
                    modifier = Modifier.padding(end = 8.dp),
                    onClick = onResetRequest
                ) {
                    Text(stringResource(R.string.reset))
                }
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(R.string.cancel))
                }
            }
        }
    )
}

@Composable
private fun APIKeyDialog(
    apiType: ApiType,
    onDismissRequest: () -> Unit,
    onConfirmRequest: (token: String) -> Unit
) {
    var token by remember { mutableStateOf("") }
    val configuration = LocalConfiguration.current

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 40.dp),
        title = { Text(text = getPlatformAPILabelResources()[apiType]!!) },
        text = {
            TokenInputField(
                value = token,
                onValueChange = { token = it },
                onClearClick = { token = "" },
                label = getPlatformAPILabelResources()[apiType]!!,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                helpLink = getPlatformHelpLinkResources()[apiType]!!
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                enabled = token.isNotBlank(),
                onClick = { onConfirmRequest(token) }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun ModelDialog(
    apiType: ApiType,
    model: String,
    onModelSelected: (String) -> Unit,
    onDismissRequest: () -> Unit,
    settingRepository: SettingRepository,
    onConfirmRequest: (model: String) -> Unit
) {
    val ollamaModelsNew = linkedSetOf<String>()

    runBlocking {
        println("开始获取平台信息...")
        val platform = checkNotNull(settingRepository.fetchPlatforms().firstOrNull { it.name == ApiType.OLLAMA })
        println("平台信息获取成功: ${platform.apiUrl}")

        println("开始从API获取模型标签...")
        val response = HttpClient().use { client ->
            client.get("${platform.apiUrl}/api/tags")
        }
        println("API响应成功，开始解析响应内容...")
        val models = response.bodyAsText().let { body ->
            println("响应内容: $body")
            val jsonObject = JSONObject(body)
            jsonObject.getJSONArray("models")
        }
        println("响应内容解析成功，开始处理模型数据...")
        for (i in 0 until models.length()) {
            val model = models.getJSONObject(i)
            val modelName = model.getString("name")
            println("获取到模型名称: $modelName")
            val modelName_02 = modelName.substringBefore(":")
            ollamaModelsNew.add(modelName_02)
        }
        println("所有模型名称获取成功，更新模型列表...")
        ollamaModels.clear()
        ollamaModels.addAll(ollamaModelsNew)
        println("模型列表更新成功")

        println("开始获取模型描述...")
        val ollamaModelDescriptionsNew = mutableMapOf<String, String>()
        val client = HttpClient()
        for (modelName in ollamaModelsNew) {
            val url = "https://ollama.com/library/$modelName"
            val descriptionResponse = client.get(url)
            val document = Jsoup.parse(descriptionResponse.bodyAsText())
            val descriptionElement = document.selectXpath("/html/body/div/main/section[1]/h2").firstOrNull()
            val description = descriptionElement?.text()
            if (description != null) ollamaModelDescriptionsNew[modelName] = description
            println("模型 $modelName 的描述获取成功: $description")
        }
        client.close()
        println("所有模型描述获取成功")
        ollamaModelDescriptions.putAll(ollamaModelDescriptionsNew)
    }

    val modelList = when (apiType) {
        ApiType.OPENAI -> openaiModels
        ApiType.ANTHROPIC -> anthropicModels
        ApiType.GOOGLE -> googleModels
        ApiType.OLLAMA -> ollamaModels
    }
    val availableModels = when (apiType) {
        ApiType.OPENAI -> generateOpenAIModelList(models = modelList)
        ApiType.ANTHROPIC -> generateAnthropicModelList(models = modelList)
        ApiType.GOOGLE -> generateGoogleModelList(models = modelList)
        ApiType.OLLAMA -> generateOllamaModelList(models = modelList, descriptions = ollamaModelDescriptions)
    }
    val configuration = LocalConfiguration.current

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 40.dp),
        title = { Text(text = stringResource(R.string.api_model)) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                availableModels.forEach { m ->
                    RadioItem(
                        value = m.aliasValue,
                        selected = model == m.aliasValue,
                        title = m.name,
                        description = m.description,
                        onSelected = { onModelSelected(it) }
                    )
                }
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                enabled = model.isNotBlank() && model in modelList,
                onClick = { onConfirmRequest(model) }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun TemperatureDialog(
    apiType: ApiType,
    temperature: Float,
    onDismissRequest: () -> Unit,
    onConfirmRequest: (temp: Float) -> Unit
) {
    val configuration = LocalConfiguration.current
    var textFieldTemperature by remember { mutableStateOf(temperature.toString()) }
    var sliderTemperature by remember { mutableFloatStateOf(temperature) }

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 40.dp),
        title = { Text(text = stringResource(R.string.temperature_setting)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(stringResource(R.string.temperature_setting_description))
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    value = textFieldTemperature,
                    onValueChange = { t ->
                        textFieldTemperature = t
                        val converted = t.toFloatOrNull()
                        converted?.let {
                            sliderTemperature = when (apiType) {
                                ApiType.ANTHROPIC -> it.coerceIn(0F, 1F)
                                else -> it.coerceIn(0F, 2F)
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = {
                        Text(stringResource(R.string.temperature))
                    }
                )
                Slider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    value = sliderTemperature,
                    valueRange = when (apiType) {
                        ApiType.ANTHROPIC -> 0F..1F
                        else -> 0F..2F
                    },
                    steps = when (apiType) {
                        ApiType.ANTHROPIC -> 10 - 1
                        else -> 20 - 1
                    },
                    onValueChange = { t ->
                        sliderTemperature = t
                        textFieldTemperature = t.toString()
                    }
                )
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = { onConfirmRequest(sliderTemperature) }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun TopPDialog(
    topP: Float?,
    onDismissRequest: () -> Unit,
    onConfirmRequest: (topP: Float) -> Unit
) {
    val configuration = LocalConfiguration.current
    var textFieldTopP by remember { mutableStateOf((topP ?: 1F).toString()) }
    var sliderTopP by remember { mutableFloatStateOf(topP ?: 1F) }

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 40.dp),
        title = { Text(text = stringResource(R.string.top_p_setting)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(stringResource(R.string.top_p_setting_description))
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    value = textFieldTopP,
                    onValueChange = { p ->
                        textFieldTopP = p
                        p.toFloatOrNull()?.let {
                            val rounded = (it.coerceIn(0.1F, 1F) * 100).roundToInt() / 100F
                            sliderTopP = rounded
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = {
                        Text(stringResource(R.string.top_p))
                    }
                )
                Slider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    value = sliderTopP,
                    valueRange = 0.1F..1F,
                    steps = 8,
                    onValueChange = { t ->
                        val rounded = (t * 100).roundToInt() / 100F
                        sliderTopP = rounded
                        textFieldTopP = rounded.toString()
                    }
                )
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = { onConfirmRequest(sliderTopP) }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun SystemPromptDialog(
    prompt: String,
    onDismissRequest: () -> Unit,
    onConfirmRequest: (text: String) -> Unit
) {
    val configuration = LocalConfiguration.current
    var textFieldPrompt by remember { mutableStateOf(prompt) }

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 40.dp),
        title = { Text(text = stringResource(R.string.system_prompt_setting)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(stringResource(R.string.system_prompt_description))
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    value = textFieldPrompt,
                    onValueChange = { textFieldPrompt = it },
                    label = {
                        Text(stringResource(R.string.system_prompt))
                    }
                )
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                enabled = textFieldPrompt.isNotBlank(),
                onClick = { onConfirmRequest(textFieldPrompt) }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
