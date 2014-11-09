package com.wheresapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class ContactsListFragment extends Fragment implements
    LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    // Defines an array that contains column names to move from
    // the Cursor to the ListView.    
    @SuppressLint("InlinedApi")
    private final static String[] FROM_COLUMNS = {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
        Contacts.DISPLAY_NAME_PRIMARY : Contacts.DISPLAY_NAME };
    
    // Defines an array that contains resource ids for the layout views
    // that get the Cursor column contents. The id is pre-defined in
    // the Android framework, so it is prefaced with "android.R.id"
    private final static int[] TO_IDS = {android.R.id.text1};
    
    // Define global mutable variables
    // Define a ListView object
    ListView mContactsList;
    // Define variables for the contact the user selects
    // The contact's _ID value
    long mContactId;
    // The contact's LOOKUP_KEY
    String mContactKey;
    // A content URI for the selected contact
    Uri mContactUri;
    // An adapter that binds the result Cursor to the ListView
    private SimpleCursorAdapter mCursorAdapter;

    public ContactsListFragment() {}
    
    // A UI Fragment must inflate its View
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the fragment layout
        return inflater.inflate(R.layout.contacts_list_view,
            container, false);
    }
    
    public void onActivityCreated(Bundle savedInstanceState) {
        // Always call the super method first
        super.onActivityCreated(savedInstanceState);        
        setHasOptionsMenu(true);
        
        // Gets the ListView from the View list of the parent activity
        mContactsList =
            (ListView) getActivity().findViewById(R.id.contacts_list);
        // Gets a CursorAdapter
        mCursorAdapter = new SimpleCursorAdapter(getActivity(),
            R.layout.contacts_list_item, null, FROM_COLUMNS, TO_IDS, 0);
        // Sets the adapter for the ListView
        mContactsList.setAdapter(mCursorAdapter);
        // Set the item click listener to be the current fragment.
        mContactsList.setOnItemClickListener(this);
        // Initializes the loader
        getLoaderManager().initLoader(0, null, this);
    }
    
    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION = {
        Contacts._ID, Contacts.LOOKUP_KEY, Build.VERSION.SDK_INT
        >= Build.VERSION_CODES.HONEYCOMB ? Contacts.DISPLAY_NAME_PRIMARY :
            Contacts.DISPLAY_NAME };

 // Defines the text expression
    @SuppressLint("InlinedApi")
    private static String SELECTION =
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
            Contacts.DISPLAY_NAME_PRIMARY + " IS NOT NULL" :
            Contacts.DISPLAY_NAME + " IS NOT NULL") + " AND " + 
            Contacts.HAS_PHONE_NUMBER + " LIKE 1";
    // Defines a variable for the search string
    //private String mSearchString;
    // Defines the array to hold values that replace the ?
    //private String[] mSelectionArgs = { mSearchString };
    // Defines a string that specifies a sort order
    private static final String SORT_ORDER =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
            Contacts.DISPLAY_NAME_PRIMARY : Contacts.DISPLAY_NAME;
    
    @Override
    public void onItemClick(AdapterView<?> parent, View item, int position, long rowID) {
        // Get the Cursor
        Intent intent = new Intent(getActivity(), ContactMap.class);
        startActivity(intent);
    }
    
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        
        // Makes search string into pattern and
        // stores it in the selection array
        //mSelectionArgs[0] = "%" + mSearchString + "%";
        // Starts the query
        return new CursorLoader(getActivity(), Contacts.CONTENT_URI,
                PROJECTION, SELECTION, null, SORT_ORDER);
    }
    
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Put the result Cursor in the adapter for the ListView
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Delete the reference to the existing Cursor
        mCursorAdapter.swapCursor(null);
        
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.removeItem(R.id.action_new_chat);
        super.onCreateOptionsMenu(menu, inflater);
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
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void searchChat() { }
}