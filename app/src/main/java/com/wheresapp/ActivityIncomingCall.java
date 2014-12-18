package com.wheresapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.wheresapp.bussiness.calls.ASCalls;
import com.wheresapp.bussiness.calls.factory.ASCallsFactory;
import com.wheresapp.modelTEMP.Call;
import com.wheresapp.modelTEMP.Contact;


public class ActivityIncomingCall extends ActionBarActivity {

    public static final String KEY_CONTACT = "CONTACT";
    private Button btAccept, btDeny;
    private Contact contact;
    private Call call;
    private ASCalls asCalls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        asCalls = ASCallsFactory.getInstance().getInstanceASCalls(this);
        if (savedInstanceState.containsKey(KEY_CONTACT)) {
            contact = (Contact) savedInstanceState.getSerializable(KEY_CONTACT);
        }
        call = asCalls.getActiveCall();
        if (call == null ) {
            finish();
        }
        btAccept = (Button) findViewById(R.id.buttonAccept);
        btAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Mirar en asincrono
                btAccept.setEnabled(false);
                btDeny.setEnabled(false);
                asCalls.accept(call);
            }
        });
        btDeny = (Button) findViewById(R.id.buttonDeny);
        btDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Mirar en asincrono
                btAccept.setEnabled(false);
                btDeny.setEnabled(false);
                asCalls.reject(call);
            }
        });
        setContentView(R.layout.activity_incoming_call);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_incoming_call, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
