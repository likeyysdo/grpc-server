package com.lncn.rsql.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Policy;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @Classname CaffeineCache
 * @Description TODO
 * @Date 2022/7/28 17:31
 * @Created by byco
 */
public enum CaffeineCache implements RCache {

    INSTANCE;

    Cache<String,Object> cache;


    CaffeineCache() {

    }



    @Override
    public Object getIfPresent(
        Object key) {
        return cache.getIfPresent(key);
    }


    @Override
    public Object get(
        String key,
        Function<? super String, ?> mappingFunction) {
        return cache.get(key, mappingFunction);
    }

    @Override
    public Map<String, Object> getAllPresent(
        Iterable<?> keys) {
        return cache.getAllPresent(keys);
    }

    @Override
    public Map<String, Object> getAll(
        Iterable<? extends String> keys,
        Function<Iterable<? extends String>, Map<String, Object>> mappingFunction) {
        return cache.getAll(keys, mappingFunction);
    }

    @Override
    public void put(String key,
                    Object value) {
        cache.put(key, value);
    }

    @Override
    public void putAll(
        Map<? extends String, ?> map) {
        cache.putAll(map);
    }

    @Override
    public void invalidate(Object key) {
        cache.invalidate(key);
    }

    @Override
    public void invalidateAll(
        Iterable<?> keys) {
        cache.invalidateAll(keys);
    }

    @Override
    public void invalidateAll() {
        cache.invalidateAll();
    }


    @Override
    public long estimatedSize() {
        return cache.estimatedSize();
    }

    @Override
    public CacheStats stats() {
        return cache.stats();
    }

    @Override
    public ConcurrentMap<String, Object> asMap() {
        return cache.asMap();
    }

    @Override
    public void cleanUp() {
        cache.cleanUp();
    }

    @Override
    public Policy<String, Object> policy() {
        return cache.policy();
    }



}
