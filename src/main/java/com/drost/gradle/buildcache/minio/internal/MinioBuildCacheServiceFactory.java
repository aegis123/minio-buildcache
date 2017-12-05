package com.drost.gradle.buildcache.minio.internal;

import com.drost.gradle.buildcache.minio.MinioBuildCache;
import com.drost.gradle.buildcache.minio.MinioPlugin;
import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.gradle.caching.BuildCacheException;
import org.gradle.caching.BuildCacheService;
import org.gradle.caching.BuildCacheServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MinioBuildCacheServiceFactory implements BuildCacheServiceFactory<MinioBuildCache> {
    private static final Logger logger = LoggerFactory.getLogger(MinioBuildCacheServiceFactory.class);
    @Override
    public BuildCacheService createBuildCacheService(@Nonnull MinioBuildCache config, @Nonnull Describer describer) {
        verifyConfig(config);

        describer.type("Minio Object store")
                .config("Endpoint", config.getEndpoint())
                .config("Access key", config.getAccessKey())
                .config("Secret key", config.getSecretKey())
                .config("Bucket", config.getBucket());

        try {
            MinioClient client = new MinioClient(config.getEndpoint(), config.getAccessKey(), config.getSecretKey());
            return new MinioBuildCacheService(client, config.getBucket());
        } catch (InvalidEndpointException | InvalidPortException e) {
            throw new BuildCacheException("could not create MinioClient because invalid config values", e);
        }

    }

    private void verifyConfig(@Nonnull MinioBuildCache config) {
        if (isNullOrEmpty(config.getEndpoint())) {
            throw new IllegalStateException("Minio build cache has no endpoint configured");
        }
        if (isNullOrEmpty(config.getAccessKey())) {
            throw new IllegalStateException("Minio buil dcache has no Access key configured");
        }
        if (isNullOrEmpty(config.getSecretKey())) {
            throw new IllegalStateException("Minio build cache has no Secret key configured");
        }
        if (isNullOrEmpty(config.getBucket())) {
            throw new IllegalStateException("Minio build cache has no Bucket configured");
        }
    }

    private boolean isNullOrEmpty(@Nullable String val) {
        return val == null || val.isEmpty();
    }

}
