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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class ContactsListFragment extends Fragment implements
LoaderManager.LoaderCallbacks<Cursor>,
AdapterView.OnItemClickListener {

	/*
     * Defines an array that contains column names to move from
     * the Cursor to the ListView.
     */
    @SuppressLint("InlinedApi")
    private final static String[] FROM_COLUMNS = {
            Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.HONEYCOMB ?
                    Contacts.DISPLAY_NAME_PRIMARY :
                    Contacts.DISPLAY_NAME
    };
    /*
     * Defines an array that contains resource ids for the layout views
     * that get the Cursor column contents. The id is pre-defined in
     * the Android framework, so it is prefaced with "android.R.id"
     */
    private final static int[] TO_IDS = {
           android.R.id.text1
    };
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
    
    
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
    	Uri contentUri;
    	/*
         * Appends the search string to the base URI. Always
         * encode search strings to ensure they're in proper
         * format.
         */
        if (mSearchString == null) {
            // Since there's no search string, use the content URI that searches the entire
            // Contacts table
            contentUri = Contacts.CONTENT_URI;
        } else {
            // Since there's a search string, use the special content Uri that searches the
            // Contacts table. The URI consists of a base Uri and the search string.
            contentUri =
                    Uri.withAppendedPath(Contacts.CONTENT_URI, Uri.encode(mSearchString));
        }
        // Starts the query
        return new CursorLoader(
                getActivity(),
                contentUri,
                PROJECTION,
                null,
                null,
                null
        );
    }
    
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
      
        // Gets the ListView from the View list of the parent activity
        mContactsList =
            (ListView) getActivity().findViewById(R.id.contacts_list);
        // Gets a CursorAdapter
        mCursorAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.contacts_list_item,
                null,
                FROM_COLUMNS, TO_IDS,
                0);
        
        
        
        // Sets the adapter for the ListView
        mContactsList.setAdapter(mCursorAdapter);
        mContactsList.setOnItemClickListener(this);
        getLoaderManager().initLoader(0, null, this);
    }
    
    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION =
            {
                Contacts._ID,
                Contacts.LOOKUP_KEY,
                Build.VERSION.SDK_INT
                        >= Build.VERSION_CODES.HONEYCOMB ?
                        Contacts.DISPLAY_NAME_PRIMARY :
                        Contacts.DISPLAY_NAME

            };
    
    
 // The column index for the _ID column
    private static final int CONTACT_ID_INDEX = 0;
    // The column index for the LOOKUP_KEY column
    private static final int LOOKUP_KEY_INDEX = 1;
    
    @Override
    public void onItemClick(
        AdapterView<?> parent, View item, int position, long rowID) {
        // Get the Cursor
    	   Intent intent = new Intent(getActivity(), ContactMap.class);
    	   startActivity(intent);
    	
    /*    Cursor cursor = ((CursorAdapter) parent.getAdapter()).getCursor();
        // Move to the selected contact
        cursor.moveToPosition(position);
        // Get the _ID value
        mContactId = Long.valueOf(CONTACT_ID_INDEX);
        // Get the selected LOOKUP KEY
        mContactKey = getString(LOOKUP_KEY_INDEX);
        // Create the contact's content Uri
        mContactUri = Contacts.getLookupUri(mContactId, mContactKey);*/
        
     
        
        /*
         * You can use mContactUri as the content URI for retrieving
         * the details for a contact.
         */
    }


	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
        // Put the result Cursor in the adapter for the ListView
        mCursorAdapter.swapCursor(arg1);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
        // Delete the reference to the existing Cursor
        mCursorAdapter.swapCursor(null);
		
	}
	
    // Defines the text expression
    @SuppressLint("InlinedApi")
    private static final String SELECTION =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
            Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?" :
            Contacts.DISPLAY_NAME + " LIKE ?";
    // Defines a variable for the search string
    private String mSearchString;
    // Defines the array to hold values that replace the ?
    private String[] mSelectionArgs = { mSearchString };

}
