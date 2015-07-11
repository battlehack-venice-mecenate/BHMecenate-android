package com.battlehack_venice.mecenate;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.battlehack_venice.lib.Monument;
import com.battlehack_venice.lib.MonumentsAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends BaseActivity
{
    @InjectView(R.id.list)
    RecyclerView _list;

    private MonumentsAdapter _adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main_activity);

        ButterKnife.inject(this);
        
        
        if (this._adapter == null) {
            this._adapter = new MonumentsAdapter();
            this._adapter.onCreate(this);
            this._adapter.onViewCreated(this, savedInstanceState);

            this._adapter.add(new Monument(0).setName("Fenice").setImageUrl("http://www.bertoliniarte.it/img/cmedia_cache/170/500xauto/imageresize/fenice_004.jpg"));
            this._adapter.add(new Monument(1).setName("Piazza SanMarco").setImageUrl("http://www.countryhousecortesia.it/j25/images/itinerari/venezia/piazza%20san%20marco%202.jpg"));
            this._adapter.add(new Monument(2).setName("Ponte di Rialto").setImageUrl("http://blog.easytobook.com/wp-content/uploads/2011/07/Ponte-di-Rialto-01.jpg"));
        }

        this._list.setAdapter(this._adapter);
        this._list.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onDestroy()
    {
        this._adapter.onDestroyView(this);

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
