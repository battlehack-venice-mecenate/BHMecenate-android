package com.battlehack_venice.lib;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.battlehack_venice.mecenate.BaseActivity;

import rx.Observer;

abstract public class RxAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements Observer<T>
{
    protected boolean _refreshing = false;

    private BaseActivity _activity;

    public void startRefreshing()
    {
        this._refreshing = true;
    }

    public BaseActivity getActivity()
    {
        return this._activity;
    }

    public Resources getResources()
    {
        return this._activity.getResources();
    }

    @Override
    public void onCompleted()
    {
    }

    @Override
    public void onError(Throwable e)
    {
    }

    @Override
    public void onNext(T t)
    {
    }

    public void onCreate(BaseActivity activity)
    {
        this._activity = activity;
    }

    public void onViewCreated(BaseActivity activity, Bundle savedInstanceState)
    {
    }

    public void onDestroyView(BaseActivity activity)
    {
    }

    public void onSaveInstanceState(BaseActivity activity, Bundle outState)
    {
    }
}

