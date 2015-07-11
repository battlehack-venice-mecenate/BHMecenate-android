package com.battlehack_venice.mecenate;


import com.battlehack_venice.mecenate.main.MainActivity;
import com.battlehack_venice.mecenate.main.MonumentsAdapter;
import com.battlehack_venice.mecenate.monument.MonumentActivity;

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
    void inject(MonumentsAdapter adapter);

    // Monument
    void inject(MonumentActivity activity);
}
