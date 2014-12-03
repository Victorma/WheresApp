package com.wheresapp.bussiness.routes;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Victorma on 26/11/2014.
 */
public interface ASRoutes {


    /**
     * Calcula la ruta dadas las coordenadas de inicio y de fin
     * @param from: inicio
     * @param to: fin
     * @return La ruta calculada
     */
    public Ruta getRuta(LatLng from, LatLng to);

    /**
     *
     * Dada una ruta, actualiza con la nueva posición
     * @param ruta: Ruta calculada anteriormente
     * @param to: fin
     * @return La ruta actualizada
     */
    public Ruta updateDestinoRuta(Ruta ruta, LatLng to);

}
