package com.battlehack_venice.mecenate;


import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent
{
    // Application
    void inject(Application activity);

    // Base classes
    void inject(BaseActivity activity);

    // Main
    void inject(MainActivity activity);
}
