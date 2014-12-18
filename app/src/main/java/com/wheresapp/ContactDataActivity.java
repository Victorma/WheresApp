package com.wheresapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wheresapp.integration.contacts.factory.DAOContactsFactory;
import com.wheresapp.modelTEMP.Contact;


public class ContactDataActivity extends Activity {

    boolean favourite = false;
    private ImageView imagenContacto;
    private TextView textoNombre;
    private MenuItem itemFavorito;
    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_data);

        Intent i = getIntent();
        Bundle bundle = i.getBundleExtra("USER");
        contact = (Contact) bundle.getSerializable("USER");
        imagenContacto = (ImageView) findViewById(R.id.imageView);

        if (contact.getImageURI()!=null)
            imagenContacto.setImageURI(Uri.parse(contact.getImageURI()));
        textoNombre = (TextView) findViewById(R.id.nombre_contacto);
        textoNombre.setText(contact.getName());
        if  (contact.getFavourite() != null)
            this.favourite = contact.getFavourite();
        else
            this.favourite = false;


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
}
