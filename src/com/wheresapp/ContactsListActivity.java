package com.wheresapp;

import android.app.ActionBar.LayoutParams;
import android.app.ListActivity;
//import android.app.ListFragment; //android.support.v4.app.ListFragment;
import android.app.LoaderManager; //android.support.v4.app.LoaderManager;
import android.content.CursorLoader; //android.support.v4.content.CursorLoader;
import android.content.Intent;
import android.content.Loader; //android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts; //
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView; //android.support.v7.widget.SearchView
import android.widget.SearchView.OnQueryTextListener; //android.support.v7.widget.SearchView.OnQueryTextListener
import android.widget.SimpleCursorAdapter; //android.support.v4.widget.SimpleCursorAdapter;

public class ContactsListActivity extends ListActivity
implements OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {

    // This is the Adapter being used to display the list's data.
    SimpleCursorAdapter mAdapter;

    // If non-null, this is the current filter the user has provided.
    String mCurFilter;

    /*private static final String TAG = "UserListActivity";
    private Intent intent;
    MessageSender messageSender;
    GoogleCloudMessaging gcm;*/
    String userNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        userNumber = i.getStringExtra("NUMBER");
        
        // Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) this.findViewById(android.R.id.content);
        root.addView(progressBar);

        /*intent = new Intent(this, GCMNotificationIntentService.class);
        registerReceiver(broadcastReceiver, new IntentFilter("com.wheresapp.contactslist"));
        messageSender = new MessageSender();
        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());*/

        // Create an empty adapter we will use to display the loaded data.
        mAdapter = new SimpleCursorAdapter(this, R.layout.contacts_list_item, null,
                new String[] {Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                        Contacts.DISPLAY_NAME_PRIMARY : Contacts.DISPLAY_NAME },
                        new int[] { android.R.id.text1 }, 0);
        setListAdapter(mAdapter);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }

    /*private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: " + intent.getStringExtra("USERLIST"));
            updateUI(intent.getStringExtra("USERLIST"));
        }
    };

    private void updateUI(String userList) {
        //get userlist from the intents and update the list
        String[] userListArr = userList.split(":");
        Log.d(TAG,"userListArr: "+userListArr.length+" tostr "+userListArr.toString());

        //remove empty strings :-)
        List<String> list = new ArrayList<String>();
        for(String s : userListArr) {
            if(s != null && s.length() > 0) {
                list.add(s);
            }
        }
        userListArr = list.toArray(new String[list.size()]);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, userListArr);
        setListAdapter(adapter);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.contacts_list, menu);
        // Place an action bar item for searching.
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView sv = new SearchView(this);
        sv.setOnQueryTextListener(this);
        item.setActionView(sv);
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
        case R.id.action_refresh:
            /*// get user list
            Bundle dataBundle = new Bundle();
            dataBundle.putString("ACTION", "USERLIST");
            messageSender.sendMessage(dataBundle, gcm);*/
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onQueryTextChange(String newText) {
        // Called when the action bar search text has changed.  Update
        // the search filter, and restart the loader to do a new query
        // with this filter.
        mCurFilter = !TextUtils.isEmpty(newText) ? newText : null;
        getLoaderManager().restartLoader(0, null, this);
        return true;
    }

    @Override public boolean onQueryTextSubmit(String query) {
        // Don't care about this.
        return true;
    }

    @Override public void onListItemClick(ListView l, View v, int position, long id) {
        // Insert desired behavior here.
        Intent intent = new Intent(this, MapActivity.class);
        //intent.putExtra("TOUSER", i);
        startActivity(intent);
        finish();
    }

    // These are the Contacts rows that we will retrieve.
    private static final String[] PROJECTION = new String[] {
        Contacts._ID, Contacts.LOOKUP_KEY, Build.VERSION.SDK_INT
        >= Build.VERSION_CODES.HONEYCOMB ? Contacts.DISPLAY_NAME_PRIMARY :
            Contacts.DISPLAY_NAME };

    private static String SELECTION = "((" +
            Contacts.HAS_PHONE_NUMBER + "=1) AND (" +
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                    Contacts.DISPLAY_NAME_PRIMARY + " NOTNULL) AND (" +
                    Contacts.DISPLAY_NAME_PRIMARY + " != '' ))" :
                        Contacts.DISPLAY_NAME + " NOTNULL) AND (" +
                        Contacts.DISPLAY_NAME + " != '' ))");

    // Defines a string that specifies a sort order
    private static final String SORT_ORDER =
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                    Contacts.DISPLAY_NAME_PRIMARY : Contacts.DISPLAY_NAME)
                    + " COLLATE LOCALIZED ASC";

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // sample only has one Loader, so we don't care about the ID.
        // First, pick the base URI to use depending on whether we are
        // currently filtering.
        Uri baseUri;
        if (mCurFilter != null) {
            baseUri = Uri.withAppendedPath(Contacts.CONTENT_FILTER_URI,
                    Uri.encode(mCurFilter));
        } else {
            baseUri = Contacts.CONTENT_URI;
        }

        return new CursorLoader(this, baseUri,
                PROJECTION, SELECTION, null, SORT_ORDER);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mAdapter.swapCursor(data);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }
}