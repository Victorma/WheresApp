package com.wheresapp.bussiness.routes.imp;

import com.wheresapp.bussiness.routes.ASRoutes;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.wheresapp.bussiness.routes.Ruta;
import com.wheresapp.bussiness.routes.ServiceHandler;
/**
 * Created by Victorma on 26/11/2014.
 */
public class ASRoutesImp implements ASRoutes {


    @Override
    public Ruta getRuta(LatLng from, LatLng to) {
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

        Ruta ruta = null;
        DescargaCoordenadas tarea = new DescargaCoordenadas();
        tarea.execute(url,lista);
        try {
            ruta = tarea.get();
        }catch (Exception e){
            e.printStackTrace();
        }
        return ruta;
    }

    @Override
    public Ruta updateDestinoRuta(Ruta ruta, LatLng to) {
        // TODO Auto-generated method stub
        return null;
    }
    /**
     * Recibe la url a descargar
     * @author jesusmartin92
     *
     */
    class DescargaCoordenadas extends AsyncTask<Object, Void, Ruta>{


        @Override
        protected Ruta doInBackground(Object... params) {


            String url = (String)params[0];
            List<BasicNameValuePair> lista = (List<BasicNameValuePair>)params[1];
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

}

