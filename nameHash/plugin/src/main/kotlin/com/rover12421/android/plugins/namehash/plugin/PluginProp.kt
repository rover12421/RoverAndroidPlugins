package com.rover12421.android.plugins.namehash.plugin

open class PluginProp {
    var debug: Boolean = false
    var allProject: Boolean = false
    var filter: MutableList<String> = mutableListOf()
    val hash: HashProp = HashProp()
}

open class HashProp {
    var algorithm: HashAlgorithm = HashAlgorithm.DEFAULT
    var args: MutableMap<String, Any> = mutableMapOf()

    fun hash(data: String): String {
        return algorithm.hash(data, args)
    }
}