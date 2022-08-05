package com.lncn.rsql.cache;


import com.github.benmanes.caffeine.cache.Policy;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @Classname R1Cache
 * @Description TODO
 * @Date 2022/7/28 22:12
 * @Created by byco
 */
public interface RCache {

    Object getIfPresent(
        Object key);

    Object get(
        String key,
        Function<? super String, ?> mappingFunction);

    Map<String, Object> getAllPresent(
        Iterable<?> keys);

    Map<String, Object> getAll(
        Iterable<? extends String> keys,
        Function<Iterable<? extends String>, Map<String, Object>> mappingFunction);

    void put(String key,
             Object value);

    void putAll(
        Map<? extends String, ?> map);

    void invalidate(Object key);

    void invalidateAll(
        Iterable<?> keys);

    void invalidateAll();

    long estimatedSize();

    CacheStats stats();

    ConcurrentMap<String, Object> asMap();

    void cleanUp();

    Policy<String, Object> policy();
}
