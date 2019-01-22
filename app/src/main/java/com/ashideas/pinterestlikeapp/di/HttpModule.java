package com.ashideas.pinterestlikeapp.di;

import com.ashideas.httplibrary.cache.CacheManager;
import com.ashideas.httplibrary.cache.LruCacheManager;
import com.ashideas.httplibrary.client.HttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class HttpModule {

    @Provides
    CacheManager getCacheManager() {
        return new LruCacheManager(50 * 1024 * 1024);
    }

    @Provides
    @Singleton
    HttpClient getHttpClient(CacheManager manager) {
        return new HttpClient.Builder()
                .setCacheManager(manager)
                .build();
    }
}
