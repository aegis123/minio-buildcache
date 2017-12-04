package com.drost.buildcache.minio

import com.drost.gradle.buildcache.minio.internal.MinioBuildCacheServiceFactory
import com.drost.gradle.buildcache.minio.MinioBuildCache
import com.drost.gradle.buildcache.minio.MinioPlugin
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.gradle.api.initialization.Settings
import org.gradle.caching.configuration.BuildCacheConfiguration
import org.junit.Test

class MinioPluginTest {
    @Test
    fun testApplyPlugin() {
        val settings: Settings = mock()
        val conf: BuildCacheConfiguration = mock()
        whenever(settings.buildCache).thenReturn(conf)

        val plugin = MinioPlugin();
        plugin.apply(settings)

        verify(conf).registerBuildCacheService(MinioBuildCache::class.java, MinioBuildCacheServiceFactory::class.java)
    }
}