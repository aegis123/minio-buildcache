package com.drost.gradle.buildcache.minio.internal;

import io.minio.MinioClient;
import io.minio.errors.*;
import org.gradle.caching.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParserException;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MinioBuildCacheService implements BuildCacheService {
    private final static Logger logger = LoggerFactory.getLogger(MinioBuildCacheService.class);
    private final static String BUILD_CACHE_MEDIA_TYPE = "application/vnd.gradle.build-cache-artifact";
    @Nonnull
    private final MinioClient client;
    @Nonnull
    private final String bucket;

    MinioBuildCacheService(@Nonnull final MinioClient client, @Nonnull final String bucket) {
        this.client = client;
        this.bucket = bucket;
    }

    @Override
    public boolean load(@Nonnull BuildCacheKey key, @Nonnull BuildCacheEntryReader reader) throws BuildCacheException {
        try {
            client.statObject(bucket, key.getHashCode());
        } catch (Exception e) {
            logger.debug("Could not find cache file on remote", e);
            return false;
        }

        try {
            final InputStream inputStream = client.getObject(bucket, key.getHashCode());
            reader.readFrom(inputStream);
            return true;
        } catch (Exception e) {
            throw new BuildCacheException("Error while reading file cache object from Minio S3 bucket", e);
        }
    }

    @Override
    public void store(@Nonnull BuildCacheKey key, @Nonnull BuildCacheEntryWriter writer) throws BuildCacheException {
        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(Math.toIntExact(writer.getSize()));
            writer.writeTo(outputStream);
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            client.putObject(bucket, key.getHashCode(), inputStream, writer.getSize(), BUILD_CACHE_MEDIA_TYPE);
            logger.debug(String.format("stored cache file in bucket %s with key: %s", bucket, key.getHashCode()));
        } catch (XmlPullParserException | InternalException | NoResponseException | ErrorResponseException | InvalidKeyException | InsufficientDataException | InvalidArgumentException | NoSuchAlgorithmException | InvalidBucketNameException | IOException e) {
            throw new BuildCacheException("Error while storing cache object in Minio bucket " + bucket, e);
        }
    }

    @Override
    public void close() throws IOException {
        // don't close the MinioClient since we want to reuse the instance
    }
}
