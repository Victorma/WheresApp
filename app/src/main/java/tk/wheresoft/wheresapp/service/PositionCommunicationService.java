package tk.wheresoft.wheresapp.service;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import tk.wheresoft.wheresapp.activity.MapActivity;
import tk.wheresoft.wheresapp.bussiness.calls.ASCalls;
import tk.wheresoft.wheresapp.bussiness.calls.factory.ASCallsFactory;
import tk.wheresoft.wheresapp.integration.calls.factory.DAOCallsFactory;
import tk.wheresoft.wheresapp.model.Call;
import tk.wheresoft.wheresapp.model.CallState;
import tk.wheresoft.wheresapp.model.Contact;
import tk.wheresoft.wheresapp.model.Message;
import tk.wheresoft.wheresapp.serverapi.ServerAPI;

import java.io.IOException;

public class PositionCommunicationService extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String BROADCAST_ACTION = "tk.wheresoft.wheresapp.LOCATION_UPDATE";
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 30;
    //Status of GPS
    public static final int OUT_OF_SERVICE = 0;
    public static final int TEMPORARILY_UNAVAILABLE = 1;
    public static final int AVAILABLE = 2;
    private static final String TAG = PositionCommunicationService.class.getSimpleName();
    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 20;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    private Gson gson = new Gson();
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Call call;
    private Contact contact;
    private ASCalls asCalls;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Location service created…");
        asCalls = ASCallsFactory.getInstance().getInstanceASCalls(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        IntentFilter filter = new IntentFilter("com.google.android.c2dm.intent.RECEIVE");
        registerReceiver(updateCallReceiver, filter);
    }    private BroadcastReceiver updateCallReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras().containsKey("update")) {
                Log.d(TAG, "updateCallReceiver");
                Bundle extras = intent.getExtras();
                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(PositionCommunicationService.this);
                if (extras != null && !extras.isEmpty()) {  // has effect of unparcelling Bundle
                    if (extras.containsKey("type")) {
                        String type = extras.getString("type");
                        switch (type) {
                            case "call": {
                                String message = extras.getString("message");
                                call = asCalls.getActiveCall();
                                Call callReceive = gson.fromJson(message, Call.class);
                                if (call.getServerId().equals(callReceive.getServerId())) {
                                    if (callReceive.getState().equals(CallState.END) && (call.getState().equals(CallState.ACCEPT) || call.getState().equals(CallState.WAIT))) {
                                        Log.d(TAG, "Message receiver: " + callReceive.toString());
                                        Log.d(TAG, "Active Call: " + call.toString());
                                        call.setUpdate(callReceive.getUpdate());
                                        call.setEnd(callReceive.getEnd());
                                        call.setState(callReceive.getState());
                                        DAOCallsFactory.getInstance().getInstanceDAOCalls(PositionCommunicationService.this).update(call);
                                        Toast.makeText(PositionCommunicationService.this, "Se ha finalizado la llamada", Toast.LENGTH_LONG).show();
                                        Log.d(TAG, "Location service destroyed…");
                                        unregisterReceiver(updateCallReceiver);
                                        clearLocationData();
                                        killActivity();
                                    } else if (callReceive.getState().equals(CallState.ACCEPT) && call.getState().equals(CallState.WAIT)) {
                                        mGoogleApiClient.connect();
                                        call.setState(CallState.ACCEPT);
                                        call.setUpdate(callReceive.getUpdate());
                                        DAOCallsFactory.getInstance().getInstanceDAOCalls(PositionCommunicationService.this).update(call);
                                        Toast.makeText(PositionCommunicationService.this, "Se ha aceptado la llamada", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }
                    }
                    setResultCode(Activity.RESULT_OK);
                }
            }
        }
    };

    private void crearLlamada() {
        new AsyncTask<Void, Void, Bundle>() {
            @Override
            protected Bundle doInBackground(Void... params) {
                Bundle bundle = new Bundle();
                try {
                    call = asCalls.getActiveCall();
                    if (call == null) {
                        Log.d(TAG, "Create call");
                        if (asCalls.call(contact)) {
                            Log.d(TAG, "Get active call");
                            call = asCalls.getActiveCall();
                            Log.d(TAG, "Active call " + call.toString());
                        } else {
                            bundle.putBoolean("ERROR", true);
                        }
                    }
                } catch (IOException e) {
                    bundle.putBoolean("ERROR", true);
                }
                return bundle;
            }

            @Override
            protected void onPostExecute(Bundle bundle) {
                if (bundle.containsKey("ERROR")) {
                    Toast.makeText(PositionCommunicationService.this, "Ha ocurrido un error", Toast.LENGTH_LONG).show();
                    killActivity();
                } else if (call.getState().equals(CallState.WAIT)) {
                    Toast.makeText(PositionCommunicationService.this, "Esperando respuesta", Toast.LENGTH_LONG).show();
                } else {
                    mGoogleApiClient.connect();
                }
            }

        }.execute(null, null, null);
    }

    private void crearLlamadaPrueba() {
        new AsyncTask<Void, Void, Bundle>() {
            @Override
            protected Bundle doInBackground(Void... params) {
                Bundle bundle = new Bundle();
                try {
                    call = asCalls.getActiveCall();
                    if (call == null) {
                        Log.d(TAG, "Create call");
                        if (asCalls.testCall()) {
                            Log.d(TAG, "Get active call");
                            call = asCalls.getActiveCall();
                            Log.d(TAG, "Active call " + call.toString());
                        } else {
                            bundle.putBoolean("ERROR", true);
                        }
                    }
                } catch (IOException e) {
                    bundle.putBoolean("ERROR", true);
                }
                return bundle;
            }

            @Override
            protected void onPostExecute(Bundle bundle) {
                if (bundle.containsKey("ERROR")) {
                    Toast.makeText(PositionCommunicationService.this, "Ha ocurrido un error", Toast.LENGTH_LONG).show();
                    killActivity();
                } else if (call.getState().equals(CallState.WAIT)) {
                    Toast.makeText(PositionCommunicationService.this, "Esperando respuesta", Toast.LENGTH_LONG).show();
                } else {
                    mGoogleApiClient.connect();
                }
            }

        }.execute(null, null, null);
    }

    private void actualizarPosicion(Location position) {
        if (position != null) {
            Log.d(TAG, "onLocationChanged");
            Log.d(TAG, "MY NEW LOCATION: " + position.getLatitude() + " , " + position.getLongitude());

            Double lat = position.getLatitude();
            Double lon = position.getLongitude();
            String positionString = lat.toString() + "," + lon.toString();
            new AsyncTask<String, Void, Message>() {
                @Override
                protected Message doInBackground(String... params) {
                    String position = params[0];
                    Message message = null;
                    try {
                        message = ServerAPI.getInstance(PositionCommunicationService.this).enviarPosicion(call.getServerId(), position);
                    } catch (IOException e) {
                        return null;
                    }
                    return message;
                }

                @Override
                protected void onPostExecute(Message message) {
                    if (message != null) {
                        Log.d(TAG, "LOCATION RECEIVE: " + message.getMessage());
                        // Since location information updated, broadcast it
                        Intent broadcast = new Intent();

                        // Set action so other parts of application can distinguish and use this information if needed
                        broadcast.setAction(BROADCAST_ACTION);
                        if (!message.getMessage().equals("WAIT")) {
                            broadcast.putExtra("latitude", Double.parseDouble(message.getMessage().split(",")[0]));
                            broadcast.putExtra("longitude", Double.parseDouble(message.getMessage().split(",")[1]));

                            LocalBroadcastManager.getInstance(PositionCommunicationService.this).sendBroadcast(broadcast);
                        }
                    }
                }
            }.execute(positionString, null, null);
        }
    }

    private void killActivity() {
        Intent killMapActivity = new Intent(PositionCommunicationService.this, MapActivity.class);
        killMapActivity.putExtra("KILL", 1);
        killMapActivity.addFlags(PendingIntent.FLAG_CANCEL_CURRENT);
        getApplication().startActivity(killMapActivity);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Unregister location listeners
    private void clearLocationData() {
        mGoogleApiClient.disconnect();
    }

    // When service destroyed we need to unbind location listeners
    @Override
    public void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(updateCallReceiver);
        Log.d(TAG, "Location service destroyed…");
        terminarLlamada();
        try {
            unregisterReceiver(updateCallReceiver);
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Error al detener el broadcast");
        }
        clearLocationData();

    }

    private void terminarLlamada() {
        new AsyncTask<Void, Void, Bundle>() {
            @Override
            protected Bundle doInBackground(Void... params) {
                Bundle bundle = new Bundle();
                try {
                    asCalls.end(call);
                    bundle.putString("INFO", "Llamada terminada");
                    return bundle;
                } catch (IOException e) {
                    bundle.putBoolean("ERROR", true);
                    return bundle;
                }
            }

            @Override
            protected void onPostExecute(Bundle bundle) {
                if (bundle.containsKey("ERROR")) {
                    Toast.makeText(PositionCommunicationService.this, "Ha ocurrido un error", Toast.LENGTH_LONG).show();
                } else if (bundle.containsKey("INFO")) {
                    Toast.makeText(PositionCommunicationService.this, "Llamada terminada", Toast.LENGTH_LONG).show();
                }
            }
        }.execute(null, null, null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Calling command…");
        if (intent != null) {
            if (intent.hasExtra("TESTCALL")) {
                crearLlamadaPrueba();
            } else {
                if (contact == null && startId == 1) {
                    contact = (Contact) intent.getSerializableExtra("CONTACT");
                    crearLlamada();
                } else {
                    killActivity();
                }
            }
        } else {
            killActivity();
        }
        return START_STICKY;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Location Callback. onConnected");

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL); // Update location every second
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        actualizarPosicion(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));
    }

    @Override
    public void onConnectionSuspended(int i) {
        stopSelf();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Location Callback. onConnectionFailed");
        stopSelf();
    }

    @Override
    public void onLocationChanged(Location location) {
        actualizarPosicion(location);
    }



}
