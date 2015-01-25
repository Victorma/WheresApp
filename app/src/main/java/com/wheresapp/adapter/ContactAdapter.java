package com.wheresapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wheresapp.R;
import com.wheresapp.model.Contact;

import java.util.List;

/**
 * Created by Sergio on 09/11/2014.
 */
public class ContactAdapter extends ArrayAdapter<Contact> {

    private LayoutInflater mInflater;


    public ContactAdapter(Context context) {
        super(context, R.layout.contacts_list_item);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public void setData(List<Contact> data) {
        clear();
        if (data != null) {
            addAll(data);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.contacts_list_item,parent,false);
        Contact item = getItem(position);

        ImageView image = (ImageView) view.findViewById(android.R.id.icon);
        TextView name = (TextView) view.findViewById(android.R.id.text1);
        name.setText(item.getName());

        if (item.getImageURI()==null) {
            image.setImageResource(R.drawable.ic_action_person);
        } else {
            image.setImageURI(Uri.parse(item.getImageURI()));
        }

        return view;
    }

    @Override
    public long getItemId(int position) {
        Contact c = getItem(position);
        return new Long(c.getServerid());
    }


}
