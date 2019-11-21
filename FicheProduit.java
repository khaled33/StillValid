package com.stillvalid.asus.stillvalid;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

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

import static javax.mail.Message.RecipientType.TO;

public class FicheProduit extends AppCompatActivity {

    SharedPreferences prefs;
    String Id, Id_user;
    ArrayList<Boutique> List = new ArrayList<Boutique>();
    TextView Titre, Description, Prix, Ville, Tel, Email;
    ImageView imageView, editMenu;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fiche_produit);
        context = this;
        prefs = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
        Bundle bundle = getIntent().getExtras();
        Id_user = prefs.getString("ID", null);

        Titre = findViewById(R.id.nom_produit);
        Description = findViewById(R.id.enseigne);
        Prix = findViewById(R.id.prix);
        Ville = findViewById(R.id.ville);
        Email = findViewById(R.id.email);
        Tel = findViewById(R.id.phone_number);
        imageView = findViewById(R.id.image_produit);
        editMenu = findViewById(R.id.imageView20);

        if (bundle != null) {
            Id = bundle.getString("ID_Annonce");
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "http://13.80.41.22/WebServicePhp/Annonce.php/?id=" + Id, null, new Response.Listener<JSONArray>() {
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
                    Email.setText(List.get(0).getEmail());
                    Ville.setText(List.get(0).getVille());
                    Prix.setText(List.get(0).getPrix());
                    if (List.get(0).getUser_id().equals(Id_user)) {
                        editMenu.setVisibility(View.VISIBLE);
                    } else {
                        editMenu.setVisibility(View.INVISIBLE);
                    }
                    Picasso.get()
                            .load(List.get(0).getImage())
                            .resize(400, 500)
                            .into(imageView);
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

    public void Contacter(View view) {
        if (!Tel.getText().toString().isEmpty()) {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + Tel.getText().toString()));
            startActivity(callIntent);
        }
    }

    public void SendMail(View view) {
        if (!Email.getText().toString().isEmpty()) {

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("message/rfc822");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, Email.getText().toString());
            emailIntent.putExtra(Intent.EXTRA_CC, "");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
            emailIntent.putExtra(Intent.EXTRA_TEXT,"");
            startActivity(emailIntent);
        }
    }

    public void retourBoutique(View view) {
        startActivity(new Intent(this, BoutiqueActivity.class));
    }

    public void Edite(View view) {
        PopupMenu dropDownMenu = new PopupMenu(getApplicationContext(), editMenu);
        dropDownMenu.getMenuInflater().inflate(R.menu.drop_down_menu, dropDownMenu.getMenu());
        dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.dropdown_menu1:
                        Intent intent = new Intent(getApplicationContext(), ModifierAnnonce.class);
                        intent.putExtra("Path_Article", List.get(0).getImage());
                        intent.putExtra("ID_Annonce", Id);
                        startActivity(intent);
                        return true;
                    case R.id.dropdown_menu2:
                        AlertDialog.Builder alt = new AlertDialog.Builder(context);
                        alt.setTitle(" Supprimer Annonce")
                                .setIcon(R.drawable.ic_delete)
                                .setMessage("\n" + "Vous été sûr de supprimer cette annonce ?")
                                .setPositiveButton(R.string.oui, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        final ProgressDialog loading = ProgressDialog.show(FicheProduit.this, "Traitement Des Données...", "S'il Vous Plaît, Attendez...", false, false);

                                        StringRequest request = new StringRequest(Request.Method.DELETE,
                                                Config_URL.URL_ANNONCES + "/" + Id, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                startActivity(new Intent(getApplicationContext(), BoutiqueActivity.class));
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }
                                        });
                                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                        queue.add(request);
                                    }
                                }).setNegativeButton(R.string.non, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        dropDownMenu.show();
    }


}