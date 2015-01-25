package com.wheresapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wheresapp.R;
import com.wheresapp.adapter.CallAdapter;
import com.wheresapp.integration.contacts.factory.DAOContactsFactory;
import com.wheresapp.loader.CallListLoader;
import com.wheresapp.model.Call;
import com.wheresapp.model.Contact;

import java.util.List;


public class ContactDataActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<List<Call>> {

    boolean favourite = false;
    private ImageView imagenContacto;
    private TextView textoNombre;
    private MenuItem itemFavorito;
    private ListView listView;
    private Contact contact;
    // This is the Adapter being used to display the list's data.
    CallAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_data);

        Intent i = getIntent();
        Bundle bundle = i.getBundleExtra("USER");
        contact = (Contact) bundle.getSerializable("USER");
        imagenContacto = (ImageView) findViewById(R.id.imageView);
        listView = (ListView) findViewById(R.id.listView);

        mAdapter =  new CallAdapter(this);
        listView.setAdapter(mAdapter);

        if (contact.getImageURI()!=null)
            imagenContacto.setImageURI(Uri.parse(contact.getImageURI()));
        textoNombre = (TextView) findViewById(R.id.nombre_contacto);
        textoNombre.setText(contact.getName());
        if  (contact.getFavourite() != null)
            this.favourite = contact.getFavourite();
        else
            this.favourite = false;

        getSupportLoaderManager().initLoader(0, null, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_data, menu);
        itemFavorito = (MenuItem) menu.findItem(R.id.action_important);
        if (this.favourite) {
            itemFavorito.setIcon(R.drawable.ic_action_important);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // When the user clicks REFRESH
            case R.id.action_call:
                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtra("TOUSER", contact);
                startActivity(intent);
                return true;
            // When the user clicks REFRESH
            case R.id.action_important:
                if (favourite){
                    eliminarFavorito();
                } else {
                    anadirFavorito();
                }
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void anadirFavorito() {
        itemFavorito.setIcon(R.drawable.ic_action_important);
        favourite = true;
        contact.setFavourite(favourite);
        DAOContactsFactory.getInstance().getInstanceDAOContacts(this).update(contact);
        Toast.makeText(this,"Se ha añadido a favorito",Toast.LENGTH_LONG).show();
    }

    private void eliminarFavorito() {
        itemFavorito.setIcon(R.drawable.ic_action_not_important);
        favourite = false;
        contact.setFavourite(favourite);
        DAOContactsFactory.getInstance().getInstanceDAOContacts(this).update(contact);
        Toast.makeText(this,"Se ha eliminado de favorito",Toast.LENGTH_LONG).show();
    }

    @Override
    public Loader<List<Call>> onCreateLoader(int id, Bundle args) {
        return new CallListLoader(this,contact.getServerid());
    }

    @Override
    public void onLoadFinished(Loader<List<Call>> loader, List<Call> data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Call>> loader) {
        // Clear the data in the adapter.
        mAdapter.setData(null);
    }
}
