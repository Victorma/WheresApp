package tk.wheresoft.wheresapp.activity;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import tk.wheresoft.wheresapp.service.PositionCommunicationService;

import tk.wheresoft.wheresapp.R;

import tk.wheresoft.wheresapp.bussiness.routes.ASRoutes;
import tk.wheresoft.wheresapp.bussiness.routes.factory.ASRoutesFactory;
import tk.wheresoft.wheresapp.model.Contact;
import tk.wheresoft.wheresapp.model.Ruta;

public class MapActivity extends FragmentActivity implements
        OnMarkerClickListener, OnMarkerDragListener {

    private LatLng fromPosition = null;
    private LatLng toPosition = null;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Double latitude = intent.getDoubleExtra("latitude", 0);
            Double longitude = intent.getDoubleExtra("longitude", 0);
            toPosition = new LatLng(latitude, longitude);
            if (map.getMyLocation() != null)
                fromPosition = new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude());
            Log.d("MapActivity", "onReceive: LATITUDE =" + latitude.toString() + ", LONGITUDE = " + longitude.toString());
            new AddRoute().execute();
        }
    };
    private ImageButton btDisconnect;
    private Contact toContact;
    private NotificationManager mNotificationManager;
    private GoogleMap map;
    private Ruta ruta;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
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
                Intent service = new Intent(getApplicationContext(), PositionCommunicationService.class);
                stopService(service);
                mNotificationManager.cancelAll();
                finish();
            }
        });
        if (map == null) {
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        }
        map.setMyLocationEnabled(true);
        Intent i = getIntent();
        if (i.hasExtra("TESTCALL")) {
            //Iniciamos el servicio que inicia y mantiene la llamada
            Intent service = new Intent(getApplicationContext(), PositionCommunicationService.class);
            service.putExtra("TESTCALL", "TESTCALL");
            this.setTitle("Llamada de prueba");
            startService(service);
        } else if (i.hasExtra("TOUSER")) {
            if (toContact == null) {
                toContact = (Contact) i.getSerializableExtra("TOUSER");
                this.setTitle("Llamando a " + toContact.getName());
            }
            //Iniciamos el servicio que inicia y mantiene la llamada
            Intent service = new Intent(getApplicationContext(), PositionCommunicationService.class);
            service.putExtra("CONTACT", toContact);
            if (i.hasExtra("INCOMING")) {
                service.putExtra("INCOMING", true);
                this.setTitle("Llamada de " + toContact.getName());
            }
            startService(service);
        } else {
            Intent main = new Intent(this, MainActivity.class);
            startActivity(main);
            finish();
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Llamada activa")
                        .setContentText(this.getTitle());
        Intent stopIntent = new Intent(this, MapActivity.class);
        stopIntent.putExtra("KILL", "KILL");
        PendingIntent stopPendingIntent = PendingIntent.getActivity(this, 0,
                stopIntent, PendingIntent.FLAG_CANCEL_CURRENT, null);
        mBuilder.addAction(R.drawable.ic_action_call, "Colgar", stopPendingIntent);

        Intent resultIntent = new Intent(this, MapActivity.class);
        resultIntent.putExtra("OPEN", "OPEN");

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        0,
                        null
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setOngoing(true);
        mNotificationManager.notify(1, mBuilder.build());


        addGoogleMap();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().hasExtra("KILL") && getIntent().getFlags() == PendingIntent.FLAG_CANCEL_CURRENT) {
            Intent service = new Intent(getApplicationContext(), PositionCommunicationService.class);
            stopService(service);
            mNotificationManager.cancelAll();
            finish();
        }
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
        Intent service = new Intent(getApplicationContext(), PositionCommunicationService.class);
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
        if (intent.hasExtra("KILL") && intent.getFlags() != 0 && intent.getFlags() != 4194304) {
            finish();
        }
    }

    private class AddRoute extends AsyncTask<Void, Void, Ruta> {

        @Override
        protected Ruta doInBackground(Void... params) {
            if (map != null && (fromPosition != null && toPosition != null)) {
                ASRoutes rutas = ASRoutesFactory.getInstance().getInstanceASRoutes(MapActivity.this);
                if (ruta != null)
                    ruta = rutas.updateDestinoRuta(ruta, fromPosition, toPosition);
                else
                    ruta = rutas.getRuta(fromPosition, toPosition);
                return ruta;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Ruta ruta) {
            super.onPostExecute(ruta);
            if (ruta != null) {
                map.clear();
                if (toPosition != null) {
                    map.clear();
                    //fromPosition = new LatLng(map.getMyLocation().getLatitude(),map.getMyLocation().getLongitude());
                    map.addMarker(new MarkerOptions().position(toPosition).title(getString(R.string.goal))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.flag))
                            .snippet("Best Time: 6 Secs").draggable(true));
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.waiting_location), Toast.LENGTH_LONG).show();
                }
                LatLngBounds.Builder bc = new LatLngBounds.Builder();
                for (LatLng item : ruta.getPuntos()) {
                    bc.include(item);
                }
                PolylineOptions lineas = new PolylineOptions();
                lineas.addAll(ruta.getPuntos());
                lineas.width(8);
                lineas.color(Color.BLUE);
                //Este es el mapa sobre el que tienes que añadir las lineas
                map.addPolyline(lineas);
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 50));
            }
        }
    }

}
