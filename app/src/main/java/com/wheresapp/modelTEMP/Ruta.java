package com.wheresapp.modelTEMP;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by jesusmartin92 on 3/12/14.
 */
public class Ruta {
    private ArrayList<LatLng> puntos;
    private LatLng inicio;
    private LatLng fin;


    public Ruta(ArrayList<LatLng> puntos){
        this.puntos = puntos;
    }

    public Ruta(ArrayList<LatLng> puntos, LatLng inicio, LatLng fin) {
        this.puntos = puntos;
        this.inicio = inicio;
        this.fin = fin;
    }


    public ArrayList<LatLng> getPuntos() {
        return puntos;
    }

    public void setPuntos(ArrayList<LatLng> puntos) {
        this.puntos = puntos;
    }

    public LatLng getInicio() {
        return inicio;
    }

    public void setInicio(LatLng inicio) {
        this.inicio = inicio;
    }

    public LatLng getFin() {
        return fin;
    }

    public void setFin(LatLng fin) {
        this.fin = fin;
    }

}
