package com.battlehack_venice.mecenate;

import android.content.Context;

import com.battlehack_venice.lib.api.ApiClient;

import javax.inject.Inject;


public class Application extends android.support.multidex.MultiDexApplication
{
    private static Context context;
    private static ApplicationComponent appComponent;

    @Inject
    ApiClient _apiClient;

    @Override
    public void onCreate()
    {
        super.onCreate();

        Application.context = getApplicationContext();

        // Initalize app component (dependency injector)
        Application.appComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        appComponent.inject(this);
    }

    public static final ApplicationComponent injector()
    {
        if (appComponent == null) {
            throw new IllegalStateException("Global app injector should be initialised in onCreate()");
        }

        return appComponent;
    }
}