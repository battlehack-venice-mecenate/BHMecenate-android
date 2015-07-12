package com.battlehack_venice.mecenate.main;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.battlehack_venice.lib.api.ApiClient;
import com.battlehack_venice.lib.api.ApiResponseListParser;
import com.battlehack_venice.lib.model.Monument;
import com.battlehack_venice.mecenate.Application;
import com.battlehack_venice.mecenate.BaseActivity;
import com.battlehack_venice.mecenate.R;
import com.battlehack_venice.mecenate.monument.MonumentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends BaseActivity implements GoogleMap.OnMarkerClickListener
{
    private static final int COLUMNS = 2;

    @InjectView(R.id.list)
    RecyclerView _list;
    GoogleMap _map; // Might be null if Google Play services APK is not available.

    @Inject
    ApiClient _apiClient;
    private MonumentsAdapter _adapter;
    private Subscription _subscription;
    private Map<Marker, Monument> allMarkersMap = new HashMap<Marker, Monument>();

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
        }

        setUpMapIfNeeded();
        this._list.setAdapter(this._adapter);
        this._list.setLayoutManager(new GridLayoutManager(this, COLUMNS));

        this._load();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        setUpMapIfNeeded();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        menu.findItem(R.id.action_map).setVisible(!this._mapModeEnabled());
        menu.findItem(R.id.action_list).setVisible(this._mapModeEnabled());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.action_map:
                this._list.setVisibility(View.GONE);
                invalidateOptionsMenu();
                return true;

            case R.id.action_list:
                this._list.setVisibility(View.VISIBLE);
                invalidateOptionsMenu();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void _load()
    {
        this._subscription = this._apiClient.get("/pois", null, new ApiResponseListParser<>(Monument.PARSER, "pois"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomSubscriber(this._adapter, this._map));
    }

    private class CustomSubscriber extends Subscriber<List<Monument>>
    {
        private final MonumentsAdapter _adapter;
        private final GoogleMap _map;

        public CustomSubscriber(MonumentsAdapter adapter, GoogleMap map)
        {
            this._adapter = adapter;
            this._map = map;
        }

        @Override
        public void onCompleted()
        {
            this._adapter.onCompleted();
        }

        @Override
        public void onError(Throwable e)
        {
            this._adapter.onError(e);
        }

        @Override
        public void onNext(List<Monument> monuments)
        {
            for (Monument m : monuments) {
                this._adapter.onNext(m);

                Marker marker = this._map.addMarker(new MarkerOptions().position(new LatLng(m.getLat(), m.getLon())).title(m.getName()));
                allMarkersMap.put(marker, m);
            }
        }
    }

    private void setUpMapIfNeeded()
    {
        // Do a null check to confirm that we have not already instantiated the map.
        if (_map == null) {
            // Try to obtain the map from the SupportMapFragment.
            _map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

            if (_map != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap()
    {
        _map.setOnMarkerClickListener(this);

        // Enable MyLocation Layer of Google Map
        _map.setMyLocationEnabled(true);

        // Get LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Get Current Location
        //Criteria criteria = new Criteria();
        //String provider = locationManager.getBestProvider(criteria, true);
        //Location myLocation = locationManager.getLastKnownLocation(provider);
        // DEMO:
        Location myLocation = null;

        double latitude = (myLocation != null) ? myLocation.getLatitude() : 45.433741;
        double longitude = (myLocation != null) ? myLocation.getLongitude() : 12.337763;

        // Show the current location in Google Map
        LatLng latLng = new LatLng(latitude, longitude);
        _map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        _map.animateCamera(CameraUpdateFactory.zoomTo(16));
        _map.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title("You are here!")
                .snippet("Consider yourself located")
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }

    private boolean _mapModeEnabled()
    {
        return this._list.getVisibility() == View.GONE;
    }

    @Override
    public boolean onMarkerClick(final Marker marker)
    {

        Monument monument = allMarkersMap.get(marker);
        if (monument == null) {
            return false;
        }

        Intent intent = new Intent(this, MonumentActivity.class);
        intent.putExtra(MonumentActivity.EXTRA_MONUMENT, monument);

        this.startActivity(intent);
        return true;
    }

}
