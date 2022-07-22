package com.lncn.rsql.config;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;

/**
 * @Classname RsqlConfig
 * @Description TODO
 * @Date 2022/7/22 13:32
 * @Created by byco
 */
@StaticInitSafe
@ConfigMapping(prefix = "config.rsql")
public interface RsqlConfig {
    int defaultFetchSize();
    int minFetchSize();
    int maxFetchSize();
    //boolean defaultEnableCompress();
}
