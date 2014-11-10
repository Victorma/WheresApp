package com.wheresapp;

import android.app.ActionBar.LayoutParams;
import android.app.ListFragment; //android.support.v4.app.ListFragment;
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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView; //android.support.v7.widget.SearchView
import android.widget.SearchView.OnQueryTextListener; //android.support.v7.widget.SearchView.OnQueryTextListener
import android.widget.SimpleCursorAdapter; //android.support.v4.widget.SimpleCursorAdapter;

public class ContactsListFragment extends ListFragment
implements OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {

	// This is the Adapter being used to display the list's data.
	SimpleCursorAdapter mAdapter;

	// If non-null, this is the current filter the user has provided.
	String mCurFilter;

	@Override public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(getActivity());
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) getActivity().findViewById(android.R.id.content);
        root.addView(progressBar);
        
        // Give some text to display if there is no data.  In a real
		// application this would come from a resource.
		//setEmptyText("No phone numbers");

		// We have a menu item to show in action bar.
		setHasOptionsMenu(true);

		// Create an empty adapter we will use to display the loaded data.
		mAdapter = new SimpleCursorAdapter(getActivity(), 
				R.layout.contacts_list_item, null,
				new String[] {Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
						Contacts.DISPLAY_NAME_PRIMARY : Contacts.DISPLAY_NAME },
						new int[] { android.R.id.text1 }, 0);
		setListAdapter(mAdapter);

		// Prepare the loader.  Either re-connect with an existing one,
		// or start a new one.
		getLoaderManager().initLoader(0, null, this);
	}

	@Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.removeItem(R.id.action_new_chat);
		// Place an action bar item for searching.
		MenuItem item = menu.findItem(R.id.action_search);
		SearchView sv = new SearchView(getActivity());
		sv.setOnQueryTextListener(this);
		item.setActionView(sv);
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
		Intent intent = new Intent(getActivity(), ContactMap.class);
		startActivity(intent);
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

		return new CursorLoader(getActivity(), baseUri,
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