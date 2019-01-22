package com.ashideas.httplibrary.cache;

import android.util.LruCache;

import com.ashideas.httplibrary.EncryptUtils;

public class LruCacheManager implements CacheManager {

    private LruCache<String, byte[]> cache;

    public LruCacheManager() {
        this(10 * 1024 * 1024);
    }
    public LruCacheManager(int memoryCacheSize) {
        cache = new LruCache<String, byte[]>(memoryCacheSize) {
            @Override
            protected int sizeOf(String key, byte[] data) {
                return data.length;
            }
        };
    }

    public byte[] getFromCache(String url) {
        return cache.get(EncryptUtils.sha1(url));
    }

    @Override
    public void putInCache(String url, byte[] data) {
        cache.put(EncryptUtils.sha1(url), data);
    }
}