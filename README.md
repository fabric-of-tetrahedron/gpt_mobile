<div align="center">

<img width="200" height="200" style="display: block;" src="./images/logo.png">

# GPT Mobile

### 支持与多个模型聊天的安卓聊天助手。

[![GitHub all releases](https://img.shields.io/github/downloads/Taewan-P/gpt_mobile/total?label=Downloads&logo=github)](https://github.com/Taewan-P/gpt_mobile/releases/)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/Taewan-P/gpt_mobile?color=black&label=Stable&logo=github)](https://github.com/Taewan-P/gpt_mobile/releases/latest/)

</div>


## 截图

<div align="center">

<img style="display: block;" src="./images/screenshots.png">

</div>

## 演示


| <video src="https://github.com/Taewan-P/gpt_mobile/assets/27392567/96229e6d-6795-48b4-a915-aca915bd2527"/> | <video src="https://github.com/Taewan-P/gpt_mobile/assets/27392567/1cc13413-7320-4f6f-ace9-de76de58adcc"/> | <video src="https://github.com/Taewan-P/gpt_mobile/assets/27392567/546e2694-953d-4d67-937f-a29fba81046f"/> |
|------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------|


## 功能

- **同时与多个模型聊天**
  - 使用各平台的官方API
  - 支持的平台：
    - OpenAI GPT
    - Anthropic Claude
    - Google Gemini
    - Ollama
  - 可以自定义温度、top p（核采样）和系统提示
- 本地聊天记录
  - 聊天记录**仅保存在本地**
  - 聊天时仅发送到官方API服务器
- [Material You](https://m3.material.io/) 风格的UI，图标
  - 支持深色模式，系统动态主题**无需重启活动**
- 安卓13+的每个应用语言设置
- 100% Kotlin，Jetpack Compose，单活动，[现代应用架构](https://developer.android.com/topic/architecture#modern-app-architecture) 在安卓开发者文档中


## 待支持

- 安卓12及以下的手动语言设置
- 更多平台
- 支持多模态模型的图像、文件

如果您有任何功能请求，请打开一个问题。


## 下载

您可以从以下网站下载该应用：

[<img height="80" alt="Get it on F-Droid" src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"/>](https://f-droid.org/packages/dev.chungjungsoo.gptmobile)
[<img height="80" alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png'/>](https://play.google.com/store/apps/details?id=dev.chungjungsoo.gptmobile&utm_source=github&utm_campaign=gh-readme)
[<img height="80" alt='Get it on GitHub' src='https://raw.githubusercontent.com/Kunzisoft/Github-badge/main/get-it-on-github.png'/>](https://github.com/Taewan-P/gpt_mobile/releases)

支持跨平台更新。然而，GitHub Releases 将是最快的更新渠道，因为没有验证/审核过程。（可能有1周的差异？）


## 构建

1. 克隆仓库
2. 在Android Studio中打开
3. 点击 `Run` 或进行Gradle构建


## 许可证

详情请参阅 [LICENSE](./LICENSE)。

[F-Droid 图标许可证](https://gitlab.com/fdroid/artwork/-/blob/master/fdroid-logo-2015/README.md)

