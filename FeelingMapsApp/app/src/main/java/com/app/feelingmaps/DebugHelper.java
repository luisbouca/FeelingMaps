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

import android.content.Context;
import android.content.Intent;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;

import android.content.SharedPreferences;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

class DebugHelper {




    private List<PolygonCustom> gridBlocks = new ArrayList<PolygonCustom>();
    private Button button;
    private RequestQueue requestQueue;

    void drawGrid(GoogleMap map, LatLngBounds bounds, LatLngBounds screen, final Context context,final View anchorview, final String cityId) {
        cleanup();
        requestQueue = Volley.newRequestQueue(context);


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
        int i = 0,j = 0;
        int maxJ = (int) Math.round(avgX/clusterSizeY);
        for(double y = minY;y+clusterSizeY<=maxY;y=y+clusterSizeY){
            j = 0;
            for(double x = minX;x+clusterSizeX<=maxX;x=x+clusterSizeX){
                PolygonOptions newPolygon = new PolygonOptions().strokeWidth(2.0f).add(new LatLng(y,x)).add(new LatLng(y+clusterSizeY,x)).add(new LatLng(y+clusterSizeY,x+clusterSizeX)).add(new LatLng(y,x+clusterSizeX));
                newPolygon.clickable(true);
                if(screen.contains(new LatLng(y,x)) || screen.contains(new LatLng(y+clusterSizeY,x+clusterSizeX))) {
                    gridBlocks.add(new PolygonCustom(map.addPolygon(newPolygon), (maxJ * i) + j));
                }
                j++;
            }
            i++;
        }
        if(gridBlocks.size()>0) {
            String url = context.getResources().getString(R.string.ip) + "/api/City/" + cityId + "/" + ((maxJ * i) + j);
            JsonObjectRequest arrReq = new JsonObjectRequest(Request.Method.GET, url, null, null, null);
            requestQueue.add(arrReq);
        }
        map.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener(){
            public void onPolygonClick(Polygon polygon) {
                for(int i = 0 ; i<gridBlocks.size();i++){
                    if(gridBlocks.get(i).equals(polygon)){
                        final int id = gridBlocks.get(i).id;

                        Toast.makeText(context.getApplicationContext(), "This is my Toast message!",
                                Toast.LENGTH_LONG).show();

                        LayoutInflater inflater = (LayoutInflater)
                                context.getSystemService(LAYOUT_INFLATER_SERVICE);
                        View popupView = inflater.inflate(R.layout.classification_map, null);

                        button = (Button) popupView.findViewById(R.id.verEComenta);

                        // create the popup window
                        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                        Display display = wm.getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);

                        int height = (size.y)/2+250;
                                int width =(size.x)/2+400;
                        boolean focusable = true; // lets taps outside the popup also dismiss it
                        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                        // show the popup window
                        // which view you pass in doesn't matter, it is only used for the window tolken
                        popupWindow.showAtLocation(anchorview, Gravity.CENTER, 0, 0);

                        button.setOnClickListener( new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context,ClassifyCommentsActivity.class);
                                intent.putExtra("id",id);
                                intent.putExtra("cityId",cityId);
                                context.startActivity(intent);
                            }
                        });


                    }
                }

            }
        });


    }

    public void comentaVer(){

    }

    void cleanup() {

        for (PolygonCustom polygon : gridBlocks) {
            polygon.remove();
        }
        gridBlocks.clear();
    }

}