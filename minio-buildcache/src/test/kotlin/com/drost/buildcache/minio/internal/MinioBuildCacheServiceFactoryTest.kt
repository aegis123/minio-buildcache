package com.drost.buildcache.minio.internal

import com.drost.gradle.buildcache.minio.MinioBuildCache
import com.drost.gradle.buildcache.minio.internal.MinioBuildCacheServiceFactory
import junit.framework.TestCase.assertNotNull
import org.gradle.caching.BuildCacheServiceFactory.Describer
import org.junit.Before
import org.junit.Test


class MinioBuildCacheServiceFactoryTest {
    private lateinit var subject: MinioBuildCacheServiceFactory
    private lateinit var buildCacheDescriber: Describer

    @Before
    fun setup() {
        subject = MinioBuildCacheServiceFactory()
        buildCacheDescriber = NoopBuildCacheDescriber()
    }

    @Test
    fun testHappyPath() {
        val conf = MinioBuildCache()

        conf.accessKey = "random_access_key"
        conf.secretKey = "random_secret_key"
        conf.endpoint = "10.10.10.10"
        conf.bucket = "random.bucket"

        val service = subject.createBuildCacheService(conf, buildCacheDescriber)

        assertNotNull(service)
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testIllegalConfigWithoutAccessKey() {
        val conf = MinioBuildCache()
        conf.secretKey = "random_secret_key"
        conf.endpoint = "random_endpoint"
        conf.bucket = "random_bucket"

        subject.createBuildCacheService(conf, buildCacheDescriber)
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testIllegalConfigWithoutBucket() {
        val conf = MinioBuildCache()
        conf.accessKey = "random_access_key"
        conf.secretKey = "random_secret_key"
        conf.endpoint = "random_endpoint"

        subject.createBuildCacheService(conf, buildCacheDescriber)
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testIllegalConfigWithoutSecretKey() {
        val conf = MinioBuildCache()
        conf.accessKey = "random_access_key"
        conf.endpoint = "random_endpoint"
        conf.bucket = "random_bucket"

        subject.createBuildCacheService(conf, buildCacheDescriber)
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(Exception::class)
    fun testIllegalConfigWithoutEndpoint() {
        val conf = MinioBuildCache()
        conf.accessKey = "random_access_key"
        conf.secretKey = "random_secret_key"
        conf.bucket = "random_bucket"

        subject.createBuildCacheService(conf, buildCacheDescriber)
    }

    private inner class NoopBuildCacheDescriber : Describer {

        override fun type(type: String): Describer {
            return this
        }

        override fun config(name: String, value: String): Describer {
            return this
        }
    }
}