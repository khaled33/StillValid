package com.stillvalid.asus.stillvalid;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.stillvalid.asus.stillvalid.Adapter.ProduitAdapter;
import com.stillvalid.asus.stillvalid.Models.Config_URL;
import com.stillvalid.asus.stillvalid.Models.Contrats;
import com.stillvalid.asus.stillvalid.Models.Produit;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Calendar;

import static com.stillvalid.asus.stillvalid.MesProduit.C_list;
import static com.stillvalid.asus.stillvalid.MesProduit.P_list;

public class Splash_Screen extends AppCompatActivity {
    public static int SPLASH_TIME_OUT = 4000;
    SharedPreferences prefs;
    int etat;
    String ID;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash__screen);
        Animation animFadeIn;
        prefs = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
        ID = prefs.getString("ID", null);
        ImageView logo = (ImageView) findViewById(R.id.logo);
        prefs = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
        P_list.clear();
        C_list.clear();
        animFadeIn = AnimationUtils.loadAnimation(this, R.anim.translate);
        logo.setAnimation(animFadeIn);

        etat = prefs.getInt("Connexion", 0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (etat == 1) {
                    Intent i = new Intent(Splash_Screen.this, Home.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(Splash_Screen.this, LoginActivity.class);
                    startActivity(i);
                }

                finish();
            }
        }, SPLASH_TIME_OUT);

if(ID!=null && etat == 1){
    JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "http://13.80.41.22/WebServicePhp/Articles.php/?id=" + ID, null, new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
            try {
                Produit produit;
                for (int i = 0; i < response.length(); i++) {
                    produit = new Produit();
                    produit.setId(response.getJSONObject(i).getInt("id"));
                    produit.setEnseigne(response.getJSONObject(i).getString("enseigne"));
                    produit.setNom(response.getJSONObject(i).getString("nom"));
                    produit.setPhoto(response.getJSONObject(i).getString("photo"));
                    produit.setFacture(response.getJSONObject(i).getString("facture"));
                    produit.setDuree(response.getJSONObject(i).getString("garantie"));
                    produit.setMarque(response.getJSONObject(i).getString("marque"));
                    produit.setDateAchat(response.getJSONObject(i).getString("dAchat"));
                    produit.setDateFin(response.getJSONObject(i).getString("dFin"));
                    produit.setUser_id(response.getJSONObject(i).getString("user_id"));
                    produit.setSav(response.getJSONObject(i).getString("sav"));
                    P_list.add(produit);



                }
                JsonArrayRequest request2 = new JsonArrayRequest(Request.Method.GET, "http://13.80.41.22/WebServicePhp/Contrats.php/?id="+ ID, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length() != 0) {
                            try {
                                Contrats contrat;
                                for (int i = 0; i < response.length(); i++) {
                                    contrat = new Contrats();
                                    contrat.setId(response.getJSONObject(i).getInt("id"));
                                    contrat.setType(response.getJSONObject(i).getString("type"));
                                    contrat.setDateEcheance(response.getJSONObject(i).getString("dEcheance"));
                                    contrat.setPhoto(response.getJSONObject(i).getString("photo"));
                                    C_list.add(contrat);

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        startActivity(new Intent(getApplicationContext(), Home.class));



                    }
                });

                int socketTimeout = 5000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                request2.setRetryPolicy(policy);
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                queue.add(request2);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            JsonArrayRequest request2 = new JsonArrayRequest(Request.Method.GET, "http://13.80.41.22/WebServicePhp/Contrats.php/?id="+ ID, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    if (response.length() != 0) {
                        try {
                            Contrats contrat;
                            for (int i = 0; i < response.length(); i++) {
                                contrat = new Contrats();
                                contrat.setId(response.getJSONObject(i).getInt("id"));
                                contrat.setType(response.getJSONObject(i).getString("type"));
                                contrat.setDateEcheance(response.getJSONObject(i).getString("decheance"));
                                contrat.setPhoto(response.getJSONObject(i).getString("photo"));
                                C_list.add(contrat);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {



                }
            });

            int socketTimeout = 5000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            request2.setRetryPolicy(policy);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(request2);

        }
    });
    int socketTimeout = 5000;
    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    request.setRetryPolicy(policy);
    RequestQueue queue = Volley.newRequestQueue(this);
    queue.add(request);




}
    }
}
