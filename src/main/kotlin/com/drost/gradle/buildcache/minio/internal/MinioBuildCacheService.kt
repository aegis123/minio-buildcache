package com.drost.gradle.buildcache.minio.internal

import io.minio.MinioClient
import org.gradle.caching.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.logging.Level
import java.util.logging.Logger

class MinioBuildCacheService(val minioClient: MinioClient, val bucket: String) : BuildCacheService {
    private val BUILD_CACHE_CONTENT_TYPE = "application/vnd.gradle.build-cache-artifact"

    override fun store(key: BuildCacheKey?, writer: BuildCacheEntryWriter?) {
        println("store to $bucket")
        writer?.let {

            try {
                val os = ByteArrayOutputStream(writer.size.toInt())
                writer.writeTo(os)

                val inputStream = ByteArrayInputStream(os.toByteArray())
                key?.let {
                    println(it.displayName)
                    println(it.hashCode)
                    println("filesize " + writer.size)
                    minioClient.putObject(bucket, it.hashCode, inputStream, writer.size, BUILD_CACHE_CONTENT_TYPE)
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
        println("load from $bucket")
        key?.let {
            println(it.displayName)
            println(it.hashCode)

            val hashCode = it.hashCode
            try {
                minioClient.statObject(bucket, hashCode)
            } catch (error: Exception) {
                Logger.getGlobal().log(Level.WARNING, "could not find cached file", error)
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