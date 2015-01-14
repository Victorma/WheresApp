package com.wheresapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.wheresapp.bussiness.routes.ASRoutes;
import com.wheresapp.bussiness.routes.factory.ASRoutesFactory;
import com.wheresapp.modelTEMP.Call;
import com.wheresapp.modelTEMP.Contact;
import com.wheresapp.modelTEMP.Message;
import com.wheresapp.modelTEMP.Ruta;
import com.wheresapp.server.ServerAPI;

import java.util.List;

public class MapActivity extends FragmentActivity implements
        OnMarkerClickListener, OnMarkerDragListener {

    private LatLng fromPosition = null;
    private LatLng toPosition = null;
    private ImageButton btDisconnect;
    private Contact toContact;
    private Call call;
    private NotificationManager mNotificationManager;
    private GoogleMap map;
    private Ruta ruta;
    GoogleCloudMessaging gcm;
    MessageSender messageSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (getIntent().hasExtra("KILL")) {
            finish();
        }
        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            Toast.makeText(getApplicationContext(), getString(R.string.no_gp_services), Toast.LENGTH_LONG).show();
            finish();
        }

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        btDisconnect = (ImageButton) findViewById(R.id.btDisconnect);
        btDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent service = new Intent(getApplicationContext(),PositionCommunicationService.class);
                stopService(service);
                mNotificationManager.cancelAll();
                finish();
            }
        });
        Intent i = getIntent();
        if (toContact==null) {
            toContact = (Contact) i.getSerializableExtra("TOUSER");
            this.setTitle("Llamando a " + toContact.getName());
        }


        if (map == null) {
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        }
        map.setMyLocationEnabled(true);

        //Iniciamos el servicio que inicia y mantiene la llamada
        Intent service = new Intent(getApplicationContext(),PositionCommunicationService.class);
        service.putExtra("CONTACT", toContact);
        if (i.hasExtra("INCOMING")) {
            service.putExtra("INCOMING", true);
        }
        startService(service);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Llamada activa")
                        .setContentText(this.getTitle());

        Intent resultIntent = new Intent(this, MapActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        0
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setOngoing(true);
        mNotificationManager.notify(1,mBuilder.build());


        addGoogleMap();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Double latitude = intent.getDoubleExtra("latitude",0);
            Double longitude = intent.getDoubleExtra("longitude",0);
            toPosition = new LatLng(latitude,longitude);
            fromPosition = new LatLng(map.getMyLocation().getLatitude(),map.getMyLocation().getLongitude());
            Log.d("MapActivity", "onReceive: LATITUDE =" + latitude.toString() + ", LONGITUDE = " + longitude.toString() );
            new AddRoute().execute();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(PositionCommunicationService.BROADCAST_ACTION));
    }
    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
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

    private class AddRoute extends AsyncTask<Void,Void,Ruta> {

        @Override
        protected Ruta doInBackground(Void... params) {
            if (map != null && (fromPosition!=null && toPosition!=null)) {
                ASRoutes rutas = ASRoutesFactory.getInstance().getInstanceASRoutes();
                if (ruta!=null)
                    ruta = rutas.updateDestinoRuta(ruta,toPosition);
                else
                    ruta = rutas.getRuta(fromPosition, toPosition);
                return ruta;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Ruta ruta) {
            super.onPostExecute(ruta);
            if (ruta!=null) {
                map.clear();
                if (toPosition != null) {
                    map.clear();
                    //fromPosition = new LatLng(map.getMyLocation().getLatitude(),map.getMyLocation().getLongitude());
                    map.addMarker(new MarkerOptions().position(toPosition).title(getString(R.string.goal))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.flag))
                            .snippet("Best Time: 6 Secs").draggable(true));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(toPosition, 13));
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.waiting_location), Toast.LENGTH_LONG).show();
                }
                PolylineOptions lineas = new PolylineOptions();
                lineas.addAll(ruta.getPuntos());
                lineas.width(8);
                lineas.color(Color.BLUE);
                //Este es el mapa sobre el que tienes que añadir las lineas
                map.addPolyline(lineas);
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
        //Log.d(getClass().getSimpleName(), "Drag start at: " + fromPosition);
    }

    @Override
    protected void onDestroy() {
        Intent service = new Intent(getApplicationContext(),PositionCommunicationService.class);
        stopService(service);
        mNotificationManager.cancelAll();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //No se puede volver hacia atras, para ello hay que cancelar la llamada
        Toast.makeText(this, "Tienes que colgar para volver atras", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_map);

        } else {
            setContentView(R.layout.activity_map);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra("KILL")) {
            finish();
        }
    }
}
