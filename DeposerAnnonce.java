package com.stillvalid.asus.stillvalid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.stillvalid.asus.stillvalid.Models.Boutique;
import com.stillvalid.asus.stillvalid.Models.Config_URL;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.stillvalid.asus.stillvalid.MesProduit.C_list;
import static com.stillvalid.asus.stillvalid.MesProduit.P_list;

public class DeposerAnnonce extends AppCompatActivity {

    ArrayList<String> List_Categorie = new ArrayList<String>();
    ArrayAdapter<String> Adapter;
    SharedPreferences prefs;
    String ID, Mail_user, Path_Img_Prod, titre, description, prix, ville, catigorie, tel, Email;
    Spinner spinner;
    CircleImageView img_Article;
    EditText Titre, Description, Prix, Ville, Tel;
    TextView Mail;
    CheckBox chekMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposer_annonce);
        prefs = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
        ID = prefs.getString("ID", null);
        Mail_user = prefs.getString("Email", null);

        Titre = findViewById(R.id.txtTitreAnnonce);
        Description = findViewById(R.id.txtDescAnnonce);
        Prix = findViewById(R.id.txtPrix);
        Ville = findViewById(R.id.txtVille);
        spinner = findViewById(R.id.CmbCategorie);
        img_Article = findViewById(R.id.imgArticle);
        Mail = findViewById(R.id.txtMail);
        Tel = findViewById(R.id.txtTel);
        chekMail = findViewById(R.id.checkBox);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Path_Img_Prod = bundle.getString("Path_Article");
            Picasso.get()
                    .load(Path_Img_Prod)
                    .resize(400, 500)
                    .into(img_Article);
        }
        Mail.setText(Mail_user);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Config_URL.URL_CATEGORIES, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        List_Categorie.add(response.getJSONObject(i).getString("libelle"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, List_Categorie);
                spinner.setAdapter(Adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                catigorie = Adapter.getItem(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void Deposer(View view) {
        titre = Titre.getText().toString().trim();
        description = Description.getText().toString().trim();
        prix = Prix.getText().toString().trim();
        ville = Ville.getText().toString().trim();
        tel = Tel.getText().toString().trim();
        Email = "";
        if (chekMail.isChecked()) {
            Email = Mail.getText().toString().trim();
        }
        if (Valider()) {
            final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config_URL.URL_ANNONCES,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {

                            loading.dismiss();
                            if (!s.isEmpty()) {
                                startActivity(new Intent(getApplicationContext(), BoutiqueActivity.class));
                            } else {
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            loading.dismiss();
                            Toast.makeText(getApplicationContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();

                    params.put("user_id", ID);
                    params.put("description", description);
                    params.put("prix", prix);
                    params.put("titre", titre);
                    params.put("ville", ville);
                    params.put("photoproduit", Path_Img_Prod);
                    params.put("categorie", catigorie);
                    params.put("email", Email);
                    params.put("numtel", tel);
                    params.put("contact", "");

                    return params;
                }
            };
            //Creating a Request Queue
//            int socketTimeout = 10000;
//            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//            stringRequest.setRetryPolicy(policy);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
    }

    private boolean Valider() {
        boolean valide = true;
        if (titre.isEmpty()) {
            Titre.setError(getString(R.string.champs_obligatoir));
            valide = false;
        }
        if (description.isEmpty()) {
            Description.setError(getString(R.string.champs_obligatoir));
            valide = false;
        }
        if (prix.isEmpty()) {
            Prix.setError(getString(R.string.champs_obligatoir));
            valide = false;
        }
        if (ville.isEmpty()) {
            Ville.setError(getString(R.string.champs_obligatoir));
            valide = false;
        }
        return valide;
    }

    public void getMenu(View view) {
        showPopupWindow(view);
    }

    void showPopupWindow(View view) {
        PopupMenu popup = new PopupMenu(DeposerAnnonce.this, view);
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        popup.getMenuInflater().inflate(R.menu.menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.dropdown_menu1:
                        startActivity(new Intent(DeposerAnnonce.this, MesProduit.class));
                        return true;
                    case R.id.dropdown_menu2:
                        startActivity(new Intent(DeposerAnnonce.this, AjoutProduit.class));
                        return true;
                    case R.id.dropdown_menu3:
                        startActivity(new Intent(getApplicationContext(), BoutiqueActivity.class));
                        return true;
                    case R.id.dropdown_menu4:
                        SharedPreferences.Editor prefes = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE).edit();
                        prefes.putInt("Connexion", 0).apply();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finishAffinity();
                        P_list.clear();
                        C_list.clear();
                        startActivity(intent);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    public void acueil(View view) {
        startActivity(new Intent(this, Home.class));
    }
}
