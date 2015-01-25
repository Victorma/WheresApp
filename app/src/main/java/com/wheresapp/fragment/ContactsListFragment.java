package com.wheresapp.fragment;

import android.support.v4.app.ListFragment; //android.app.ListFragment;
import android.support.v4.app.LoaderManager; //android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.Loader; //android.content.Loader;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView; //android.support.v7.widget.SearchView
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener; //android.support.v7.widget.SearchView.OnQueryTextListener

import com.wheresapp.R;
import com.wheresapp.activity.ContactDataActivity;
import com.wheresapp.adapter.ContactAdapter;
import com.wheresapp.loader.ContactListLoader;
import com.wheresapp.model.Contact;

import java.util.List;


public class ContactsListFragment extends ListFragment
        implements OnQueryTextListener, OnCloseListener, LoaderManager.LoaderCallbacks<List<Contact>> {

    // This is the Adapter being used to display the list's data.
    ContactAdapter mAdapter;

    // The SearchView for doing filtering.
    SearchView mSearchView;

    private Boolean favourite=false;
    private Boolean recent=false;

    // If non-null, this is the current filter the user has provided.
    String mCurFilter;

    private static String COLUMN_ID = ContactsContract.RawContacts.SYNC1;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // handle fragment arguments
        Bundle arguments = getArguments();
        setHasOptionsMenu(true);
        if (arguments.containsKey("TAB")) {
            Integer tab = arguments.getInt("TAB");
            if (tab == 2)
                this.favourite = true;
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
        if (favourite)
            setEmptyText("No favourite contacts");
        else if (recent)
            setEmptyText("No recent calls");
        else
            setEmptyText("No contacts");

        //Ahora se hace asi porque aún no hay un Fragment para cada lista
        Intent i = getActivity().getIntent();

        // We have a menu item to show in action bar.
        setHasOptionsMenu(true);

        mAdapter =  new ContactAdapter(getActivity());
        setListAdapter(mAdapter);

        setListShown(false);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (recent)
            inflater.inflate(R.menu.menu_list_call,menu);
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
        // Called when the action bar search text has changed.  Since this
        // is a simple array adapter, we can just have it do the filtering.
        mCurFilter = !TextUtils.isEmpty(newText) ? newText : null;
        mAdapter.getFilter().filter(mCurFilter);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // When the user clicks REFRESH
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Insert desired behavior here.
        Log.i("LoaderCustom", "Item clicked: " + id);
        Intent intent = new Intent(getActivity(),ContactDataActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("USER",mAdapter.getItem(position));
        intent.putExtra("USER",bundle);
        startActivity(intent);
    }

    @Override
    public Loader<List<Contact>> onCreateLoader(int id, Bundle args) {
        return new ContactListLoader(getActivity(),favourite,recent);
    }

    @Override
    public void onLoadFinished(Loader<List<Contact>> loader, List<Contact> data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mAdapter.setData(data);

        // The list should now be shown.
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Contact>> loader) {
        // Clear the data in the adapter.
        mAdapter.setData(null);
    }

}
