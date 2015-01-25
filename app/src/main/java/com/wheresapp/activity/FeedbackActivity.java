package com.wheresapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.wheresapp.R;


public class FeedbackActivity extends Activity {

    private Spinner mAsunto;
    private EditText mTexto;
    private CheckBox mDeviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        setTitle("Enviar feedback");
        mAsunto = (Spinner) findViewById(R.id.reporte_asunto);
        mTexto = (EditText) findViewById(R.id.reporte_texto);
        mDeviceInfo = (CheckBox) findViewById(R.id.reporte_device_info);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_feedback, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.menu_enviar:
                enviar();
            default:
                return false;
        }

    }

    private void enviar() {
        // Validar
        StringBuilder texto = new StringBuilder();
        texto.append(mTexto.getText().toString());
        if (TextUtils.isEmpty(texto)) {
            mTexto.setError("Un reporte vacío no sirve de nada");
        } else {
            // Información extra?
            texto.append( "\n\n=====================");
            texto.append("\n Versión: "+getString(R.string.app_version));
            if (mDeviceInfo.isChecked()) {
                texto.append( "\n\nInformación del dispositivo");
                texto.append( "\n---------- ");
                texto.append( "\n- Release: " + android.os.Build.VERSION.RELEASE);
                texto.append( "\n- SDK: " + android.os.Build.VERSION.SDK_INT);
                texto.append( "\n- Codename: " + android.os.Build.VERSION.CODENAME);
                texto.append( "\n- Incremental: " + android.os.Build.VERSION.INCREMENTAL);
                texto.append( "\n- Brand: " + android.os.Build.BRAND);
                texto.append( "\n- Device: " + android.os.Build.DEVICE);
                texto.append( "\n- Display: " + android.os.Build.DISPLAY);
                if (Build.VERSION.SDK_INT >= 8) {
                    texto.append( "\n- Hardware: " + android.os.Build.HARDWARE);
                }
                texto.append( "\n- Manufacturer: " + android.os.Build.MANUFACTURER);
                texto.append( "\n- Model: " + android.os.Build.MODEL);
                texto.append( "\n- Product: " + android.os.Build.PRODUCT);
            }
            mTexto.setError(null);
            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { getString(R.string.email_address) });
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                    getString(R.string.email_subject) + " - " + getResources().getStringArray(R.array.feedback_asuntos)[mAsunto.getSelectedItemPosition()]);
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, texto.toString());
            startActivity(Intent.createChooser(emailIntent, getString(R.string.email_intent)));
        }

    }
}
