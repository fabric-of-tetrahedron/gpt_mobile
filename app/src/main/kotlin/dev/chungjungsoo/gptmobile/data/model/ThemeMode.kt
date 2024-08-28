package dev.chungjungsoo.gptmobile.data.model

/**
 * 主题模式枚举类
 *
 * 此枚举类定义了应用程序可用的主题模式。
 * 包括系统默认、深色模式和浅色模式。
 */
enum class ThemeMode {
    /** 跟随系统设置 */
    SYSTEM,

    /** 深色模式 */
    DARK,

    /** 浅色模式 */
    LIGHT;

    companion object {
        /**
         * 根据给定的整数值获取对应的主题模式
         *
         * @param value 整数值，对应枚举常量的序数
         * @return 对应的ThemeMode枚举常量，如果没有找到则返回null
         */
        fun getByValue(value: Int) = entries.firstOrNull { it.ordinal == value }
    }
}
