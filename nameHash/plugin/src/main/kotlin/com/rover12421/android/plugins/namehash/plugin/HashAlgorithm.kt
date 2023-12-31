package com.rover12421.android.plugins.namehash.plugin

import org.apache.commons.codec.digest.MurmurHash3

interface HashAlgorithm {
    fun hash(data: String, param: Map<String, Any> = emptyMap()): String

    fun algorithmName(): String

    companion object {
        val Default: HashAlgorithm = object : HashAlgorithm {
            override fun hash(data: String, param: Map<String, Any>): String {
                return data
            }

            override fun algorithmName(): String {
                return "Default"
            }
        }
        val MurmurHash32 = object : HashAlgorithm {
            override fun hash(data: String, param: Map<String, Any>): String {
                var seed = MurmurHash3.DEFAULT_SEED
                if (param.containsKey("seed")) {
                    seed = param["seed"].toString().toInt()
                }
                val bytes = data.toByteArray()
                return MurmurHash3.hash32x86(bytes, 0, bytes.size, seed).toString(16)
            }

            override fun algorithmName(): String {
                return "MurmurHash32"
            }

        }
    }
}