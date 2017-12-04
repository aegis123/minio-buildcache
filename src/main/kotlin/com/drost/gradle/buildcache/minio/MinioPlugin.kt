package com.drost.gradle.buildcache.minio

import com.drost.gradle.buildcache.minio.internal.MinioBuildCacheServiceFactory
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class MinioPlugin : Plugin<Settings> {

    override fun apply(settings: Settings?) {
        println("apply cache plugin")
        settings?.let {
            val buildCache = it.buildCache
            buildCache.registerBuildCacheService(MinioBuildCache::class.java, MinioBuildCacheServiceFactory::class.java)
        }
    }
}