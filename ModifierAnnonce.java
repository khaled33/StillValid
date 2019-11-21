package com.stillvalid.asus.stillvalid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.stillvalid.asus.stillvalid.Models.Boutique;
import com.stillvalid.asus.stillvalid.Models.Config_URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModifierAnnonce extends AppCompatActivity {

    ImageView image;
    Spinner spinner;
    EditText Titre, Description, Prix, Ville, Tel;
    ArrayList<Boutique> List = new ArrayList<Boutique>();
    ArrayList<String> List_Categorie = new ArrayList<String>();
    ArrayAdapter<String> Adapter;
    String Id, titre, description, prix, ville, catigorie, tel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifier_annonce);

        Titre = findViewById(R.id.titre_annonce);
        Description = findViewById(R.id.description);
        Prix = findViewById(R.id.prix_produit);
        Ville = findViewById(R.id.ville_produit);
        Tel = findViewById(R.id.tel_produit);
        image = findViewById(R.id.logo);
        spinner = findViewById(R.id.categorie);

        Bundle bundle = getIntent().getExtras();
        JsonArrayRequest reques = new JsonArrayRequest(Request.Method.GET, Config_URL.URL_CATEGORIES, null, new Response.Listener<JSONArray>() {
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
        RequestQueue queu = Volley.newRequestQueue(this);
        queu.add(reques);

        if (bundle != null) {
            Id = bundle.getString("ID_Annonce");
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "http://13.80.41.22/WebServicePhp/Annonce.php/?id="+Id, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {

                        Boutique boutique = new Boutique();
                        boutique.setId(response.getJSONObject(0).getInt("id"));
                        boutique.setImage(response.getJSONObject(0).getString("photoProduit"));
                        boutique.setDescription(response.getJSONObject(0).getString("description"));
                        boutique.setPrix(response.getJSONObject(0).getString("prix"));
                        boutique.setVille(response.getJSONObject(0).getString("ville"));
                        boutique.setCategorie(response.getJSONObject(0).getString("categorie"));
                        boutique.setTel(response.getJSONObject(0).getString("numtel"));
                        boutique.setTitre(response.getJSONObject(0).getString("titre"));
                        boutique.setUser_id(response.getJSONObject(0).getString("user_id"));
                        boutique.setEmail(response.getJSONObject(0).getString("email"));
                        List.add(boutique);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Titre.setText(List.get(0).getTitre());
                    Description.setText(List.get(0).getDescription());
                    Tel.setText(List.get(0).getTel());
                    Ville.setText(List.get(0).getVille());
                    Prix.setText(List.get(0).getPrix());
                    spinner.setSelection(TrouverIndice(List.get(0).getCategorie()));
                    Picasso.get()
                            .load(List.get(0).getImage())
                            .resize(400, 500)
                            .into(image);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        }

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

    public void valider(View view) {
        titre = Titre.getText().toString().trim();
        description = Description.getText().toString().trim();
        prix = Prix.getText().toString().trim();
        ville = Ville.getText().toString().trim();
        tel = Tel.getText().toString().trim();
        if (Valider()) {
            final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
            StringRequest request = new StringRequest(Request.Method.PUT, Config_URL.URL_ANNONCES + "/" + Id, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject object = new JSONObject(response);
                        loading.dismiss();
                        if (!response.isEmpty()) {
                            Intent intent = new Intent(getApplicationContext(), BoutiqueActivity.class);
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
                    loading.dismiss();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<String, String>();

                    params.put("description", description);
                    params.put("prix", prix);
                    params.put("titre", titre);
                    params.put("ville", ville);
                    params.put("categorie", catigorie);
                    params.put("numtel", tel);
                    return params;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
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

    public int TrouverIndice(String item) {
        int i = 0;
        for (String item_cat : List_Categorie) {
            if (item_cat.equals(item)) {
                return i;
            }
            i++;
        }
        return 0;
    }
}
