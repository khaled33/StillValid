package com.stillvalid.asus.stillvalid;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
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
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.stillvalid.asus.stillvalid.Models.Config_URL;
import com.stillvalid.asus.stillvalid.Models.Contrats;
import com.stillvalid.asus.stillvalid.Models.Papiers;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static com.stillvalid.asus.stillvalid.MesProduit.C_list;
import static com.stillvalid.asus.stillvalid.MesProduit.P_list;

public class DetailContrat extends AppCompatActivity {

    ImageView editDeleteMenu;
    TextView Types_Contrat, Date_Contrat;
    RoundedImageView Img_Contrat;
    ArrayList<Contrats> List = new ArrayList<Contrats>();
    int Id;
    String NbJour;
    static int PAGE;
    Context context;
    Contrats contrats = new Contrats();
    public static ArrayList<String> List_Type = new ArrayList<>();
    public static ArrayList<Papiers> List_Papiers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_contrat);
        context = this;
        editDeleteMenu = findViewById(R.id.modif_supp_btn);
        Img_Contrat = findViewById(R.id.profile_image);
        Types_Contrat = findViewById(R.id.type_contrat);
        Date_Contrat = findViewById(R.id.date_contrat);
        Bundle bundle = getIntent().getExtras();


        if (bundle != null) {
            Id = bundle.getInt("ID_Contrat");
            NbJour = bundle.getString("NbJOUR");


                        Contrats contrats = new Contrats();
                        contrats.setId(C_list.get(Id).getId());
                        contrats.setType(C_list.get(Id).getType());
                        contrats.setDateEcheance(C_list.get(Id).getDateEcheance());
                        contrats.setPhoto(C_list.get(Id).getPhoto());
                        contrats.setId_user(C_list.get(Id).getId_user());
                        List.add(contrats);


                    Types_Contrat.setText(contrats.getType());
                    Date_Contrat.setText(contrats.getDateEcheance());
                    Picasso.get()
                            .load(contrats.getPhoto())
                            .resize(400, 500)
                            .into(Img_Contrat);


                    if (NbJour != null) {
                        int testJour = Integer.parseInt(NbJour);
                        if (testJour <= 0) {
                            Img_Contrat.setBorderColor(Color.RED);
                        } else {
                            Img_Contrat.setBorderColor(Color.parseColor("#358c42"));
                        }
                    }
                }



    }

    public void EditContrat(View view) {
        PopupMenu dropDownMenu = new PopupMenu(getApplicationContext(), editDeleteMenu);
        dropDownMenu.getMenuInflater().inflate(R.menu.drop_down_menu, dropDownMenu.getMenu());
        dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.dropdown_menu1:
                        final ProgressDialog loading = ProgressDialog.show(DetailContrat.this, "Traitement Des Données...", "S'il Vous Plaît, Attendez...", false, false);
                        PAGE = 1;
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Config_URL.URL_TYPES, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            List_Type.add(response.getJSONObject(i).getString("libelle"));
                        }
                        if (List_Papiers.size()==0){

                            JsonArrayRequest reques = new JsonArrayRequest(Request.Method.GET, "http://13.80.41.22/WebServicePhp/Papiers.php/?id=" +  C_list.get(Id).getId(), null, new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    try {
                                        Papiers papiers;
                                        if(response.length()!=0){
                                        for (int i = 0; i < response.length(); i++) {
                                            papiers = new Papiers();
                                            papiers.setId(response.getJSONObject(i).getInt("id"));
                                            papiers.setPath(response.getJSONObject(i).getString("path"));
                                            papiers.setId_contrat(response.getJSONObject(i).getString("contrat_id"));
                                            List_Papiers.add(papiers);

                                            Intent intent = new Intent(getApplicationContext(), ModifierContrat.class);
                                            intent.putExtra("ID_Contrat", Id);
                                            intent.putExtra("NbJOUR", NbJour);
                                            startActivity(intent);
                                        }

                                        }else {
                                            Intent intent = new Intent(getApplicationContext(), ModifierContrat.class);
                                            intent.putExtra("ID_Contrat", Id);
                                            intent.putExtra("NbJOUR", NbJour);
                                            startActivity(intent);
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
                            int socketTimeout = 50000;
                            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                            reques.setRetryPolicy(policy);
                            RequestQueue queu = Volley.newRequestQueue(getApplicationContext());
                            queu.add(reques);
                        }else {
                            Intent intent = new Intent(getApplicationContext(), ModifierContrat.class);
                            intent.putExtra("ID_Contrat", Id);
                            intent.putExtra("NbJOUR", NbJour);
                            startActivity(intent);
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
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(request);


                        return true;
                    case R.id.dropdown_menu2:
                        AlertDialog.Builder alt = new AlertDialog.Builder(context);
                        alt.setTitle(" Supprimer Contrat")
                                .setIcon(R.drawable.ic_delete)
                                .setMessage("\n" + "Vous été sûr de supprimer cette contrat ?")
                                .setPositiveButton(R.string.oui, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        List_Papiers.clear();

                                        startActivity(new Intent(getApplicationContext(), MesProduit.class));
                                        StringRequest request = new StringRequest(Request.Method.DELETE,
                                                Config_URL.URL_CONTRAT + "/" + C_list.get(Id).getId(), new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {


                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }
                                        });
                                        C_list.remove(Id);
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
        PopupMenu popup = new PopupMenu(DetailContrat.this, view);
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
                //the necessary intent for each MenuItem
                switch (item.getItemId()) {
                    case R.id.dropdown_menu1:
                        List_Papiers.clear();
                        startActivity(new Intent(DetailContrat.this, MesProduit.class));
                        return true;
                    case R.id.dropdown_menu2:
                        List_Papiers.clear();
                        startActivity(new Intent(DetailContrat.this, AjoutProduit.class));
                        return true;
                    case R.id.dropdown_menu3:
                        List_Papiers.clear();
                        startActivity(new Intent(getApplicationContext(), BoutiqueActivity.class));
                        return true;
                    case R.id.dropdown_menu4:
                        SharedPreferences.Editor prefes = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE).edit();
                        prefes.putInt("Connexion", 0).apply();
                        P_list.clear();
                        C_list.clear();
                        List_Papiers.clear();

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

        List_Papiers.clear();
        startActivity(new Intent(this, MesProduit.class));
    }
    @Override
    public void onBackPressed() {
        List_Papiers.clear();
        startActivity(new Intent(this, MesProduit.class));
        ;

    }
    public void acueil(View view) {
        startActivity(new Intent(this, Home.class));
        List_Papiers.clear();
    }

    public void VoirContrat(View view) {
        PAGE = 2;

if (List_Papiers.size()==0){
    final ProgressDialog loading = ProgressDialog.show(this, "Traitement Des Données...", "S'il Vous Plaît, Attendez...", false, false);

    JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "http://13.80.41.22/WebServicePhp/Papiers.php/?id=" +  C_list.get(Id).getId(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Papiers papiers;
                    if(response.length()!=0){
                        for (int i = 0; i < response.length(); i++) {
                            papiers = new Papiers();
                            papiers.setId(response.getJSONObject(i).getInt("id"));
                            papiers.setPath(response.getJSONObject(i).getString("path"));
                            papiers.setId_contrat(response.getJSONObject(i).getString("contrat_id"));
                            List_Papiers.add(papiers);

                            Intent intent = new Intent(getApplicationContext(), ConsulterContrat.class);
                            intent.putExtra("ID_Contrat", Id);
                            intent.putExtra("NbJOUR", NbJour);
                            startActivity(intent);
                        }
                    }else{
                            Intent intent = new Intent(getApplicationContext(), ConsulterContrat.class);
                            intent.putExtra("ID_Contrat", Id);
                            intent.putExtra("NbJOUR", NbJour);
                            startActivity(intent);
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
        int socketTimeout = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
}else {
    Intent intent = new Intent(getApplicationContext(), ConsulterContrat.class);
    intent.putExtra("ID_Contrat", Id);
    intent.putExtra("NbJOUR", NbJour);
    startActivity(intent);
}
    }
}
