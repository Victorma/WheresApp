package com.wheresapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements
OnMarkerClickListener, OnMarkerDragListener {

    private Location location = null;
    private static final LatLng GRAN_VIA = new LatLng(40.420276, -3.705709);
    private static LatLng fromPosition = null;
    private static LatLng toPosition = GRAN_VIA;
    private GoogleMap map;
    LocationService locationService;
    GoogleCloudMessaging gcm;
    Intent intent;
    private String toUserName;
    MessageSender messageSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        toUserName = i.getStringExtra("TOUSER");
        setContentView(R.layout.activity_map);

        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            Toast.makeText(getApplicationContext(), getString(R.string.no_gp_services), Toast.LENGTH_LONG).show();
            finish();
        }
        locationService = new LocationService(this);

        intent = new Intent(this, GcmIntentService.class);
        registerReceiver(broadcastReceiver, new IntentFilter("com.wheresapp.request"));

        messageSender = new MessageSender();
        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());

        sendRequest();

        if (map == null) {
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        }

        addGoogleMap();
        addMarkers();
        //addLines();
    }

    private boolean sendRequest(){
        //sending gcm message to the paired device
        Bundle dataBundle = new Bundle();
        dataBundle.putString("ACTION", "REQUEST");
        dataBundle.putString("TOUSER", toUserName);
        messageSender.sendMessage(dataBundle,gcm);

        return true;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            /*Log.d(TAG, "onReceive: " + intent.getStringExtra("CHATMESSAGE"));
            chatArrayAdapter.add(new ChatMessage(true, intent.getStringExtra("CHATMESSAGE")));*/
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    private void addGoogleMap() {
        // check if we have got the googleMap already
        if (map == null) {
            map = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            map.setOnMarkerClickListener(this);
            map.setOnMarkerDragListener(this);
        }

    }

    private void addMarkers() {
        if (map != null) {
            if (locationService.getLocation() != null) {
                fromPosition = new LatLng(location.getLatitude(), location.getLongitude());
                map.addMarker(new MarkerOptions().position(fromPosition).title(getString(R.string.my_location))
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).alpha(0.4f).draggable(true));
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.waiting_location), Toast.LENGTH_LONG).show();
            }

            // a draggable marker with title and snippet
            // marker using custom image
            map.addMarker(new MarkerOptions().position(toPosition).title(getString(R.string.goal))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.flag))
                    .snippet("Best Time: 6 Secs").draggable(true));

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(toPosition, 13));
        }
    }

    /*private void addLines() {
        if (map != null) {
            map.addPolyline((new PolylineOptions()).add(GRAN_VIA, fromPosition)
                    .width(5).color(Color.BLUE).geodesic(true));
            // move camera to zoom on map
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(GRAN_VIA, 13));
        }
    }
     */

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.i("GoogleMapActivity", "onMarkerClick");
        Toast.makeText(getApplicationContext(),
                "Marker Clicked: " + marker.getTitle(), Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        // do nothing during drag
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        toPosition = marker.getPosition();
        Toast.makeText(getApplicationContext(),
                "Marker " + marker.getTitle() + " dragged from " + fromPosition
                + " to " + toPosition, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        fromPosition = marker.getPosition();
        Log.d(getClass().getSimpleName(), "Drag start at: " + fromPosition);
    }
}