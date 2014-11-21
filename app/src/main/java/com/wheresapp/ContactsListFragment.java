package com.wheresapp;

import android.app.ActionBar.LayoutParams;
import android.app.ListFragment; //android.support.v4.app.ListFragment;
import android.app.LoaderManager; //android.support.v4.app.LoaderManager;
import android.content.Context;
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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView; //android.support.v7.widget.SearchView
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener; //android.support.v7.widget.SearchView.OnQueryTextListener
import android.widget.SimpleCursorAdapter; //android.support.v4.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class ContactsListFragment extends ListFragment
        implements OnQueryTextListener, OnCloseListener, LoaderManager.LoaderCallbacks<Cursor> {

    // This is the Adapter being used to display the list's data.
    SimpleCursorAdapter mAdapter;

    // The SearchView for doing filtering.
    SearchView mSearchView;

    // If non-null, this is the current filter the user has provided.
    String mCurFilter;

    /*private static final String TAG = "UserListActivity";
    private Intent intent;
    MessageSender messageSender;
    GoogleCloudMessaging gcm;*/
    String userNumber;

    private class CustomViewBinder implements SimpleCursorAdapter.ViewBinder {

        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            if (columnIndex == cursor.getColumnIndex(Contacts.PHOTO_URI)) {
                String src = cursor.getString(columnIndex);
                ImageView im = (ImageView) view.findViewById(android.R.id.icon);
                if (src != null) {
                    im.setImageURI(Uri.parse(src));
                } else {
                    im.setImageResource(R.drawable.ic_action_person);
                }
                return true;
            }
            // For others, we simply return false so that the default binding
            // happens.
            return false;
        }
    }

    public static class MySearchView extends SearchView {
        public MySearchView(Context context) {
            super(context);
        }

        // The normal SearchView doesn't clear its search text when
        // collapsed, so we will do this for it.
        @Override
        public void onActionViewCollapsed() {
            setQuery("", false);
            super.onActionViewCollapsed();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Intent i = getActivity().getIntent();
        userNumber = i.getStringExtra("NUMBER");

        // We have a menu item to show in action bar.
        setHasOptionsMenu(true);

        // Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(getActivity());
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) getActivity().findViewById(android.R.id.content);
        root.addView(progressBar);

        /*intent = new Intent(this, GCMNotificationIntentService.class);
        registerReceiver(broadcastReceiver, new IntentFilter("com.wheresapp.contactslist"));
        messageSender = new MessageSender();
        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());*/

        /*Resources resources = this.getResources();
        int iconId = R.drawable.ic_action_person;
         */// Create an empty adapter we will use to display the loaded data.
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.contacts_list_item, null,
                new String[] { Contacts.PHOTO_URI,
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                                Contacts.DISPLAY_NAME_PRIMARY : Contacts.DISPLAY_NAME },
                new int[] {android.R.id.icon, android.R.id.text1 }, 0);
        mAdapter.setViewBinder(new CustomViewBinder());
        setListAdapter(mAdapter);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Place an action bar item for searching.
        MenuItem item = menu.add("Search");
        item.setIcon(android.R.drawable.ic_menu_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM
                | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        mSearchView = new MySearchView(getActivity());
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);
        mSearchView.setIconifiedByDefault(true);
        item.setActionView(mSearchView);
    }

    public boolean onQueryTextChange(String newText) {
        // Called when the action bar search text has changed.  Update
        // the search filter, and restart the loader to do a new query
        // with this filter.
        String newFilter = !TextUtils.isEmpty(newText) ? newText : null;
        // Don't do anything if the filter hasn't actually changed.
        // Prevents restarting the loader when restoring state.
        if (mCurFilter == null && newFilter == null) {
            return true;
        }
        if (mCurFilter != null && mCurFilter.equals(newFilter)) {
            return true;
        }
        mCurFilter = newFilter;
        getLoaderManager().restartLoader(0, null, this);
        return true;
    }

    @Override public boolean onQueryTextSubmit(String query) {
        // Don't care about this.
        return true;
    }

    @Override
    public boolean onClose() {
        if (!TextUtils.isEmpty(mSearchView.getQuery())) {
            mSearchView.setQuery(null, true);
        }
        return true;
    }

    @Override public void onListItemClick(ListView l, View v, int position, long id) {
        // Insert desired behavior here.
        Intent intent = new Intent(getActivity(), MapActivity.class);
        intent.putExtra("TOUSER", id);
        startActivity(intent);
    }

    // These are the Contacts rows that we will retrieve.
    private static final String[] PROJECTION = new String[] {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                    Contacts.DISPLAY_NAME_PRIMARY : Contacts.DISPLAY_NAME, Contacts._ID,
            Contacts.LOOKUP_KEY, Contacts.PHOTO_FILE_ID, Contacts.PHOTO_URI};

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

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(getActivity(), baseUri,
                PROJECTION, SELECTION, null, SORT_ORDER);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mAdapter.swapCursor(data);

        // The list should now be shown.
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }
}
