package com.drost.gradle.buildcache.minio;

import com.drost.gradle.buildcache.minio.internal.MinioBuildCacheServiceFactory;
import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;
import org.gradle.caching.configuration.BuildCacheConfiguration;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinioPlugin implements Plugin<Settings> {
    private static final Logger logger = LoggerFactory.getLogger(MinioPlugin.class);
    @Override
    public void apply(@NotNull Settings target) {
        logger.info("Apply Minio Build Cache to project");
        BuildCacheConfiguration buildCache = target.getBuildCache();
        buildCache.registerBuildCacheService(MinioBuildCache.class, MinioBuildCacheServiceFactory.class);
    }
}
