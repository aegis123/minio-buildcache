package com.drost.gradle.buildcache.minio

import org.gradle.caching.configuration.AbstractBuildCache

open class MinioBuildCache : AbstractBuildCache() {
    var endpoint: String? = null
    var accessKey: String? = null
    var secretKey: String? = null
    var bucket: String? = null
}