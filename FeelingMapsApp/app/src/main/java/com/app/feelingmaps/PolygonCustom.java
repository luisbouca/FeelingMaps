package com.app.feelingmaps;

import com.google.android.gms.maps.model.Polygon;

public class PolygonCustom {
    Polygon area;
    int id;

    public PolygonCustom(Polygon area, int id) {
        this.area = area;
        this.id = id;
    }

    public void remove() {
        area.remove();
    }
}
