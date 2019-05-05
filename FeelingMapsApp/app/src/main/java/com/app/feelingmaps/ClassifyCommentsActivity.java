package com.app.feelingmaps;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.feelingmaps.adapters.CategoriesAdapter;
import com.app.feelingmaps.adapters.CommentsAdapter;
import com.app.feelingmaps.adapters.RecyclerTouchListener;
import com.app.feelingmaps.models.Categories;
import com.app.feelingmaps.models.Comments;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassifyCommentsActivity extends AppCompatActivity {

    private List<Categories> categoriesList = new ArrayList<>();
    private List<Comments> commentsList = new ArrayList<>();
    private LinearLayoutManager categoriesLayoutManager;
    CommentsAdapter commentsAdapter;
    private LinearLayoutManager commentsLayoutManager;
    private static final int MY_SOCKET_TIMEOUT_MS = 20000;
    private RequestQueue requestQueue; // This is our requests queue to process our HTTP requests.
    private int id;
    private String cityId;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify_comments);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                id = 0;
                cityId = "";
            } else {
                id= extras.getInt("id");
                cityId= extras.getString("cityId");
            }
        } else {
            id= (int) savedInstanceState.getSerializable("id");
            cityId= (String) savedInstanceState.getSerializable("cityId");
        }
        email = GetPreferences("email");

        final Button rate = findViewById(R.id.bt_rate);
        final RatingBar ratingbar = findViewById(R.id.rb_rate);
        final EditText commentBox = findViewById(R.id.et_comment);

        requestQueue = Volley.newRequestQueue(this); // This setups up a new request queue which we will need to make HTTP requests.

        setUpCategories();

        commentsAdapter = new CommentsAdapter(this, commentsList);
        setUpComments();
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final float rating = ratingbar.getRating();
                final String comment = commentBox.getText().toString();
                String categoriesString = "";

                for (int i = 0 ; i< categoriesList.size();i++){
                    if(categoriesList.get(i).isSelected()){
                        if(categoriesString != ""){
                            categoriesString += ",";
                        }
                        categoriesString += categoriesList.get(i).getCategory();
                    }
                }
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("rating", String.valueOf(rating));
                params.put("comment", comment);
                params.put("id", String.valueOf(id));
                params.put("cityId", cityId);
                params.put("categories", categoriesString);
                params.put("email", email);
                JsonObjectRequest arrReq = new JsonObjectRequest(Request.Method.POST, getResources().getString(R.string.ip) + "/api/Comments", new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Check the length of our response (to see if the user has any repos)
                                try {
                                    JSONArray locals = response.getJSONArray("response");
                                    if (locals.length() > 0) {

                                    } else {
                                        // The user didn't have any repos.
                                        //setRepoListText("No repos found.");
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
                arrReq.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(arrReq);
            }
        });
        JsonObjectRequest arrReq = new JsonObjectRequest(Request.Method.GET, getResources().getString(R.string.ip) + "/api/Comments/"+cityId+"/"+id+"/All", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Check the length of our response (to see if the user has any repos)
                        try {
                            JSONArray comments = response.getJSONArray("response");
                            List<Comments> commentsList = new ArrayList<>();
                            if (comments.length() > 0) {
                                for (int idx = 0 ; idx<comments.length();idx++){

                                }

                                commentsAdapter.notifyDataSetChanged();
                            } else {
                                // The user didn't have any repos.
                                //setRepoListText("No repos found.");
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
        arrReq.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(arrReq);
    }

    private void setUpCategories(){

        final RecyclerView categories = findViewById(R.id.rv_cat);
        categoriesList.add(new Categories("Entretenimento"));
        categoriesList.add(new Categories("Comercial"));
        categoriesList.add(new Categories("Lazer"));
        categoriesList.add(new Categories("Desporto"));
        final CategoriesAdapter categoriesAdapter = new CategoriesAdapter(this, categoriesList);

        categoriesLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(categories.getContext(),categoriesLayoutManager.getOrientation());

        categories.setHasFixedSize(true);
        categories.addItemDecoration(dividerItemDecoration);
        categories.setAdapter(categoriesAdapter);
        categories.setLayoutManager(categoriesLayoutManager);

        categories.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), categories, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                categoriesList.get(position).select();
                categoriesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLongClick(View view, int position) {
                categoriesList.get(position).select();
                categoriesAdapter.notifyDataSetChanged();
            }
        }));
    }

    private void setUpComments(){

        final RecyclerView comments = findViewById(R.id.rv_comments);

        commentsLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(comments.getContext(),commentsLayoutManager.getOrientation());

        comments.setHasFixedSize(true);
        comments.addItemDecoration(dividerItemDecoration);
        comments.setAdapter(commentsAdapter);
        comments.setLayoutManager(commentsLayoutManager);
    }


    private String GetPreferences(String key){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPreferences.getString(key, "");
    }
}
