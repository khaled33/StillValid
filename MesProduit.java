package com.stillvalid.asus.stillvalid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.stillvalid.asus.stillvalid.Adapter.ContratsAdapter;
import com.stillvalid.asus.stillvalid.Adapter.ProduitAdapter;
import com.stillvalid.asus.stillvalid.Helper.RecyclerViewClickListener;
import com.stillvalid.asus.stillvalid.Helper.RecyclerViewTouchListener;
import com.stillvalid.asus.stillvalid.Models.Config_URL;
import com.stillvalid.asus.stillvalid.Models.Contrats;
import com.stillvalid.asus.stillvalid.Models.Produit;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;


public class MesProduit extends AppCompatActivity {

    RecyclerView recyclerviewArticles, recyclerviewContrats;
    ProduitAdapter produitAdapter;
    ContratsAdapter ContratsAdapter;
    SharedPreferences prefs;
    public static  ArrayList<Produit> P_list = new ArrayList<Produit>();
    public static ArrayList<Contrats> C_list = new ArrayList<Contrats>();
    String ID;
    public static final int REQUEST_PERMISSION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mes_produit);
        checkpermission();

        System.setProperty("http.keepAlive", "false");

        prefs = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
        ID = prefs.getString("ID", null);
        recyclerviewArticles = findViewById(R.id.recyclerviewArticles);
        recyclerviewContrats = findViewById(R.id.recyclerviewContrats);

        produitAdapter = new ProduitAdapter(getApplicationContext(), P_list);
        recyclerviewArticles.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false));
        recyclerviewArticles.setAdapter(produitAdapter);

        ContratsAdapter = new ContratsAdapter(getApplicationContext(), C_list);
        recyclerviewContrats.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false));
        recyclerviewContrats.setAdapter(ContratsAdapter);

//

        recyclerviewContrats.addOnItemTouchListener(new RecyclerViewTouchListener(getApplicationContext(), recyclerviewContrats, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                TextView txt = view.findViewById(R.id.id);
                TextView NbJour = view.findViewById(R.id.jourContrat);
                Intent intent = new Intent(getApplicationContext(), DetailContrat.class);
                intent.putExtra("ID_Contrat", position);
                intent.putExtra("NbJOUR", NbJour.getText());

                startActivity(intent);


            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        recyclerviewArticles.addOnItemTouchListener(new RecyclerViewTouchListener(getApplicationContext(), recyclerviewContrats, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                TextView txt = view.findViewById(R.id.id);
                TextView NbJour = view.findViewById(R.id.jourArticle);
                Intent intent = new Intent(getApplicationContext(), DetailProduit.class);
                intent.putExtra("ID_Produit", position);
                intent.putExtra("NbJOUR", NbJour.getText());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

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
                        startActivity(new Intent(getApplicationContext(), MesProduit.class));
                          
                        return true;
                    case R.id.dropdown_menu2:
                        startActivity(new Intent(getApplicationContext(), AjoutProduit.class));
                          
                        return true;
                    case R.id.dropdown_menu3:
                          
                        startActivity(new Intent(getApplicationContext(), BoutiqueActivity.class));
                        return true;
                    case R.id.dropdown_menu4:
                        SharedPreferences.Editor prefes = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE).edit();
                        prefes.putInt("Connexion", 0).apply();

                        LoginActivity.List.clear();
                        P_list.clear();
                        C_list.clear();
//                        Intent intent = new Intent(Intent.ACTION_MAIN);
//                        intent.addCategory(Intent.CATEGORY_HOME);
//                        startActivity(intent);
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
    public void acueil(View view) {
          startActivity(new Intent(this, Home.class));
    }

    public void checkpermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Thanks for granting Permission", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
