package com.rover12421.android.plugins.namehash.plugin

import org.apache.commons.codec.digest.MurmurHash3

interface HashAlgorithm<out HashType: Any> {
    fun hash(data: String, param: Map<String, Any> = emptyMap()): HashType

    fun algorithmName(): String

    companion object {
        val Default: HashAlgorithm<Int> = object : HashAlgorithm<Int> {
            override fun hash(data: String, param: Map<String, Any>): Int {
                return data.hashCode()
            }

            override fun algorithmName(): String {
                return "Default"
            }
        }
        val MurmurHash32 = object : HashAlgorithm<Long> {
            override fun hash(data: String, param: Map<String, Any>): Long {
                var seed = MurmurHash3.DEFAULT_SEED
                if (param.containsKey("seed")) {
                    seed = param["seed"].toString().toInt()
                }
                val bytes = data.toByteArray()
                return MurmurHash3.hash32x86(bytes, 0, bytes.size, seed).toLong()
            }

            override fun algorithmName(): String {
                return "MurmurHash32"
            }

        }
    }
}