package com.lncn.rsql.cache;

import static org.junit.jupiter.api.Assertions.*;

import io.quarkus.test.junit.QuarkusTest;
import javax.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @Classname CacheConfigTest
 * @Description TODO
 * @Date 2022/7/28 17:22
 * @Created by byco
 */
@QuarkusTest
class CacheConfigTest {

    @Inject
    CacheConfig cacheConfig;


    @Test
    void expireTime() {
        Assertions.assertNotEquals(0,cacheConfig.expireTime());
    }

    @Test
    void maxSize() {
        Assertions.assertNotEquals(0,cacheConfig.maxSize());
    }
}