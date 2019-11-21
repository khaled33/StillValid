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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.stillvalid.asus.stillvalid.Models.Config_URL;
import com.stillvalid.asus.stillvalid.Models.MailBody;
import com.stillvalid.asus.stillvalid.Models.SendMail;
import com.stillvalid.asus.stillvalid.Models.User;
import com.stillvalid.asus.stillvalid.Receive.NetworkStateChangeReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class InscriptionActivity extends AppCompatActivity {

    ArrayList<User> List = new ArrayList<User>();
    EditText Email, Password, Password_Confirme;
    String mail, password, conf_password, Token;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    JSONObject object;
    Context context;
    BroadcastReceiver mNetworkReceiver;
    static ACProgressFlower dialoge;
    Button valider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        LoginActivity.NB_Activity = 1;
        mNetworkReceiver = new NetworkStateChangeReceiver();
        registerNetworkBroadcastForNougat();
        valider=findViewById(R.id.button);
        prefs = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
        editor = prefs.edit();
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.pass);
        Password_Confirme = findViewById(R.id.pass2);
        context = this;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Config_URL.URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    User user;
                    for (int i = 0; i < response.length(); i++) {
                        user = new User();
                        user.setId(response.getJSONObject(i).getInt("id"));
                        user.setEmail(response.getJSONObject(i).getString("email"));
                        user.setPassword(response.getJSONObject(i).getString("password"));
                        user.setCodeActivation(response.getJSONObject(i).getString("code"));
                        user.setAdmin(response.getJSONObject(i).getString("admin"));
                        user.setEtat(response.getJSONObject(i).getString("etat"));
                        List.add(user);
                    }
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

    public void inscrire(View view) {
        valider.setClickable(false);
        mail = Email.getText().toString().trim();
        password = Password.getText().toString().trim();
        conf_password = Password_Confirme.getText().toString().trim();

        if (valider()) {
            if (TrouverEmail(mail)) {
                Random r = new Random();
                int valeur = 100000 + r.nextInt(999999 - 100000);
                Token = String.valueOf(valeur);
                final ProgressDialog loading = ProgressDialog.show(context, "Traitement Des Données...", "S'il Vous Plaît, Attendez...", false, false);

                StringRequest request = new StringRequest(Request.Method.POST, Config_URL.URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            object = new JSONObject(response);

                            if (!response.isEmpty()) {

                                //Toast.makeText(getApplicationContext(), getString(R.string.InsertionTerminer), Toast.LENGTH_SHORT).show();
                                editor.putString("ID", String.valueOf(object.getInt("id")));
                                editor.apply();
                                SendMail sm = new SendMail(context, mail, getString(R.string.code_activation),
                                        MailBody.getBody(Token));
                                sm.execute();
                                Intent intent = new Intent(getApplicationContext(), Activation.class);
                                startActivity(intent);
//
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
                        hmap.put("email", mail);
                        hmap.put("password", password);
                        hmap.put("admin", "0");
                        hmap.put("etat", "0");
                        hmap.put("code", Token);
                        return hmap;
                    }
                };
                int socketTimeout = 30000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                request.setRetryPolicy(policy);
                RequestQueue queue = Volley.newRequestQueue(this);
                queue.add(request);
            } else {
                Email.setError(getString(R.string.chekmail));
                valider.setClickable(true);
            }
        }else  valider.setClickable(true);
    }

    private boolean valider() {
        boolean valide = true;
        if (mail.isEmpty()) {
            Email.setError(getString(R.string.champs_obligatoir));
            valide = false;
        }
        if (!mail.isEmpty() && (!android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches())) {
            Email.setError(getString(R.string.email_invalide));
            valide = false;
        }
        if (password.isEmpty()) {
            Password.setError(getString(R.string.champs_obligatoir));
            valide = false;
        }
        if (conf_password.isEmpty()) {
            Password_Confirme.setError(getString(R.string.champs_obligatoir));
            valide = false;
        }
        if (!conf_password.isEmpty() && (!conf_password.contentEquals(password))) {
            Password_Confirme.setError(getString(R.string.err_pass2));
            valide = false;
        }
        return valide;
    }

    public boolean TrouverEmail(String item) {
        for (User user : List) {
            if (user.getEmail().equals(item)) {
                return false;
            }
        }
        return true;
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

}