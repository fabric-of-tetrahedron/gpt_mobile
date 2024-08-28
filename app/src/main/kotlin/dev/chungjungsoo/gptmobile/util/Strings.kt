package dev.chungjungsoo.gptmobile.util

import android.util.Patterns
import android.webkit.URLUtil

/**
 * 检查字符串是否为有效的URL
 *
 * 此扩展函数用于验证一个字符串是否为合法的URL。
 * 它结合了Android的URLUtil和Patterns.WEB_URL来进行双重检查，
 * 以确保URL的格式正确且符合Web URL的标准模式。
 *
 * @return 如果字符串是有效的URL则返回true，否则返回false
 */
fun String.isValidUrl(): Boolean = URLUtil.isValidUrl(this) && Patterns.WEB_URL.matcher(this).matches()

// 函数实现说明：
// 1. URLUtil.isValidUrl(this)：使用Android提供的URLUtil工具类检查URL的基本有效性
// 2. Patterns.WEB_URL.matcher(this).matches()：使用Android的正则表达式模式further验证URL格式
// 3. 两个条件使用"与"(&&)操作符连接，确保URL同时满足两种检查方法
