package com.drost.gradle.buildcache.minio.internal

import com.drost.gradle.buildcache.minio.MinioBuildCache
import org.gradle.caching.BuildCacheService
import org.gradle.caching.BuildCacheServiceFactory
import io.minio.MinioClient
import org.gradle.caching.BuildCacheException

class MinioBuildCacheServiceFactory : BuildCacheServiceFactory<MinioBuildCache> {
    override fun createBuildCacheService(config: MinioBuildCache, describer: BuildCacheServiceFactory.Describer): BuildCacheService {
        verifyConfig(config)

        describer.type("Minio Object store")
                .config("Endpoint", config.endpoint)
                .config("Access key", config.accessKey)
                .config("Secret key", config.secretKey)
                .config("Bucket", config.bucket)
                .config("region", config.region)

        val minioClient = if (config.region != null)
                                MinioClient(config.endpoint, config.accessKey, config.secretKey, config.region)
                            else
                                MinioClient(config.endpoint, config.accessKey, config.secretKey)

        val bucket = config.bucket
        bucket?.let {
            return MinioBuildCacheService(minioClient, it)
        }
        throw BuildCacheException("Could not create MinioBuildCacheService because bucket was null which should be possible since we verify for that")
    }

    private fun verifyConfig(config: MinioBuildCache) {
        if (config.endpoint.isNullOrEmpty()) {
            throw IllegalStateException("Minio build cache has no endpoint configured")
        }
        if (config.accessKey.isNullOrEmpty()) {
            throw IllegalStateException("Minio buil dcache has no Access key configured")
        }
        if (config.secretKey.isNullOrEmpty()) {
            throw IllegalStateException("Minio build cache has no Secret key configured")
        }
        if (config.bucket.isNullOrEmpty()) {
            throw IllegalStateException("Minio build cache has no Bucket configured")
        }
    }
}


