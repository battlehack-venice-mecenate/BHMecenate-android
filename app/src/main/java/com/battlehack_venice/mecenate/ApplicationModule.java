package com.battlehack_venice.mecenate;

import android.content.Context;

import com.battlehack_venice.lib.api.ApiClient;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule
{
    private Application _application;

    public ApplicationModule(Application application)
    {
        this._application = application;
    }

    @Provides
    @Singleton
    Context provideApplicationContext()
    {
        return this._application.getApplicationContext();
    }

    @Provides
    @Singleton
    OkHttpClient provideHttpClient(Context context)
    {
        OkHttpClient client = new OkHttpClient();

        // Disable automatic redirect follow
        client.setFollowRedirects(false);

        // Setup cache
        // TODO: adjust size and folder
        final long HTTP_CACHE_SIZE = 10 * 1024 * 1024; // 10 MiB
        File cacheDir = context.getDir("http_cache", Context.MODE_PRIVATE);

        client.setCache(new Cache(cacheDir, HTTP_CACHE_SIZE));

        // Setup timeouts
        client.setConnectTimeout(5, TimeUnit.SECONDS);
        client.setWriteTimeout(30, TimeUnit.SECONDS);
        client.setReadTimeout(30, TimeUnit.SECONDS);

        return client;
    }

    @Provides
    @Singleton
    ApiClient provideApiClient(OkHttpClient client)
    {
        return new ApiClient(client, "https://api.mecenate.org");
    }
}