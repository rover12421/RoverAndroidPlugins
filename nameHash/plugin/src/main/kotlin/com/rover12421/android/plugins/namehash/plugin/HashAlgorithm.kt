package com.rover12421.android.plugins.namehash.plugin

interface HashAlgorithm {
    fun hash(data: String, param: Map<String, Any> = emptyMap()): String

    companion object {
        val DEFAULT: HashAlgorithm = object : HashAlgorithm {
            override fun hash(data: String, param: Map<String, Any>): String {
                return data
            }
        }
    }
}