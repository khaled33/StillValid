package com.stillvalid.asus.stillvalid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.stillvalid.asus.stillvalid.Adapter.BotiqueAdapter;
import com.stillvalid.asus.stillvalid.Helper.RecyclerViewClickListener;
import com.stillvalid.asus.stillvalid.Helper.RecyclerViewTouchListener;
import com.stillvalid.asus.stillvalid.Models.Boutique;
import com.stillvalid.asus.stillvalid.Models.Config_URL;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static com.stillvalid.asus.stillvalid.MesProduit.C_list;
import static com.stillvalid.asus.stillvalid.MesProduit.P_list;

public class BoutiqueActivity extends AppCompatActivity {
    RecyclerView recycleview;
    ArrayList<Boutique> list = new ArrayList<>();
    BotiqueAdapter botiqueAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boutique);

        recycleview = findViewById(R.id.r);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "http://13.80.41.22/WebServicePhp/Annonces.php", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Boutique boutique;
                    for (int i = 0; i < response.length(); i++) {
                        boutique = new Boutique();
                        boutique.setId(response.getJSONObject(i).getInt("id"));
                        boutique.setTitre(response.getJSONObject(i).getString("titre"));
                        boutique.setVille(response.getJSONObject(i).getString("ville"));
                        boutique.setImage(response.getJSONObject(i).getString("photoProduit"));
                        boutique.setPrix(response.getJSONObject(i).getString("prix"));
                        boutique.setUser_id(response.getJSONObject(i).getString("user_id"));
                        list.add(boutique);
                    }
                    botiqueAdapter = new BotiqueAdapter(getApplicationContext(), list);
                    recycleview.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                            LinearLayoutManager.VERTICAL, false));
                    recycleview.setAdapter(botiqueAdapter);
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

        recycleview.addOnItemTouchListener(new RecyclerViewTouchListener(getApplicationContext(), recycleview,
                new RecyclerViewClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        TextView txt = view.findViewById(R.id.id);
                        Intent intent = new Intent(getApplicationContext(), FicheProduit.class);
                        intent.putExtra("ID_Annonce", txt.getText());
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
                        startActivity(new Intent(BoutiqueActivity.this, MesProduit.class));
                        return true;
                    case R.id.dropdown_menu2:
                        startActivity(new Intent(BoutiqueActivity.this, AjoutProduit.class));
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
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent ite = new Intent(this, Home.class);
            startActivity(ite);
        }
        return false;
    }
}
