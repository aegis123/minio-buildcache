package com.drost.gradle.buildcache.minio.internal

import io.minio.MinioClient
import mu.KotlinLogging
import org.gradle.caching.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

class MinioBuildCacheService(private val minioClient: MinioClient, private val bucket: String) : BuildCacheService {
    private var createBucketFirst = true
    private companion object {
        const val buildCacheContentType = "application/vnd.gradle.build-cache-artifact"
        val logger = KotlinLogging.logger {}
    }

    override fun store(key: BuildCacheKey, writer: BuildCacheEntryWriter) {
        try {
            if (createBucketFirst && !minioClient.bucketExists(bucket)) {
                minioClient.makeBucket(bucket)
                createBucketFirst = false
            }
            val os = ByteArrayOutputStream()
            writer.writeTo(os)

            val inputStream = ByteArrayInputStream(os.toByteArray())

            minioClient.putObject(bucket, key.hashCode, inputStream, os.size().toLong(), buildCacheContentType)

            logger.debug { "Stored cache file ${key.hashCode} to $bucket" }
        } catch (error: IOException) {
            throw BuildCacheException("Error while storing cache object in Minio bucket", error)
        }
    }

    override fun load(key: BuildCacheKey, reader: BuildCacheEntryReader): Boolean {
        return try {
            if (createBucketFirst && !minioClient.bucketExists(bucket)) {
                minioClient.makeBucket(bucket)
                createBucketFirst = false
            }
            minioClient.statObject(bucket, key.hashCode)
            try {
                val inputStream = minioClient.getObject(bucket, key.hashCode)
                reader.readFrom(inputStream)
            } catch (error: IOException) {
                throw BuildCacheException("Error while reading file cache object from Minio S3 bucket", error)
            }
            true
        } catch (error: Exception) {
            false
        }
    }

    override fun close() {
        // don't close the MinioClient.
    }
}