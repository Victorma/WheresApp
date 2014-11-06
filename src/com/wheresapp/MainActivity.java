package com.wheresapp;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //FavoritesFragment favorites = new FavoritesFragment();
        //getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, favorites).commit();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
    	switch (item.getItemId()) {
		case R.id.action_settings:
            return true;
		case R.id.action_search:
			searchChat();
			return true;
		case R.id.action_new_chat:
			seeContacts();
			return true;
		}
    	return super.onOptionsItemSelected(item);
    }
    
    private void searchChat() {
    	
    }
    
    private void seeContacts() {
    	ContactsListFragment newFragment = new ContactsListFragment();
    	FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

    	// Replace whatever is in the fragment_container view with this fragment,
    	// and add the transaction to the back stack so the user can navigate back
    	transaction.replace(R.id.fragment_container, newFragment);
    	transaction.addToBackStack(null);

    	// Commit the transaction
    	transaction.commit();
    }
}
