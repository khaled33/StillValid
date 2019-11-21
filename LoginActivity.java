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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.stillvalid.asus.stillvalid.Models.Config_URL;
import com.stillvalid.asus.stillvalid.Models.Contrats;
import com.stillvalid.asus.stillvalid.Models.MailBody;
import com.stillvalid.asus.stillvalid.Models.Produit;
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

import static com.stillvalid.asus.stillvalid.MesProduit.C_list;
import static com.stillvalid.asus.stillvalid.MesProduit.P_list;

public class LoginActivity extends AppCompatActivity {

    static ArrayList<User> List = new ArrayList<User>();
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    EditText Email, Password;
    String mail, password, Token, TokenPhone;
    int ID, indice;
    Context context;
    BroadcastReceiver mNetworkReceiver;
    static ACProgressFlower dialoge;
    public static int NB_Activity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(LoginActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                TokenPhone = instanceIdResult.getToken();
                Log.e("newToken", TokenPhone);
//                            Toast.makeText(LoginActivity.this, TokenPhone, Toast.LENGTH_SHORT).show();

            }
        });
        List.clear();

        mNetworkReceiver = new NetworkStateChangeReceiver();
        registerNetworkBroadcastForNougat();

        context = this;
        prefs = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
        editor = prefs.edit();
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);

    }

    public void inscrire(View view) {
        startActivity(new Intent(this, InscriptionActivity.class));
    }

    public void valider(View view) {
        mail = Email.getText().toString().trim();
        password = Password.getText().toString().trim();
        //startActivity(new Intent(this, Home.class));
        if (Valider()) {
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

                        if (TrouverUser(mail, password)) {
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

                                            SendMail sm = new SendMail(context, mail, getString(R.string.code_activation),
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
                                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                                queue.add(request);
                            } else {

                                editor.putString("ID", String.valueOf(ID));
                                editor.putString("Email", mail);
                                editor.putInt("Connexion", 1);
                                editor.apply();

                                if (!String.valueOf(ID).equals("")) {
                                    final ProgressDialog loading = ProgressDialog.show(LoginActivity.this, "Traitement Des Données...", "S'il Vous Plaît, Attendez...", false, false);

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
                                    RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                                    queue.add(request);
                                    startActivity(new Intent(getApplicationContext(), Home.class));


                                }


                            }
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.EmailOuMotDePasseInvalide, Toast.LENGTH_LONG).show();
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

//            if (TrouverUser(mail, password)) {
//                if (List.get(indice).getEtat().equals("0")) {
//                    Random r = new Random();
//                    int valeur = 100000 + r.nextInt(999999 - 100000);
//                    Token = String.valueOf(valeur);
//                    StringRequest request = new StringRequest(Request.Method.PUT, Config_URL.URL_ACTIVATION + ID, new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//
//                            if (!response.isEmpty()) {
//                                editor.putString("ID", String.valueOf(ID));
//                                editor.putString("Email", mail);
//                                editor.apply();
//
//                                SendMail sm = new SendMail(context, mail, getString(R.string.code_activation),
//                                        MailBody.getBody(Token));
//                                sm.execute();
//                                startActivity(new Intent(getApplicationContext(), Activation.class));
//                            } else {
//                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            Log.d("Error.Response", "test");
//                        }
//                    }) {
//                        @Override
//                        protected Map<String, String> getParams() throws AuthFailureError {
//                            HashMap<String, String> hmap = new HashMap<String, String>();
//                            hmap.put("email", List.get(indice).getEmail());
//                            hmap.put("password", List.get(indice).getPassword());
//                            hmap.put("admin", "0");
//                            hmap.put("etat", "0");
//                            hmap.put("codeActivation", Token);
//                            return hmap;
//                        }
//                    };
//                    RequestQueue queue = Volley.newRequestQueue(this);
//                    queue.add(request);
//                } else {
//
//                    editor.putString("ID", String.valueOf(ID));
//                    editor.putString("Email", mail);
//                    editor.putInt("Connexion", 1);
//                    editor.apply();
//
//                    if (!String.valueOf(ID).equals("")) {
//                        final ProgressDialog loading = ProgressDialog.show(this, "Traitement Des Données...", "S'il Vous Plaît, Attendez...", false, false);
//
//                        UpdateToken(TokenPhone, String.valueOf(ID));
//
//                        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "http://13.80.41.22/WebServicePhp/Articles.php/?id=" + ID, null, new Response.Listener<JSONArray>() {
//                            @Override
//                            public void onResponse(JSONArray response) {
//                                try {
//                                    Produit produit;
//                                    for (int i = 0; i < response.length(); i++) {
//                                        produit = new Produit();
//                                        produit.setId(response.getJSONObject(i).getInt("id"));
//                                        produit.setEnseigne(response.getJSONObject(i).getString("enseigne"));
//                                        produit.setNom(response.getJSONObject(i).getString("nom"));
//                                        produit.setPhoto(response.getJSONObject(i).getString("photo"));
//                                        produit.setFacture(response.getJSONObject(i).getString("facture"));
//                                        produit.setDuree(response.getJSONObject(i).getString("garantie"));
//                                        produit.setMarque(response.getJSONObject(i).getString("marque"));
//                                        produit.setDateAchat(response.getJSONObject(i).getString("dAchat"));
//                                        produit.setDateFin(response.getJSONObject(i).getString("dFin"));
//                                        produit.setUser_id(response.getJSONObject(i).getString("user_id"));
//                                        produit.setSav(response.getJSONObject(i).getString("sav"));
//                                        P_list.add(produit);
//
//
//                                    }
//                                    JsonArrayRequest request2 = new JsonArrayRequest(Request.Method.GET, "http://13.80.41.22/WebServicePhp/Contrats.php/?id=" + ID, null, new Response.Listener<JSONArray>() {
//                                        @Override
//                                        public void onResponse(JSONArray response) {
//                                            if (response.length() != 0) {
//                                                try {
//                                                    Contrats contrat;
//                                                    for (int i = 0; i < response.length(); i++) {
//                                                        contrat = new Contrats();
//                                                        contrat.setId(response.getJSONObject(i).getInt("id"));
//                                                        contrat.setType(response.getJSONObject(i).getString("type"));
//                                                        contrat.setDateEcheance(response.getJSONObject(i).getString("dEcheance"));
//                                                        contrat.setPhoto(response.getJSONObject(i).getString("photo"));
//                                                        C_list.add(contrat);
//
//                                                    }
//
//                                                } catch (JSONException e) {
//                                                    e.printStackTrace();
//                                                }
//                                            }
//                                        }
//                                    }, new Response.ErrorListener() {
//                                        @Override
//                                        public void onErrorResponse(VolleyError error) {
////                        startActivity(new Intent(getApplicationContext(), Home.class));
//
//
//                                        }
//                                    });
//
//                                    int socketTimeout = 5000;
//                                    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//                                    request2.setRetryPolicy(policy);
//                                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
//                                    queue.add(request2);
//
//
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }, new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                JsonArrayRequest request2 = new JsonArrayRequest(Request.Method.GET, "http://13.80.41.22/WebServicePhp/Contrats.php/?id=" + ID, null, new Response.Listener<JSONArray>() {
//                                    @Override
//                                    public void onResponse(JSONArray response) {
//                                        if (response.length() != 0) {
//                                            try {
//                                                Contrats contrat;
//                                                for (int i = 0; i < response.length(); i++) {
//                                                    contrat = new Contrats();
//                                                    contrat.setId(response.getJSONObject(i).getInt("id"));
//                                                    contrat.setType(response.getJSONObject(i).getString("type"));
//                                                    contrat.setDateEcheance(response.getJSONObject(i).getString("decheance"));
//                                                    contrat.setPhoto(response.getJSONObject(i).getString("photo"));
//                                                    C_list.add(contrat);
//
//
//                                                }
//
//                                            } catch (JSONException e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                    }
//                                }, new Response.ErrorListener() {
//                                    @Override
//                                    public void onErrorResponse(VolleyError error) {
//
//
//                                    }
//                                });
//
//                                int socketTimeout = 5000;
//                                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//                                request2.setRetryPolicy(policy);
//                                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
//                                queue.add(request2);
//
//                            }
//                        });
//                        int socketTimeout = 5000;
//                        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//                        request.setRetryPolicy(policy);
//                        RequestQueue queue = Volley.newRequestQueue(this);
//                        queue.add(request);
//                        startActivity(new Intent(getApplicationContext(), Home.class));
//
//
//                    }
//
//
//                }
//            } else {
//                Toast.makeText(this, R.string.EmailOuMotDePasseInvalide, Toast.LENGTH_LONG).show();
//            }
        }

    }

    private boolean Valider() {
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
        return valide;
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


    public boolean TrouverEmail(String email) {
        int i = 0;
        for (User user : List) {
            if (user.getEmail().equals(email)) {
                return true;
            }
            i++;
        }
        return false;
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

    public void passOblier(View view) {
        mail = Email.getText().toString().trim();
        if (!mail.equals("")){
        final ProgressDialog loading = ProgressDialog.show(context, "Traitement Des Données...", "S'il Vous Plaît, Attendez...", false, false);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "http://13.80.41.22/WebServicePhp/User.php", null, new Response.Listener<JSONArray>() {
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
                    if (TrouverEmail(mail)) {
                        Random r = new Random();
                        int valeur = 100000 + r.nextInt(999999 - 100000);
                        SendMail sm = new SendMail(context, mail, getString(R.string.code_activation),
                                MailBody.getBody1(String.valueOf(valeur)));
                        sm.execute();

                        Intent intent = new Intent(getApplicationContext(), ConfirmationMotPassOblierParMail.class);
                        intent.putExtra("Email", mail);
                        intent.putExtra("MotPassTemporer", String.valueOf(valeur));
                        startActivity(intent);
                        loading.dismiss();
                    } else if (!mail.equals("")) {
                        loading.dismiss();
                        Toast.makeText(context, "Cette adresse mail n'existe pas", Toast.LENGTH_SHORT).show();
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
        }else {
            Email.setError(getString(R.string.champs_obligatoir));
        }

    }
}
