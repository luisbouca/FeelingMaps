package com.app.feelingmaps;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
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


public class Login extends AppCompatActivity implements View.OnClickListener{

    RelativeLayout rellay1, rellay2;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            rellay1.setVisibility(View.VISIBLE);
            rellay2.setVisibility(View.VISIBLE);
        }
    };


    private FrameLayout progressBarHolder;
    private static final int MY_SOCKET_TIMEOUT_MS = 20000;
    private RequestQueue requestQueue; // This is our requests queue to process our HTTP requests.

    private AlphaAnimation btnClickAnimation = new AlphaAnimation(1F, 0.8F);
    private AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
    private AlphaAnimation outAnimation =  new AlphaAnimation(1f, 0f);


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        rellay1 = (RelativeLayout) findViewById(R.id.rellay1);
        rellay2 = (RelativeLayout) findViewById(R.id.rellay2);

        handler.postDelayed(runnable, 2000); //2000 is the timeout for the splash

        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);

        //Setting event listener.
        findViewById(R.id.btnSubmit).setOnClickListener(this);
        findViewById(R.id.btnRegister).setOnClickListener(this);

        requestQueue = Volley.newRequestQueue(this); // This setups up a new request queue which we will need to make HTTP requests.
    }


    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.btnSubmit) {

            OpenProgressBar();
            new Authentication().execute();

        }
        else if (v.getId() == R.id.btnRegister)
        {
            startActivity(new Intent(this, Register.class));
        }
        v.startAnimation(btnClickAnimation);
    }


    private class Authentication extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try
            {

                String email = ((EditText) findViewById(R.id.txtEmail)).getText().toString();
                String password = ((EditText) findViewById(R.id.txtPassword)).getText().toString();

                if(email.isEmpty() || email == null)
                {
                    DisplayToastMsg("Atenção, e-mail em falta");
                    CloseProgressBar();
                }
                else if(password.isEmpty() || password == null)
                {
                    DisplayToastMsg("Atenção, password em falta");
                    CloseProgressBar();
                }
                else
                {

                    String ip = "10.0.2.2:85";
                    String url = "http://" + ip + "/api/Email/" + AESEncyption.encrypt(email.toLowerCase()) + "/Password/" + AESEncyption.encrypt(password);;

                    JsonObjectRequest arrReq = new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        CloseProgressBar();
                                        Boolean redirect = Integer.parseInt((response.getJSONArray("response")).getJSONObject(0).optString("authentication")) > 0 ? true:false;

                                        if(redirect)
                                            startActivity(new Intent(Login.this, MapsActivity.class));
                                        else
                                            DisplayToastMsg("Atenção, o email ou a password incorretos!");

                                    } catch (JSONException e) {
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

    private void DisplayToastMsg(final String message){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(),  message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void OpenProgressBar()
    {
        progressBarHolder.clearAnimation();
        progressBarHolder.animate().alpha(0.5f);
        inAnimation.setDuration(500);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);
    }

    private void CloseProgressBar()
    {
        outAnimation.setDuration(500);
        progressBarHolder.setAnimation(outAnimation);
        progressBarHolder.setVisibility(View.GONE);
        progressBarHolder.clearAnimation();
    }

}
