package com.stillvalid.asus.stillvalid;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.stillvalid.asus.stillvalid.Models.Config_URL;
import com.stillvalid.asus.stillvalid.Models.Contrats;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.stillvalid.asus.stillvalid.DetailContrat.List_Papiers;
import static com.stillvalid.asus.stillvalid.DetailContrat.List_Type;
import static com.stillvalid.asus.stillvalid.MesProduit.C_list;
import static com.stillvalid.asus.stillvalid.MesProduit.P_list;

public class ModifierContrat extends AppCompatActivity {

    EditText Dates;
    RoundedImageView img_contrat;
    int Id;
    String types;
    String dates;
    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog picker;
    Spinner Types;
    ArrayAdapter<String> Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifier_contrat);
        Types = findViewById(R.id.type_contrat);
        Dates = findViewById(R.id.date_contrat);
        img_contrat = findViewById(R.id.img_contrat);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Id = bundle.getInt("ID_Contrat");

            Picasso.get()
                    .load(C_list.get(Id).getPhoto())
                    .resize(400,500)
                    .into(img_contrat);
            Adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, List_Type);
            Types.setAdapter(Adapter);
            for (int i = 0; i < List_Type.size(); i++) {
                if (List_Type.get(i).equals(C_list.get(Id).getType())) {
                    Types.setSelection(i);
                }
            }
            Dates.setText(C_list.get(Id).getDateEcheance());


//            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Config_URL.URL_TYPES, null, new Response.Listener<JSONArray>() {
//                @Override
//                public void onResponse(JSONArray response) {
//                    try {
//                        for (int i = 0; i < response.length(); i++) {
//                            List_Type.add(response.getJSONObject(i).getString("libelle"));
//                        }
//                        JsonArrayRequest requeste = new JsonArrayRequest(Request.Method.GET, Config_URL.URL_CONTRATS + Id, null, new Response.Listener<JSONArray>() {
//                            @Override
//                            public void onResponse(JSONArray response) {
//                                try {
//                                    Contrats contrats = new Contrats();
//                                    contrats.setId(response.getJSONObject(0).getInt("id"));
//                                    contrats.setType(response.getJSONObject(0).getString("type"));
//                                    contrats.setDateEcheance(response.getJSONObject(0).getString("decheance"));
//                                    contrats.setPhoto(response.getJSONObject(0).getString("photo"));
//                                    contrats.setId_user(response.getJSONObject(0).getString("user_id"));
//                                    List.add(contrats);
//
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                Picasso.get()
//                                        .load(List.get(0).getPhoto())
//                                        .resize(400,500)
//                                        .into(img_contrat);
//                                Adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, List_Type);
//                                Types.setAdapter(Adapter);
//                                for (int i = 0; i < List_Type.size(); i++) {
//                                    if (List_Type.get(i).equals(List.get(0).getType())) {
//                                        Types.setSelection(i);
//                                    }
//                                }
//                                Dates.setText(List.get(0).getDateEcheance());
//                            }
//                        }, new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//
//                            }
//                        });
//                        RequestQueue queu = Volley.newRequestQueue(getApplicationContext());
//                        queu.add(requeste);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//            RequestQueue queue = Volley.newRequestQueue(this);
//            queue.add(request);
            Types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    types = Adapter.getItem(i);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }

    public void getdate(View view) {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        picker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        }, year, month, day);
        picker.show();
    }

    private void updateLabel() {
        String myFormat = "dd MMMM yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
        Dates.setText(sdf.format(myCalendar.getTime()));
        Dates.setError(null);
    }

    public void valider(View view) {
        dates = Dates.getText().toString().trim();
        if (Valider()) {
            final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
            StringRequest request = new StringRequest(Request.Method.PUT, Config_URL.URL_CONTRAT + "/" + C_list.get(Id).getId(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject object = new JSONObject(response);
                        loading.dismiss();
                        if (!response.isEmpty()) {
                            C_list.get(Id).setType(types);
                            C_list.get(Id).setDateEcheance(dates);
                            Intent intent = new Intent(getApplicationContext(), MesProduit.class);
                            List_Type.clear();
                            List_Papiers.clear();
                            intent.putExtra("ID_Contrat", Id);
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

                    params.put("type", types);
                    params.put("decheance", dates);
                    return params;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        }
    }

    private boolean Valider() {
        boolean valide = true;
        if (dates.isEmpty()) {
            Dates.setError(getString(R.string.champs_obligatoir));
            valide = false;
        }
        return valide;
    }

    public void Modifier_img(View view) {
        DetailContrat.PAGE=1;
        Intent intent = new Intent(getApplicationContext(), ConsulterContrat.class);
        intent.putExtra("ID_Contrat", Id);
        startActivity(intent);
    }

    public void getMenu(View view) {
        showPopupWindow(view);
    }

    void showPopupWindow(View view) {
        PopupMenu popup = new PopupMenu(ModifierContrat.this, view);
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
                        List_Type.clear();
                        List_Papiers.clear();
                        return true;
                    case R.id.dropdown_menu2:
                        List_Type.clear();
                        List_Papiers.clear();
                        startActivity(new Intent(getApplicationContext(), AjoutProduit.class));
                        return true;
                    case R.id.dropdown_menu3:
                        List_Type.clear();
                        List_Papiers.clear();
                        startActivity(new Intent(getApplicationContext(), BoutiqueActivity.class));
                        return true;
                    case R.id.dropdown_menu4:
                        SharedPreferences.Editor prefes = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE).edit();
                        prefes.putInt("Connexion", 0).apply();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        List_Type.clear();
                        P_list.clear();
                        C_list.clear();
                        List_Papiers.clear();

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
        List_Papiers.clear();List_Type.clear();startActivity(new Intent(this, Home.class));
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),DetailProduit.class);
        ;

    }
}
