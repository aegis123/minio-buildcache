# Minio Gradle build cache

[![Apache License 2.0](https://img.shields.io/badge/License-Apache%20License%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.html) [![Build Status](https://travis-ci.org/aegis123/minio-buildcache.svg?branch=master)](https://travis-ci.org/aegis123/minio-buildcache)

This is a custom Gradle [build cache](https://docs.gradle.org/current/userguide/build_cache.html)
implementation which uses [Minio Object Storage](https://minio.io/) to store the cache objects.


## Compatibility

Plugin made with gradle 4.5.1 so should work for 4.0 and up.
Might work with gradle version 3.5 and up but haven't tested.


## Use in your project

Please note that this plugin is not yet ready for production. Feedback though is very welcome.
Please open an [issue](https://github.com/aegis123/minio-buildcache/issues/new) if you find a bug or
have an idea for an improvement.


### Apply plugin

The Gradle build cache needs to be configured on the Settings level. As a first step, add a
dependency to the plugin to your `settings.gradle` file:

```
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
    jcenter()
  }
  dependencies {
    classpath "gradle.plugin.com.drost.gradle:minio-buildcache:1.0"
  }
}
```

### Configuration

The Minio build cache implementation has a few configuration options:

| Configuration Key | Description | Mandatory |Default Value |
| ----------------- | ----------- | --------- | ----------- |
| endpoint | The endpoint where Minio is hosted. | yes | |
| access key | The minio access key. | yes | |
| secret key | The minio secret key. | yes | |
| bucket | The name of the AWS S3 bucket where cache objects should be stored. | yes | |
| region | The region the server is hosted | yes | |

(Options without a default value are mandatory.)


The `buildCache` configuration block might look like this:

```
apply plugin: 'com.drost.gradle.minio-buildcache'

ext.isCiServer = System.getenv().containsKey("CI")

buildCache {
    local {
        enabled = !isCiServer
    }
    remote(com.drost.gradle.buildcache.minio.MinioBuildCache) {
        endpoint='https://url-to-your-minio-server'
        accessKey='your-accesskey'
        secretKey='your-secretkey'
        bucket='bucketname'
        region='some-region'
        push=isCiServer
    }
}
```

More details about configuring the Gradle build cache can be found in the
[official Gradle documentation](https://docs.gradle.org/current/userguide/build_cache.html#sec:build_cache_configure).

### Run build with build cache

The Gradle build cache is an incubating feature and needs to be enabled per build (`--build-cache`)
or in the Gradle properties (`org.gradle.caching=true`).

Example:

```
$> gradle --build-cache assemble
Build cache is an incubating feature.
```


## Contributing

Contributions are always welcome! If you'd like to contribute (and we hope you do) please open a pull request.


## License

```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
