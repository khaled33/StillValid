package com.stillvalid.asus.stillvalid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.stillvalid.asus.stillvalid.Models.Config_URL;
import com.stillvalid.asus.stillvalid.Models.Marques;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.stillvalid.asus.stillvalid.MesProduit.C_list;
import static com.stillvalid.asus.stillvalid.MesProduit.P_list;

public class MarqueProduit extends AppCompatActivity {
    public static int indexMarque;
    ArrayList<Marques> List = new ArrayList<>();
    public static ArrayList<String> List_Marques = new ArrayList<>();
    ArrayAdapter<String> Adapter;
    Spinner spinner;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marque_produit);

        prefs = getApplicationContext().getSharedPreferences("Produit", MODE_PRIVATE);
        editor = prefs.edit();
        spinner = findViewById(R.id.marque);

List_Marques.clear();
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Config_URL.URL_Marque, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Marques marques;
                    for (int i = 0; i < response.length(); i++) {
                        marques = new Marques();
                        marques.setId(response.getJSONObject(i).getInt("id"));
                        marques.setSupport(response.getJSONObject(i).getString("support"));
                        marques.setSav(response.getJSONObject(i).getString("sav"));
                        marques.setLibelle(response.getJSONObject(i).getString("libelle"));
                        List.add(marques);
                        List_Marques.add(response.getJSONObject(i).getString("libelle"));
                    }
                    Adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, List_Marques);
                    spinner.setAdapter(Adapter);
                    int restoredmarque = prefs.getInt("ID_Marque", -1);
                    if (restoredmarque!=-1){
                        spinner.setSelection(restoredmarque);
                    }
                    loading.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MarqueProduit.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                editor.putString("Marque", Adapter.getItem(i));
                editor.putInt("ID_Marque", i);
                  indexMarque= i;
                editor.putString("SAV", List.get(i).getSav());
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void getMenu(View view) {
        showPopupWindow(view);
    }

    void showPopupWindow(View view) {
        PopupMenu popup = new PopupMenu(this, view);
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
                        startActivity(new Intent(MarqueProduit.this, MesProduit.class));
                        return true;
                    case R.id.dropdown_menu2:
                        startActivity(new Intent(MarqueProduit.this, AjoutProduit.class));
                        return true;
                    case R.id.dropdown_menu3:
                        startActivity(new Intent(getApplicationContext(), BoutiqueActivity.class));
                        return true;
                    case R.id.dropdown_menu4:
                        SharedPreferences.Editor prefes = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE).edit();
                        prefes.putInt("Connexion", 0).apply();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        P_list.clear();
                        C_list.clear();
                        finishAffinity();
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

    public void Activite_suivant(View view) {

       startActivity(new Intent(this, NomProduit.class));
    }

    public void precedent(View view) {
        startActivity(new Intent(this, EnseigneAchat.class));
    }


}