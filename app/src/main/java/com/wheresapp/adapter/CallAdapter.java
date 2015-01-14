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
import com.wheresapp.bussiness.contacts.factory.ASContactsFactory;
import com.wheresapp.modelTEMP.Call;
import com.wheresapp.modelTEMP.Contact;

import java.util.List;

/**
 * Created by Sergio on 09/11/2014.
 */
public class CallAdapter extends ArrayAdapter<Call> {

    private LayoutInflater mInflater;


    public CallAdapter(Context context) {
        super(context, R.layout.call_list_item);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public void setData(List<Call> data) {
        clear();
        if (data != null) {
            addAll(data);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.call_list_item,parent,false);
        Call item = getItem(position);

        ImageView image = (ImageView) view.findViewById(android.R.id.icon);
        TextView name = (TextView) view.findViewById(android.R.id.text1);
        ImageView typeCall = (ImageView) view.findViewById(R.id.typeCallIcon);

        Contact c = new Contact();

        if (item.isIncoming()) {
            typeCall.setImageResource(android.R.drawable.sym_call_incoming);
            c.setServerid(item.getSender());
            c = ASContactsFactory.getInstance().getInstanceASContacts(getContext()).getContact(c);
        } else {
            c.setServerid(item.getReceiver());
            c = ASContactsFactory.getInstance().getInstanceASContacts(getContext()).getContact(c);
        }

        name.setText(c.getName());

        if (c.getImageURI()==null) {
            image.setImageResource(R.drawable.ic_action_person);
        } else {
            image.setImageURI(Uri.parse(c.getImageURI()));
        }

        return view;
    }

    @Override
    public long getItemId(int position) {
        Call c = getItem(position);
        if (c.isIncoming())
            return new Long(c.getSender());
        else
            return new Long(c.getReceiver());
    }


}
