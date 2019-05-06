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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.feelingmaps.models.FrequenciaZonas;
import com.app.feelingmaps.models.Ocurrencias;
import com.google.android.gms.maps.GoogleMap;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

class DebugHelper {




    private RequestQueue requestQueue; // This is our requests queue to process our HTTP requests.
    private List<PolygonCustom> gridBlocks = new ArrayList<PolygonCustom>();
    private Button button;
    private RatingBar ratingBar;
    private TextView categoriasView;
    private TextView comentarioView;
    private List<FrequenciaZonas> frequenciaZonas = new ArrayList<>();
    ;

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

        String url = context.getResources().getString(R.string.ip) + "/api/CityInfo/" + cityId;
        JsonObjectRequest arrReq = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Check the length of our response (to see if the user has any repos)
                        try {
                            JSONArray locals = response.getJSONArray("response");
                            Log.e("fds", " : "+response.toString());
                            if (locals.getJSONObject(0).getString("idCZ") != "null" ) {
                                for(int i=0 ; i<locals.length();i++){
                                    FrequenciaZonas novaFrequencia = new FrequenciaZonas(locals.getJSONObject(i).getString("idCZ"),locals.getJSONObject(0).getString("comentario"));
                                    frequenciaZonas.add(novaFrequencia);
                                }
                                int count = 0;
                                for(int a = 0 ; a<gridBlocks.size();a++) {
                                    int id = gridBlocks.get(a).id;
                                    for(int k = 0; k < frequenciaZonas.size();k++){
                                        if(id==Integer.valueOf(frequenciaZonas.get(k).getZona())){
                                            count++;
                                        }
                                    }if(count>2){
                                        gridBlocks.get(a).area.setFillColor(0x77FF0000);
                                        gridBlocks.get(a).area.setStrokeColor(Color.RED);
                                        gridBlocks.get(a).area.setStrokeWidth(3);
                                    }
                                    if(count<2&&count>0){
                                        gridBlocks.get(a).area.setFillColor(0x22FF0000);
                                        gridBlocks.get(a).area.setStrokeColor(Color.RED);
                                        gridBlocks.get(a).area.setStrokeWidth(3);
                                    }
                                    count = 0;

                                }



                            } else {
                                Toast.makeText(context.getApplicationContext(), "There are no record in this zone",
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If there a HTTP error then add a note to our repo list.
                        //setRepoListText("Error while calling REST API");
                        Log.e("Volley", error.toString());
                    }
                }
        );

        requestQueue.add(arrReq);


        map.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener(){
            public void onPolygonClick(final Polygon polygon) {
                for(int i = 0 ; i<gridBlocks.size();i++){
                    if(gridBlocks.get(i).equals(polygon)){
                        final int id = gridBlocks.get(i).id;


                        LayoutInflater inflater = (LayoutInflater)
                                context.getSystemService(LAYOUT_INFLATER_SERVICE);
                        final View popupView = inflater.inflate(R.layout.classification_map, null);

                        String url = context.getResources().getString(R.string.ip)  + "/api/CityId/"+cityId+"/ZoneId/"+id ;
                        Log.v("Volley",url);


                        JsonObjectRequest arrReq = new JsonObjectRequest(Request.Method.GET, url, null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        // Check the length of our response (to see if the user has any repos)
                                        try {
                                            JSONArray locals = response.getJSONArray("response");
                                            Log.e("fds", " : "+response.toString());
                                            if (locals.getJSONObject(0).getString("comentario") != "null" ) {


                                                categoriasView = (TextView)popupView.findViewById(R.id.cat);
                                                comentarioView = (TextView)popupView.findViewById(R.id.coment1);
                                                ratingBar = (RatingBar)popupView.findViewById(R.id.MyRating);

                                                String comentario[]=locals.getJSONObject(0).getString("comentario").split(",");
                                                List<String> cate= Arrays.asList(locals.getJSONObject(0).getString("categoria").split(","));

                                                List<Ocurrencias> ocorrencia = new ArrayList<>();
                                                ocorrencia.add(new Ocurrencias("Entretenimento"));
                                                ocorrencia.add(new Ocurrencias("Comercial"));
                                                ocorrencia.add(new Ocurrencias("Lazer"));
                                                ocorrencia.add(new Ocurrencias("Desporto"));

                                                for (int j = 0; j<cate.size();j++) {
                                                    for (int i = 0; i < ocorrencia.size(); i++) {
                                                        if (ocorrencia.get(i).getKey().equals(cate.get(j))) {
                                                            ocorrencia.get(i).addOccurence();
                                                            i = ocorrencia.size();
                                                        }
                                                    }
                                                }
                                                Collections.sort(ocorrencia, new Comparator<Ocurrencias>() {
                                                    @Override
                                                    public int compare(Ocurrencias o1, Ocurrencias o2) {
                                                        if (o1.getValue()>o2.getValue()){
                                                            return -1;
                                                        }
                                                        if (o1.getValue()<o2.getValue()){
                                                            return 1;
                                                        }
                                                        return 0;
                                                    }
                                                });

                                                String top3 = "";

                                                if(ocorrencia.get(0).getValue()!=0){
                                                    top3 = top3 + ocorrencia.get(0).getKey();
                                                }if (ocorrencia.get(1).getValue()!=0){
                                                    top3 = top3 + ocorrencia.get(1).getKey();
                                                }if (ocorrencia.get(2).getValue()!=0){
                                                    top3 = top3 + ocorrencia.get(2).getKey();
                                                }


                                                comentarioView.setText(comentario[0]);
                                                ratingBar.setRating(Float.parseFloat(locals.getJSONObject(0).getString("classificacao")));
                                                categoriasView.setText(top3);

                                            } else {
                                                Toast.makeText(context.getApplicationContext(), "There are no record in this zone",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                    }
                                },

                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // If there a HTTP error then add a note to our repo list.
                                        //setRepoListText("Error while calling REST API");
                                        Log.e("Volley", error.toString());
                                    }
                                }
                        );

                        requestQueue.add(arrReq);
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