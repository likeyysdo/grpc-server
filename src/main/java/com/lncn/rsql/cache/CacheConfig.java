package com.lncn.rsql.cache;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.vertx.core.cli.annotations.DefaultValue;
import javax.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * @Classname CacheConfig
 * @Description TODO
 * @Date 2022/7/28 17:20
 * @Created by byco
 */
@StaticInitSafe
@ConfigMapping(prefix = "config.rsql-cache")
public interface CacheConfig {

    @ConfigProperty( defaultValue = "60")
    int expireTime();

    @ConfigProperty( defaultValue = "100")
    int maxSize();
}
