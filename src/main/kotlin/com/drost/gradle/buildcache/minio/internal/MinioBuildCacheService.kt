package com.drost.gradle.buildcache.minio.internal

import io.minio.MinioClient
import mu.KotlinLogging
import org.gradle.caching.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.logging.Level
import java.util.logging.Logger

class MinioBuildCacheService(private val minioClient: MinioClient, private val bucket: String) : BuildCacheService {
    private companion object {
        val buildCacheContentType = "application/vnd.gradle.build-cache-artifact"
        val logger = KotlinLogging.logger {}
    }

    override fun store(key: BuildCacheKey, writer: BuildCacheEntryWriter) {
        try {
            val os = ByteArrayOutputStream()
            writer.writeTo(os)

            val inputStream = ByteArrayInputStream(os.toByteArray())

            minioClient.putObject(bucket, key.hashCode, inputStream, os.size().toLong(), buildCacheContentType)
            logger.info { "Stored cache file ${key.hashCode} to $bucket" }
        } catch (error: IOException) {
            throw BuildCacheException("Error while storing cache object in Minio bucket", error)
        }
    }

    override fun load(key: BuildCacheKey, reader: BuildCacheEntryReader): Boolean {
        return try {
            minioClient.statObject(bucket, key.hashCode)
            try {
                val inputStream = minioClient.getObject(bucket, key.hashCode)
                reader.readFrom(inputStream)
            } catch (error: IOException) {
                throw BuildCacheException("Error while reading file cache object from Minio S3 bucket", error)
            }
            true
        } catch (error: Exception) {
            logger.info("could not find cached file", error)
            false
        }
    }

    override fun close() {
        // don't close the MinioClient.
    }
}