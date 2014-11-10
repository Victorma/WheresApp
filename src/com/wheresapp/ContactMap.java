package com.wheresapp;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ContactMap extends FragmentActivity implements LocationListener,
GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
OnMarkerClickListener, OnMarkerDragListener {

	private static final long INTERVAL = 1000 * 30;
	private static final long FASTEST_INTERVAL = 1000 * 5;
	private static final long ONE_MIN = 1000 * 60;
	private static final long REFRESH_TIME = ONE_MIN * 5;
	private static final float MINIMUM_ACCURACY = 50.0f;
	private LocationRequest locationRequest;
	private GoogleApiClient googleApiClient;
	private Location location = null;
	private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;
	private static final LatLng GRAN_VIA = new LatLng(40.420276, -3.705709);
	private static LatLng fromPosition = null;
	private static LatLng toPosition = GRAN_VIA;
	private GoogleMap map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_map);

		locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locationRequest.setInterval(INTERVAL);
		locationRequest.setFastestInterval(FASTEST_INTERVAL);

		googleApiClient = new GoogleApiClient.Builder(this)
		.addApi(LocationServices.API)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this)
		.build();
		if (googleApiClient != null) {
			googleApiClient.connect();
		}


		if (map == null) {
			map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		}

		//show error dialog if GoolglePlayServices not available
		if (!isGooglePlayServicesAvailable()) {
			finish();
		}

		addGoogleMap();
		addMarkers();
		//addLines();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contact_map, menu);
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

	public void showSettingsAlert(String provider) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(ContactMap.this);

		alertDialog.setTitle(provider + " SETTINGS");
		alertDialog.setMessage(provider + " is not enabled! Want to go to settings menu?");

		alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(
						Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				ContactMap.this.startActivity(intent);
			}
		});

		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		alertDialog.show();
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
			// a draggable marker with custom color and opacity
			if (location != null) {
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
	public void onConnectionFailed(ConnectionResult arg0) { }

	@Override
	public void onConnected(Bundle connectionH) {
		Location currentLocation = fusedLocationProviderApi.getLastLocation(googleApiClient);
		if (currentLocation != null && currentLocation.getTime() > REFRESH_TIME) {
			location = currentLocation;
			addGoogleMap();
			addMarkers();
			//addNavigation();
		} else {
			fusedLocationProviderApi.requestLocationUpdates(googleApiClient, locationRequest, this);
			// Schedule a Thread to unregister location listeners
			Executors.newScheduledThreadPool(1).schedule(new Runnable() {
				@Override
				public void run() {
					fusedLocationProviderApi.removeLocationUpdates(googleApiClient, ContactMap.this);
				}
			}, ONE_MIN, TimeUnit.MILLISECONDS);
		}    
	}

	@Override
	public void onConnectionSuspended(int arg0) { }

	@Override
	public void onLocationChanged(Location arg0) {
		//if the existing location is empty or
		//the current location accuracy is greater than existing accuracy
		//then store the current location
		if (location==null || arg0.getAccuracy() < location.getAccuracy()) {
			location = arg0;
			//if the accuracy is not better, remove all location updates for this listener
			if (location.getAccuracy() < MINIMUM_ACCURACY) {
				fusedLocationProviderApi.removeLocationUpdates(googleApiClient, this);
			} else {
				addGoogleMap();
				addMarkers();
				//addNavigation();
			}
		}
	}

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