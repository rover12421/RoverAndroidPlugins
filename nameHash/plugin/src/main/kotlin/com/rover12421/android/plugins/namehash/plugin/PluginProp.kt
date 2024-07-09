package com.rover12421.android.plugins.namehash.plugin

open class PluginProp {
    var debug: Boolean = false

    /**
     * 是否处理所有项目和依赖class
     * 默认false, 只处理当前项目
     */
    var allProject: Boolean = false

    /**
     * 过滤class规则列表
     */
    var filter: MutableList<String> = mutableListOf()

    /**
     * filter 列表是否是正则
     * 默认false,使用class全名前缀匹配
     */
    var filterRegex: Boolean = false

    /**
     * hash 处理对象
     */
    val hash: HashProp = HashProp()
}

open class HashProp {
    /**
     * hash 算法实现对象
     */
    var algorithm: HashAlgorithm<Any> = HashAlgorithm.Default

    /**
     * hash 算法可选参数
     */
    var args: MutableMap<String, Any> = mutableMapOf()

    fun hash(data: String): Any {
        return algorithm.hash(data, args)
    }
}