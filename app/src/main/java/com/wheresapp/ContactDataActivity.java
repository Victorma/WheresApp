package com.wheresapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.wheresapp.modelTEMP.Contact;


public class ContactDataActivity extends Activity {

    boolean favourite = false;
    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_data);

        Intent i = getIntent();
        Bundle bundle = i.getBundleExtra("USER");
        contact = (Contact) bundle.getSerializable("USER");
        this.favourite = contact.getFavourite();
        if (this.favourite) {
            MenuItem item = (MenuItem) findViewById(R.id.action_important);
            item.setIcon(R.drawable.ic_action_important);
        }
        this.setTitle(contact.getName());
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
                intent.putExtra("TOUSER", contact.getServerid());
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
