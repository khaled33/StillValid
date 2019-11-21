package com.stillvalid.asus.stillvalid;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.stillvalid.asus.stillvalid.Models.Config_URL;
import com.stillvalid.asus.stillvalid.Models.Marques;
import com.stillvalid.asus.stillvalid.Models.Produit;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.stillvalid.asus.stillvalid.MarqueProduit.List_Marques;
import static com.stillvalid.asus.stillvalid.MesProduit.C_list;
import static com.stillvalid.asus.stillvalid.MesProduit.P_list;

public class DetailProduit extends AppCompatActivity {

    ImageView editDeleteMenu;
    TextView Nom_Produit, Date, Garantie, Enseigne;
    CircleImageView Img_Prod;
    ImageView chekImage;

    int Id;
    String NbJour;
    String SAV;
    String marque;
    Bundle bundle;
    Context context;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_produit);
        context=this;
        editDeleteMenu = findViewById(R.id.menuItem);
        Nom_Produit = findViewById(R.id.txtnom);
        Enseigne = findViewById(R.id.enseigne);
        Garantie = findViewById(R.id.txtgarantie);
        Date = findViewById(R.id.txtDat);
        Img_Prod = findViewById(R.id.profile_image);
        chekImage = findViewById(R.id.imageView7);

        bundle = getIntent().getExtras();
        if (bundle != null) {
            Id = bundle.getInt("ID_Produit");
            try {
                Id = bundle.getInt("ID_Produit");
                NbJour = bundle.getString("NbJOUR");

            }catch (Exception e){
                e.getMessage();
            }

                    Nom_Produit.setText(P_list.get(Id).getNom());
                    Enseigne.setText(P_list.get(Id).getEnseigne());
                    Garantie.setText(P_list.get(Id).getDuree() + " Mois");
                    Date.setText(P_list.get(Id).getDateAchat());
                    SAV = P_list.get(Id).getSav();
                    marque = P_list.get(Id).getMarque();
                    Picasso.get()
                            .load(P_list.get(Id).getPhoto())
                            .resize(400, 500)
                            .into(Img_Prod);

                    if (NbJour != null && NbJour.isEmpty()) {
                        int testJour = Integer.parseInt(NbJour);
                        if (testJour <= 0) {
                            Img_Prod.setBorderColor(Color.RED);
                            chekImage.setImageResource(R.drawable.x);
                        } else {
                            Img_Prod.setBorderColor(Color.parseColor("#358c42"));
                            chekImage.setImageResource(R.drawable.check_produits);
                        }
                    }


        }
    }

    public void EditProduit(View view) {
        PopupMenu dropDownMenu = new PopupMenu(getApplicationContext(), editDeleteMenu);
        dropDownMenu.getMenuInflater().inflate(R.menu.drop_down_menu, dropDownMenu.getMenu());
        dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.dropdown_menu1:
                        Intent intent = new Intent(DetailProduit.this, ModifierProduit.class);
                        intent.putExtra("ID_Produit", Id);
                        startActivity(intent);


                        return true;
                    case R.id.dropdown_menu2:
                        AlertDialog.Builder alt = new AlertDialog.Builder(context);
                        alt.setTitle(" Supprimer Produit")
                                .setIcon(R.drawable.ic_delete)
                                .setMessage("\n" + "Vous été sûr de supprimer cette produit ?")
                                .setPositiveButton(R.string.oui, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {


                                        startActivity(new Intent(getApplicationContext(), MesProduit.class));

                                        StringRequest request = new StringRequest(Request.Method.DELETE, Config_URL.URL_PRODUIT + "/" + P_list.get(Id).getId(), new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }
                                        });
                                        P_list.remove(Id);
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

    public void getMenu(View view) {
        showPopupWindow(view);
    }

    void showPopupWindow(View view) {
        PopupMenu popup = new PopupMenu(DetailProduit.this, view);
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
                        startActivity(new Intent(DetailProduit.this, MesProduit.class));
                         
                        return true;
                    case R.id.dropdown_menu2:
                        startActivity(new Intent(DetailProduit.this, AjoutProduit.class));
                         
                        return true;
                    case R.id.dropdown_menu3:
                        startActivity(new Intent(DetailProduit.this, BoutiqueActivity.class));
                         
                        return true;
                    case R.id.dropdown_menu4:
                        SharedPreferences.Editor prefes = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE).edit();
                        prefes.putInt("Connexion", 0).apply();
                        P_list.clear();
                        C_list.clear();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finishAffinity();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    public void Mes_Produit(View view) {

         
        startActivity(new Intent(this, MesProduit.class));
    }

    public void acueil(View view) {
         
        startActivity(new Intent(this, Home.class));
    }

    public void annonce(View view) {
        Intent intent = new Intent(this, DeposerAnnonce.class);
        intent.putExtra("Path_Article", P_list.get(Id).getPhoto());
        startActivity(intent);
    }

    public void VoirFacture(View view) {
        Intent intent = new Intent(this, ConsulterFacture.class);
        intent.putExtra("ID_Produit", Id);
        startActivity(intent);
    }

    public void getSav(View view) {
        if (!SAV.isEmpty()) {
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(SAV));
//            startActivity(intent);

            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + SAV));
            startActivity(callIntent);
        } else {
            Toast.makeText(this, "Il n'a pas de SAV disponible!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
         
        startActivity(new Intent(this, MesProduit.class));
        super.onBackPressed();
    }

    public void support(View view) {

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "http://13.80.41.22/WebServicePhp/GetMarquesBySav.php?marque="+marque, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    if (response.length()>0) {
                       String support= String.valueOf(response.getJSONObject(0).get("support"));
//                        Toast.makeText(context, ""+test, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(support));
                        startActivity(intent);
                    }else {
                        Toast.makeText(getApplicationContext(), "Il n'a pas de Notice disponible!", Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}