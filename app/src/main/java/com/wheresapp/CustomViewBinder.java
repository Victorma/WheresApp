package com.wheresapp;

import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;

/**
 * Created by Paloma on 03/12/2014.
 */
public class CustomViewBinder implements ViewBinder {
    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        if (columnIndex == cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)) {
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
