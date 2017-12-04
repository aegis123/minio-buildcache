package com.drost.gradle.buildcache.minio

import com.drost.gradle.buildcache.minio.internal.MinioBuildCacheServiceFactory
import mu.KLogging
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings


class MinioPlugin : Plugin<Settings> {
    companion object : KLogging()

    override fun apply(settings: Settings?) {
        logger.info { "Apply MinioPlugin" }
        settings?.let {
            val buildCache = it.buildCache
            buildCache.registerBuildCacheService(MinioBuildCache::class.java, MinioBuildCacheServiceFactory::class.java)
        }
    }
}