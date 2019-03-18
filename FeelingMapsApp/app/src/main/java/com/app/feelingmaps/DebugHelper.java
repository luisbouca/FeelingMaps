package com.app.feelingmaps;

/*
 * Copyright (C) 2013 Maciej Górski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

class DebugHelper {

    private List<PolygonCustom> gridBlocks = new ArrayList<PolygonCustom>();

    void drawGrid(GoogleMap map,LatLngBounds bounds,LatLngBounds screen) {
        cleanup();


        double minY = bounds.southwest.latitude;
        double minX = bounds.southwest.longitude;
        double maxY = bounds.northeast.latitude;
        double maxX = bounds.northeast.longitude;

        boolean done = false;
        while(!done){
            done = true;
            if(minY>90){
                minY-=180;
                done = false;
            }else if(minY<-90){
                minY+=180;
                done = false;
            }
            if(maxY>90){
                maxY-=180;
                done = false;
            }else if(maxY<-90){
                maxY+=180;
                done = false;
            }

            if(minX>180){
                minX-=360;
                done = false;
            }else if(minX<-180){
                minX+=360;
                done = false;
            }
            if(maxX>180){
                maxX-=360;
                done = false;
            }else if(maxX<-180){
                maxX+=360;
                done = false;
            }
        }

        double avgY = maxY-minY;
        double avgX =  maxX - minX;
        double clusterSizeX = (avgX * avgX)/6;
        double clusterSizeY = (avgY * avgY)/6;
        int i = 0;
        int maxJ = (int) Math.round(avgX/clusterSizeY);
        for(double y = minY;y+clusterSizeY<=maxY;y=y+clusterSizeY){
            int j = 0;
            for(double x = minX;x+clusterSizeX<=maxX;x=x+clusterSizeX){
                PolygonOptions newPolygon = new PolygonOptions().strokeWidth(2.0f).add(new LatLng(y,x)).add(new LatLng(y+clusterSizeY,x)).add(new LatLng(y+clusterSizeY,x+clusterSizeX)).add(new LatLng(y,x+clusterSizeX));
                if(screen.contains(new LatLng(y,x)) || screen.contains(new LatLng(y+clusterSizeY,x+clusterSizeX))) {
                    gridBlocks.add(new PolygonCustom(map.addPolygon(newPolygon), (maxJ * i) + j));
                }
                j++;
            }
            i++;
        }


    }

    void cleanup() {

        for (PolygonCustom polygon : gridBlocks) {
            polygon.remove();
        }
        gridBlocks.clear();
    }
}