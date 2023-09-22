package com.rover12421.android.plugins.namehash.core

data class HashData(val len: Int,
                    val hashcode: Int,
                    val hash: String) {

    companion object {
        @JvmStatic
        fun of(ann: HashValue): HashData = HashData(ann.len, ann.hashcode, ann.hash)
    }
}