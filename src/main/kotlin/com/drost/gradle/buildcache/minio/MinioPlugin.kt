package com.drost.gradle.buildcache.minio

import com.drost.gradle.buildcache.minio.internal.MinioBuildCacheServiceFactory
import mu.KLogging
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class MinioPlugin : Plugin<Settings> {
    companion object : KLogging()

    override fun apply(settings: Settings) {
        logger.info { "apply cache plugin" }
        val buildCache = settings.buildCache
        buildCache.registerBuildCacheService(MinioBuildCache::class.java, MinioBuildCacheServiceFactory::class.java)
    }
}