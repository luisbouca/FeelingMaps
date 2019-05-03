package com.app.feelingmaps;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Register extends AppCompatActivity  implements View.OnClickListener {

    private FrameLayout progressBarHolder;
    private AlphaAnimation btnClickAnimation = new AlphaAnimation(1F, 0.8F);
    private AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
    private AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
    private static final int MY_SOCKET_TIMEOUT_MS = 20000;
    private RequestQueue requestQueue; // This is our requests queue to process our HTTP requests.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
        //Setting event listener.
        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.btnRegister).setOnClickListener(this);

        requestQueue = Volley.newRequestQueue(this); // This setups up a new request queue which we will need to make HTTP requests.
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnRegister) {
            OpenProgressBar();
            new Registration().execute();
        } else {
            startActivity(new Intent(this, Login.class));
        }

        v.startAnimation(btnClickAnimation);
    }


    private void OpenProgressBar() {
        progressBarHolder.clearAnimation();
        progressBarHolder.animate().alpha(0.5f);
        inAnimation.setDuration(500);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);
    }

    private void CloseProgressBar() {
        outAnimation.setDuration(500);
        progressBarHolder.setAnimation(outAnimation);
        progressBarHolder.setVisibility(View.GONE);
        progressBarHolder.clearAnimation();
    }

    private void DisplayToastMsg(final String message){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(),  message, Toast.LENGTH_LONG).show();
            }
        });
    }


    private class Registration extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try
            {
                String user = ((EditText) findViewById(R.id.txtUser)).getText().toString();
                String email = ((EditText) findViewById(R.id.txtEmail)).getText().toString();
                String password = ((EditText) findViewById(R.id.txtPassword)).getText().toString();
                String passwordConfirm = ((EditText) findViewById(R.id.txtPasswordConfirm)).getText().toString();

                if(user.isEmpty() || user == null){
                    DisplayToastMsg("Atenção, utilizador em falta");
                    CloseProgressBar();
                }
                else if (email.isEmpty() || email == null)
                {
                    DisplayToastMsg("Atenção, email em falta");
                    CloseProgressBar();
                }
                else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    DisplayToastMsg("Introduza um email válido");
                    CloseProgressBar();
                }
                else if(password.isEmpty() || password == null)
                {
                    DisplayToastMsg("Atenção, password em falta");
                    CloseProgressBar();
                }
                else if(passwordConfirm.isEmpty() || passwordConfirm == null)
                {
                    DisplayToastMsg("Atenção, deve confirmar a password");
                    CloseProgressBar();
                }
                else if(!password.equals(passwordConfirm))
                {
                    DisplayToastMsg("Atenção, as passwords não coincidem");
                    CloseProgressBar();
                }
                else if(password.length() < 6 || passwordConfirm.length() < 6 || password.length() > 10 || passwordConfirm.length() > 10)
                {
                    DisplayToastMsg("A password deve ter entre 6 e 10 caracteres");
                    CloseProgressBar();
                }
                else
                {

                    String url = getResources().getString(R.string.ip) + "/api/Email/" + AESEncyption.encrypt(email.toLowerCase()) + "/Name/" + user;

                    JsonObjectRequest arrReq = new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        CloseProgressBar();
                                        Boolean registered = Integer.parseInt((response.getJSONArray("response")).getJSONObject(0).optString("registered")) > 0 ? true:false;

                                        if(!registered){

                                            String user = ((EditText) findViewById(R.id.txtUser)).getText().toString();
                                            String email = ((EditText) findViewById(R.id.txtEmail)).getText().toString();
                                            String password = ((EditText) findViewById(R.id.txtPassword)).getText().toString();

                                            String url = getResources().getString(R.string.ip) + "/api/Name/" + user + "/Email/" + AESEncyption.encrypt(email.toLowerCase())+"/Password/"+AESEncyption.encrypt(password);

                                            JsonObjectRequest arrReq = new JsonObjectRequest(Request.Method.GET, url, null,
                                                    new Response.Listener<JSONObject>() {
                                                        @Override
                                                        public void onResponse(JSONObject response) {
                                                            startActivity(new Intent(Register.this, Login.class));
                                                        }
                                                    },
                                                    new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            DisplayToastMsg("Ocorreu um erro durante o inicio de sessão, tente novamente");
                                                            // If there a HTTP error then add a note to our repo list.
                                                            Log.e("Volley", error.toString());
                                                            CloseProgressBar();
                                                        }
                                                    });
                                            arrReq.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                            requestQueue.add(arrReq);
                                        }
                                        else
                                        {
                                            DisplayToastMsg("Atenção, email ou utilizador já se encontra em uso");
                                            CloseProgressBar();
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    DisplayToastMsg("Ocorreu um erro durante o inicio de sessão, tente novamente");
                                    // If there a HTTP error then add a note to our repo list.
                                    Log.e("Volley", error.toString());
                                    CloseProgressBar();
                                }
                            });
                    arrReq.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    requestQueue.add(arrReq);


                }


            }
            catch (Exception ex)
            {
                Log.d("Error", ex.getMessage());
            }

            return null;
        }




    }
}
