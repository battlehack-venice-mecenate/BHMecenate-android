package com.battlehack_venice.mecenate.main;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.battlehack_venice.lib.model.Monument;
import com.battlehack_venice.lib.api.ApiClient;
import com.battlehack_venice.lib.api.ApiResponseListParser;
import com.battlehack_venice.mecenate.Application;
import com.battlehack_venice.mecenate.BaseActivity;
import com.battlehack_venice.mecenate.R;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends BaseActivity
{
    private static final int COLUMNS = 2;

    @InjectView(R.id.list)
    RecyclerView _list;

    @Inject
    ApiClient _apiClient;

    private MonumentsAdapter _adapter;
    private Subscription _subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main_activity);

        ButterKnife.inject(this);
        Application.injector().inject(this);

        if (this._adapter == null) {
            this._adapter = new MonumentsAdapter();
            this._adapter.onCreate(this);
            this._adapter.onViewCreated(this, savedInstanceState);

            // DEBUG:
            /*
            this._adapter.add(new Monument(0).setName("Fenice").setImageUrl("http://www.bertoliniarte.it/img/cmedia_cache/170/500xauto/imageresize/fenice_004.jpg"));
            this._adapter.add(new Monument(1).setName("Piazza SanMarco").setImageUrl("http://www.countryhousecortesia.it/j25/images/itinerari/venezia/piazza%20san%20marco%202.jpg"));
            this._adapter.add(new Monument(2).setName("Ponte di Rialto").setImageUrl("http://blog.easytobook.com/wp-content/uploads/2011/07/Ponte-di-Rialto-01.jpg"));
            */

            this._load();
        }

        this._list.setAdapter(this._adapter);
        this._list.setLayoutManager(new GridLayoutManager(this, COLUMNS));
    }

    @Override
    protected void onDestroy()
    {
        if (this._subscription != null) {
            this._subscription.unsubscribe();
            this._subscription = null;
        }

        this._adapter.onDestroyView(this);

        super.onDestroy();
    }

    private void _load()
    {
        this._subscription = this._apiClient.get("/pois", null, new ApiResponseListParser<>(Monument.PARSER, "pois"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Monument>>()
                {
                    @Override
                    public void onCompleted()
                    {
                        _adapter.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        _adapter.onError(e);
                    }

                    @Override
                    public void onNext(List<Monument> monuments)
                    {
                        for (Monument m : monuments) {
                            _adapter.onNext(m);
                        }
                    }
                });
    }
}
