package com.wheresapp.bussiness.routes.imp;

import com.wheresapp.bussiness.routes.ASRoutes;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.model.LatLng;
import com.wheresapp.model.Ruta;
import com.wheresapp.model.ServiceHandler;
/**
 * Created by Victorma on 26/11/2014.
 */
public class ASRoutesImp implements ASRoutes {


    private Context context;

    public ASRoutesImp (Context context) {
        this.context = context;
    }

    @Override
    public Ruta getRuta(LatLng from, LatLng to) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        //URL
        String url = "http://www.mapquestapi.com/directions/v2/route";
        //Lista de parámetros
        List<BasicNameValuePair> lista = new ArrayList<BasicNameValuePair>();
        lista.add(new BasicNameValuePair("key", "Fmjtd|luurn9ub2g%2C70%3Do5-9wzx54"));
        lista.add(new BasicNameValuePair("from", String.valueOf(from.latitude)+ ","+ String.valueOf(from.longitude)));
        lista.add(new BasicNameValuePair("to", String.valueOf(to.latitude)+ ","+ String.valueOf(to.longitude)));
        lista.add(new BasicNameValuePair("outFormat", "json"));
        lista.add(new BasicNameValuePair("fullShape", "true"));
        lista.add(new BasicNameValuePair("narrativeType", "none"));
        lista.add(new BasicNameValuePair("locale", "es_ES"));
        lista.add(new BasicNameValuePair("routeType",sharedPref.getString("TYPE_ROUTE", "pedestrian")));

        Ruta ruta = null;
        ruta =  descargarRuta(url,lista);
        ruta.setInicio(from);
        ruta.setFin(to);
        return ruta;
    }

    @Override
    public Ruta updateDestinoRuta(Ruta ruta, LatLng to) {
        Ruta newRuta = null;
        // Si el destino no se ha movido más de 30 metros no actualizamos la ruta
        float [] distancias = new float[1];
        try{
            Location.distanceBetween(ruta.getFin().latitude, ruta.getFin().longitude,
                    to.latitude, to.longitude, distancias );
        }catch (Exception e){
            e.printStackTrace();
        }
        if (distancias[0] < 30){
            //Misma ruta
            newRuta = ruta;
        }else{
            //Calculamos nueva ruta
            newRuta = getRuta(ruta.getInicio(),to);
        }



        return newRuta;
    }
    /**
     * Recibe la url a descargar
     * @author jesusmartin92
     *
     */
    public Ruta descargarRuta(String url, List<BasicNameValuePair> lista) {
        // Creating service handler class instance
        ServiceHandler sh = new ServiceHandler();
        // Making a request to url and getting response
        String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET,lista);
        ArrayList<LatLng> puntos = new ArrayList<LatLng>();
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                jsonObj = jsonObj.getJSONObject("route");
                jsonObj = jsonObj.getJSONObject("shape");
                JSONArray jsonArray = jsonObj.getJSONArray("shapePoints");

                for(int i = 0; i < jsonArray.length(); i +=2){
                    Double lat = jsonArray.getDouble(i);
                    Double lng = jsonArray.getDouble(i+1);
                    puntos.add(new LatLng(lat,lng));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return new Ruta(puntos);
    }

}

