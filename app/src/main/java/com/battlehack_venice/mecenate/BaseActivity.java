package com.battlehack_venice.mecenate;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public abstract class BaseActivity extends ActionBarActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Application.injector().inject(this);
    }
}
