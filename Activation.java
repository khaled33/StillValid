package com.stillvalid.asus.stillvalid;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.stillvalid.asus.stillvalid.Models.Config_URL;
import com.stillvalid.asus.stillvalid.Models.User;
import com.stillvalid.asus.stillvalid.Receive.NetworkStateChangeReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class Activation extends AppCompatActivity {
    Context context;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    EditText Code_Activation;
    String ID_User, Code, Token;
    ArrayList<User> List = new ArrayList<User>();
    BroadcastReceiver mNetworkReceiver;
    static ACProgressFlower dialoge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);
        Code_Activation = findViewById(R.id.Activation);
        LoginActivity.NB_Activity = 2;
        mNetworkReceiver = new NetworkStateChangeReceiver();
        registerNetworkBroadcastForNougat();
        context = this;
        prefs = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
        editor = prefs.edit();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(Activation.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                Token = instanceIdResult.getToken();
                Log.e("newToken", Token);
//                Toast.makeText(Activation.this, Token, Toast.LENGTH_SHORT).show();

            }
        });
        String Id = prefs.getString("ID", null);
        if (Id != null) {
            ID_User = Id;
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Config_URL.URL_ACTIVATION + ID_User, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        User user = new User();
                        user.setId(response.getInt("id"));
                        user.setEmail(response.getString("email"));
                        user.setPassword(response.getString("password"));
                        user.setCodeActivation(response.getString("code"));
                        user.setAdmin(response.getString("admin"));
                        user.setEtat(response.getString("etat"));
                        List.add(user);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        }
    }

    public void ValiderEmail(View view) {
        Code = Code_Activation.getText().toString().trim();

        if (valider()) {
            final ProgressDialog loading = ProgressDialog.show(context, "Traitement Des Données...", "S'il Vous Plaît, Attendez...", false, false);

            StringRequest request = new StringRequest(Request.Method.PUT, Config_URL.URL_ACTIVATION + ID_User, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject object = new JSONObject(response);

                        if (!response.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Votre compte est activé", Toast.LENGTH_SHORT).show();
                            editor.putString("ID", ID_User);
                            editor.putInt("Connexion", 1);
                            editor.apply();
                            EnvoieToken(Token, ID_User);
                            Intent intent = new Intent(getApplicationContext(), Home.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error.Response", "test");
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> hmap = new HashMap<String, String>();
                    hmap.put("email", List.get(0).getEmail());
                    hmap.put("password", List.get(0).getPassword());
                    hmap.put("admin", "0");
                    hmap.put("etat", "1");
                    hmap.put("code", List.get(0).getCodeActivation());
                    return hmap;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        }
    }

    private boolean valider() {
        boolean valide = true;
        if (Code.isEmpty()) {
            Code_Activation.setError(getString(R.string.champs_obligatoir));
            valide = false;
        }
        if (List.size() != 0) {
            if (!Code.isEmpty() && !Code.equals(List.get(0).getCodeActivation())) {
                Code_Activation.setError(getString(R.string.eror_code_activation));
                valide = false;
            }
        }
        return valide;
    }

    //Poster Token
    private void EnvoieToken(final String token, final String id_user) {

        StringRequest request = new StringRequest(Request.Method.POST, Config_URL.URL_INSERT_TOKEN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject object = new JSONObject(response);

                    if (!response.isEmpty()) {

                        Toast.makeText(getApplicationContext(), response + "", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error.Response", "test");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hmap = new HashMap<String, String>();
                hmap.put("id_user", id_user);
                hmap.put("token", token);
                return hmap;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    //Methode de test connexion
    public static void Alert_dialog(boolean value, Context context) {

        if (value) {
            context = null;
            dialoge.dismiss();
        } else {
            dialoge = new ACProgressFlower.Builder(context)
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .themeColor(Color.WHITE)
                    .text("Access Denied...").textColor(Color.WHITE)
                    .fadeColor(Color.TRANSPARENT).build();
            dialoge.show();
        }
    }

    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);

    }
}
