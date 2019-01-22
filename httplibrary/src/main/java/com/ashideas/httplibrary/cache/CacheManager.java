package com.ashideas.httplibrary.cache;

public interface CacheManager {
    byte[] getFromCache(String url);
    void putInCache(String url, byte[] data);
}
