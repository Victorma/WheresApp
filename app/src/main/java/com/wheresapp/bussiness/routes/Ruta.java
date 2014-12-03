package com.wheresapp.bussiness.routes;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by jesusmartin92 on 3/12/14.
 */
public class Ruta {
    private ArrayList<LatLng> puntos;

    public Ruta(ArrayList<LatLng> puntos) {
        this.puntos = puntos;
    }


    public ArrayList<LatLng> getPuntos() {
        return puntos;
    }

    public void setPuntos(ArrayList<LatLng> puntos) {
        this.puntos = puntos;
    }


}
