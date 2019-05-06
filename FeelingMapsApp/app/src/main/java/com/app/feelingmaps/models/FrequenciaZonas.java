package com.app.feelingmaps.models;

public class FrequenciaZonas {
    String zona;
    String comentarios;


    public FrequenciaZonas(String idzona, String idcomentarios) {
        zona = idzona;
        comentarios = idcomentarios;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

}
