package com.stillvalid.asus.stillvalid;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.stillvalid.asus.stillvalid.Models.Config_URL;
import com.stillvalid.asus.stillvalid.Models.Contrats;
import com.stillvalid.asus.stillvalid.Models.MailBody;
import com.stillvalid.asus.stillvalid.Models.Produit;
import com.stillvalid.asus.stillvalid.Models.SendMail;
import com.stillvalid.asus.stillvalid.Models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.stillvalid.asus.stillvalid.MesProduit.C_list;
import static com.stillvalid.asus.stillvalid.MesProduit.P_list;

public class MotDePassOblie extends AppCompatActivity {
EditText MotPass,ConfMotPass;
String mail,motpass,ConfMotPas,Token,TokenPhone;
int indix=-1;
Context contex;
int ID, indice;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    ArrayList<User> List = new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mot_de_pass_oblie);
        MotPass=findViewById(R.id.MotPass);
        ConfMotPass=findViewById(R.id.ConfMotPass);
        contex=this;
        prefs = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
        editor = prefs.edit();
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MotDePassOblie.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                TokenPhone = instanceIdResult.getToken();
                Log.e("newToken", TokenPhone);
//                            Toast.makeText(LoginActivity.this, TokenPhone, Toast.LENGTH_SHORT).show();

            }
        });
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mail = bundle.getString("Email");
        }

    }

    public void MotPassOblie(View view) {
         motpass=MotPass.getText().toString();
        ConfMotPas=ConfMotPass.getText().toString();

        if (!motpass.equals("")){
            if (!ConfMotPas.equals("")){
                if (motpass.equals(ConfMotPas)) {
                    if (TrouverEmail(mail)!=-1) {
                    indix=TrouverEmail(mail);
                        final ProgressDialog loading = ProgressDialog.show(MotDePassOblie.this, "Traitement Des Données...", "S'il Vous Plaît, Attendez...", false, false);

                        StringRequest request = new StringRequest(Request.Method.PUT, Config_URL.URL_ACTIVATION+LoginActivity.List.get(indix).getId(), new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject object = new JSONObject(response);

                                if (!response.isEmpty()) {
                                    loading.dismiss();
                                    login();
//                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                                    LoginActivity.List.clear();
//                                    startActivity(intent);
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
                            hmap.put("password",motpass);
                            hmap.put("admin", "0");
                            hmap.put("etat", "1");
                            hmap.put("code", LoginActivity.List.get(indix).getCodeActivation());
                            return hmap;
                        }
                    };
                    RequestQueue queue = Volley.newRequestQueue(this);
                    queue.add(request);

                }
                }else {
                    MotPass.setError(getString(R.string.err_pass2));
                }

            }else {
                ConfMotPass.setError(getString(R.string.champs_obligatoir));
            }

        }else {
            MotPass.setError(getString(R.string.champs_obligatoir));
        }
    }
    public int TrouverEmail(String email) {
        int i = 0;
        for (User user : LoginActivity.List) {
            if (user.getEmail().equals(email)) {

                return i;
            }
            i++;
        }
        return -1;
    }

    public void login(){
        final ProgressDialog loadinge = ProgressDialog.show(this, "Traitement Des Données...", "S'il Vous Plaît, Attendez...", false, false);

        JsonArrayRequest reques = new JsonArrayRequest(Request.Method.GET, "http://13.80.41.22/WebServicePhp/User.php", null, new Response.Listener<JSONArray>() {
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

                    if (TrouverUser(mail, motpass)) {
                        if (List.get(indice).getEtat().equals("0")) {
                            Random r = new Random();
                            int valeur = 100000 + r.nextInt(999999 - 100000);
                            Token = String.valueOf(valeur);
                            StringRequest request = new StringRequest(Request.Method.PUT, Config_URL.URL_ACTIVATION + ID, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    if (!response.isEmpty()) {
                                        editor.putString("ID", String.valueOf(ID));
                                        editor.putString("Email", mail);
                                        editor.apply();

                                        SendMail sm = new SendMail(contex, mail, getString(R.string.code_activation),
                                                MailBody.getBody(Token));
                                        sm.execute();
                                        startActivity(new Intent(getApplicationContext(), Activation.class));
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
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
                                    hmap.put("email", List.get(indice).getEmail());
                                    hmap.put("password", List.get(indice).getPassword());
                                    hmap.put("admin", "0");
                                    hmap.put("etat", "0");
                                    hmap.put("codeActivation", Token);
                                    return hmap;
                                }
                            };
                            RequestQueue queue = Volley.newRequestQueue(contex);
                            queue.add(request);
                        } else {

                            editor.putString("ID", String.valueOf(ID));
                            editor.putString("Email", mail);
                            editor.putInt("Connexion", 1);
                            editor.apply();

                            if (!String.valueOf(ID).equals("")) {
                                final ProgressDialog loading = ProgressDialog.show(contex, "Traitement Des Données...", "S'il Vous Plaît, Attendez...", false, false);

                                UpdateToken(TokenPhone, String.valueOf(ID));

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
                                            JsonArrayRequest request2 = new JsonArrayRequest(Request.Method.GET, "http://13.80.41.22/WebServicePhp/Contrats.php/?id=" + ID, null, new Response.Listener<JSONArray>() {
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
                                        JsonArrayRequest request2 = new JsonArrayRequest(Request.Method.GET, "http://13.80.41.22/WebServicePhp/Contrats.php/?id=" + ID, null, new Response.Listener<JSONArray>() {
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
                                RequestQueue queue = Volley.newRequestQueue(contex);
                                queue.add(request);
                                startActivity(new Intent(getApplicationContext(), Home.class));


                            }


                        }
                    } else {
                        Toast.makeText(contex, R.string.EmailOuMotDePasseInvalide, Toast.LENGTH_LONG).show();
                    }
                    loadinge.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue queues = Volley.newRequestQueue(this);
        queues.add(reques);
    }

    public boolean TrouverUser(String email, String password) {
        int i = 0;
        for (User user : List) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                ID = user.getId();
                indice = i;
                return true;
            }
            i++;
        }
        return false;
    }
    //Update Token
    private void UpdateToken(final String token, final String id_user) {
        StringRequest request = new StringRequest(Request.Method.POST, Config_URL.URL_UPDATE_TOKEN, new Response.Listener<String>() {
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
}
