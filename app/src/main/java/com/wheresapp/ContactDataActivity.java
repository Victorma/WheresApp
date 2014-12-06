package com.wheresapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class ContactDataActivity extends Activity {

    private String toUserName;
    boolean favourite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_data);

        Intent i = getIntent();
        toUserName = i.getStringExtra("TOUSER");
        this.setTitle(toUserName);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // When the user clicks REFRESH
            case R.id.action_call:
                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtra("TOUSER", toUserName);
                startActivity(intent);
                finish();
                return true;
            // When the user clicks REFRESH
            case R.id.action_important:
                if (favourite){
                    item.setIcon(R.drawable.ic_action_not_important);
                    favourite = false;
                } else {
                    item.setIcon(R.drawable.ic_action_important);
                    favourite = true;
                }
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
