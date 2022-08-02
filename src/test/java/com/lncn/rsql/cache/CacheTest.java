package com.lncn.rsql.cache;

import static org.junit.jupiter.api.Assertions.*;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;


import io.quarkus.cache.CacheManager;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Duration;
import java.util.Optional;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

/**
 * @Classname CacheTest
 * @Description TODO
 * @Date 2022/7/28 15:08
 * @Created by byco
 */
 
class CacheTest {



    @Test
    public void D_Test(){
        //Arrange
        Cache<String, Object> cache = Caffeine.newBuilder()
            //指定初始大小
            .initialCapacity(1000)
            .build();
        cache.put("123",123);
        byte[] bytes = new byte[10];
        cache.put("a",bytes);
        System.out.println(cache.get("123",String::toString) );
        System.out.println(cache.get("a", x -> x));
        System.out.println(cache.get("asd", x -> "asd2222"));
        System.out.println(cache.get("asd", x -> "asd2221232"));
        //Act

        //Assert
    }
}