package com.wheresapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class ContactMap extends FragmentActivity implements
OnMarkerClickListener, OnMarkerDragListener {
	
	private static final LatLng GRAN_VIA = new LatLng(40.420276, -3.705709);
	private static LatLng fromPosition = new LatLng(40.440260, -3.716165);
	private static LatLng toPosition = null;
	private GoogleMap map;
	FusedLocationService fusedLocationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_map);
        if (map == null) {
        	map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        }
        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        fusedLocationService = new FusedLocationService(this);

        //findMe();
		addGoogleMap();
		addLines();
		addMarkers();
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.contacto_map, menu);
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
    
    /*private void findMe(){
    	Location location = fusedLocationService.getLocation();
    	if (null != location) {
    		double latitude = location.getLatitude();
    		double longitude = location.getLongitude();
    		fromPosition = new LatLng(latitude, longitude);
       } else {
    	   Toast.makeText(this, "Location Not Available!", Toast.LENGTH_LONG).show();
           finish();
       }
    }*/
    
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
			// a draggable marker with custom color and opacity
			map.addMarker(new MarkerOptions().position(fromPosition).title("Race Start")
					.icon(BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).alpha(0.4f).draggable(true));

			// a draggable marker with title and snippet
			// marker using custom image
			map.addMarker(new MarkerOptions().position(GRAN_VIA).title("First Pit Stop")
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.flag))
					.snippet("Best Time: 6 Secs").draggable(true));

			map.moveCamera(CameraUpdateFactory.newLatLngZoom(GRAN_VIA, 13));

		}
	}

	private void addLines() {
		if (map != null) {
			map.addPolyline((new PolylineOptions()).add(GRAN_VIA, fromPosition)
					.width(5).color(Color.BLUE).geodesic(true));
			// move camera to zoom on map
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(GRAN_VIA, 13));
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		Log.i("GoogleMapActivity", "onMarkerClick");
		Toast.makeText(getApplicationContext(),
				"Marker Clicked: " + marker.getTitle(), Toast.LENGTH_LONG)
				.show();
		return false;
	}

	@Override
	public void onMarkerDrag(Marker marker) {
		// do nothing during drag
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		toPosition = marker.getPosition();
		Toast.makeText(
				getApplicationContext(),
				"Marker " + marker.getTitle() + " dragged from " + fromPosition
						+ " to " + toPosition, Toast.LENGTH_LONG).show();

	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		fromPosition = marker.getPosition();
		Log.d(getClass().getSimpleName(), "Drag start at: " + fromPosition);
	}
}



