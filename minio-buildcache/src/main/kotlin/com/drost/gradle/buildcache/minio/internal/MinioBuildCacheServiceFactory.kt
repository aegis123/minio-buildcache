package com.drost.gradle.buildcache.minio.internal

import com.drost.gradle.buildcache.minio.MinioBuildCache
import org.gradle.caching.BuildCacheService
import org.gradle.caching.BuildCacheServiceFactory
import io.minio.MinioClient

class MinioBuildCacheServiceFactory : BuildCacheServiceFactory<MinioBuildCache> {
    override fun createBuildCacheService(config: MinioBuildCache?, describer: BuildCacheServiceFactory.Describer?): BuildCacheService {
        describer?.let {
            @Suppress("NAME_SHADOWING")
            val describer = it
            config?.let {
                @Suppress("NAME_SHADOWING")
                val config = it
                describer.type("Minio Object store")
                        .config("Endpoint", config.endpoint)
                        .config("Access key", config.accessKey)
                        .config("Secret key", config.secretKey)
                        .config("Bucket", config.bucket)

                verifyConfig(it)

                val minioClient = createMinioClient(it)

                config.bucket?.let {
                    if (!minioClient.bucketExists(it)) {
                        minioClient.makeBucket(it)
                    }
                    return MinioBuildCacheService(minioClient, it)
                }
            }
        }
        throw IllegalStateException("could not create MinioBuildCache.")
    }

    private fun createMinioClient(config: MinioBuildCache): MinioClient
            = MinioClient(config.endpoint, config.accessKey, config.secretKey)

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


