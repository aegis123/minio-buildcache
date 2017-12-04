package com.drost.gradle.buildcache.minio.internal

import io.minio.MinioClient
import mu.KotlinLogging
import org.gradle.caching.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

private val logger = KotlinLogging.logger {}

class MinioBuildCacheService(private val minioClient: MinioClient, private val bucket: String) : BuildCacheService {
    companion object {
        val buildCacheContentType = "application/vnd.gradle.build-cache-artifact"
    }

    override fun store(key: BuildCacheKey?, writer: BuildCacheEntryWriter?) {
        logger.info { "store to $bucket" }
        writer?.let {

            try {
                val os = ByteArrayOutputStream(writer.size.toInt())
                writer.writeTo(os)

                val inputStream = ByteArrayInputStream(os.toByteArray())
                key?.let {
                    logger.info { "name: ${it.displayName}" }
                    logger.info { "id: ${it.hashCode}" }
                    logger.info { "size: ${writer.size}" }
                    minioClient.putObject(bucket, it.hashCode, inputStream, writer.size, buildCacheContentType)
                }
            } catch (error: IOException) {
                throw BuildCacheException("Error while storing cache object in Minio bucket", error)
            }
        }
    }

    override fun close() {
        // don't close the MinioClient.
    }

    override fun load(key: BuildCacheKey?, reader: BuildCacheEntryReader?): Boolean {
        logger.info { "load from $bucket" }
        key?.let {
            logger.info { "name: ${it.displayName}" }
            logger.info { "id: ${it.hashCode}" }

            val hashCode = it.hashCode
            try {
                minioClient.statObject(bucket, hashCode)
            } catch (error: Exception) {
                logger.warn(error, {"Could not find cache item on remote"})
                return false
            }

            try {
                val inputStream = minioClient.getObject(bucket, hashCode)
                reader?.let {
                    it.readFrom(inputStream)
                    return true
                }
            } catch (error: IOException) {
                throw BuildCacheException("Error while reading file cache object from Minio S3 bucket", error)
            }
        }
        return false
    }
}