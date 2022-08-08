package com.lncn.rsql.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Classname Cache
 * @Description TODO
 * @Date 2022/7/28 17:26
 * @Created by byco
 */

@Singleton
public class RCacheManager {

    private static final Logger log = LoggerFactory.getLogger(RCacheManager.class);
    public static final int DEFAULT_MAX_SIZE = 100;
    public static final int DEFAULT_EXPIRE_TIME = 60;

    @Inject
    CacheConfig cacheConfig;

    public RCache getDefaultCache(){
        return getCaffeineCache();
    }


    @PostConstruct
    void initCaffeineCache(){
        log.debug("initCaffeineCache");
        int expireTime = DEFAULT_EXPIRE_TIME;
        if( cacheConfig.expireTime() != 0 ){
            expireTime = cacheConfig.expireTime();
        }
        int maxSize = DEFAULT_MAX_SIZE;
        if( cacheConfig.maxSize() != 0 ){
            maxSize = cacheConfig.maxSize();
        }
        CaffeineCache.INSTANCE.cache = Caffeine.newBuilder()
            .maximumSize(maxSize)
            .expireAfterWrite(expireTime, TimeUnit.SECONDS)
            .evictionListener((key,value,callback)->{
                log.info("CaffeineCache expired：key:{}", key );
            })
            .build();
        log.debug("initCaffeineCache finish with expireTime:{} maxSize:{}",expireTime,maxSize);
    }

    public RCache getCaffeineCache(){
        return CaffeineCache.INSTANCE;
    }
}
