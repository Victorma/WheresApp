package com.wheresapp;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class SendPositionActivity extends Activity {

    LocationService locationService;
    GoogleCloudMessaging gcm;
    Intent intent;
    private String toUserName;
    MessageSender messageSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toUserName = getIntent().getStringExtra("TOUSER");
        setContentView(R.layout.activity_send_position);
        
        intent = new Intent(this, GcmIntentService.class);
        registerReceiver(broadcastReceiver, new IntentFilter("com.wheresapp.request"));

        messageSender = new MessageSender();
        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setIcon(R.drawable.ic_launcher);
        b.setMessage(getString(R.string.request) + toUserName);
        b.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Llamar a clase/servicio localizacion
                //Enviar mensaje localizacion "RESPONSE" con datos
                sendResponse();

                Toast.makeText(SendPositionActivity.this.getApplicationContext(),
                        getString(R.string.granted) + toUserName, Toast.LENGTH_LONG).show();
                SendPositionActivity.this.finish();
            }
        });
        b.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(SendPositionActivity.this.getApplicationContext(),
                        getString(R.string.denied) + toUserName, Toast.LENGTH_LONG).show();
                SendPositionActivity.this.finish();}
        });
        b.show();
    }
    
    private void sendResponse(){
        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        locationService = new LocationService(this);

        Location location = locationService.getLocation();
        String loc = "";
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            loc = latitude + "," + longitude;
        }
        // Emitir cada 20 segundos
      //sending gcm message to the paired device
        Bundle dataBundle = new Bundle();
        dataBundle.putString("ACTION", "RESPONSE");
        dataBundle.putString("TOUSER", toUserName);
        dataBundle.putString("LOCATION", loc);
        messageSender.sendMessage(dataBundle,gcm);

        
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.send_position, menu);
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
}
