package com.stillvalid.asus.stillvalid;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
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
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.stillvalid.asus.stillvalid.Models.Config_URL;
import com.stillvalid.asus.stillvalid.Models.Contrats;
import com.stillvalid.asus.stillvalid.Models.Produit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import static com.stillvalid.asus.stillvalid.MesProduit.C_list;
import static com.stillvalid.asus.stillvalid.MesProduit.P_list;

public class RecapitulatifContrat extends AppCompatActivity {
    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date;
    DatePickerDialog picker;
    SharedPreferences prefs, pref;
    SharedPreferences.Editor editor;
    ImageView Img_Contrat;
    EditText  Date_Echeance;
    String types, date_echeance, Id_User, URI_contrat;
    String ID_Contrat, item;
    Contrats contrats,ContartReponse;
Spinner Types;
    ArrayList<String> List_Type = new ArrayList<>();
    ArrayAdapter<String> Adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recapitulatif_contrat);

        pref = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
        prefs = getApplicationContext().getSharedPreferences("Contrat", MODE_PRIVATE);
        editor = prefs.edit();
        Id_User = pref.getString("ID", null);
        Types = findViewById(R.id.txt_type);
        Date_Echeance = findViewById(R.id.txtdate_echeanche);
        Img_Contrat = findViewById(R.id.image_contrat);
        


        Remplir_Value();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Config_URL.URL_TYPES, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        List_Type.add(response.getJSONObject(i).getString("libelle"));
                    }
                    Adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, List_Type);
                    Types.setAdapter(Adapter);
                    int restoredtype = prefs.getInt("ID_Type", -1);
                    if (restoredtype!=-1){
                        Types.setSelection(restoredtype);
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

        Types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                types=  Adapter.getItem(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void Activite_suivant(View view) {
//        types = Types.getText().toString().trim();
        date_echeance = Date_Echeance.getText().toString().trim();
        if (valider()) {
            final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
            StringRequest request = new StringRequest(Request.Method.POST, Config_URL.URL_CONTRAT, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject object = new JSONObject(response);

                        if (!response.isEmpty()) {
                            ID_Contrat = object.getString("id");
                             contrats=new Contrats();

                            Gson gson = new Gson();
                            ContartReponse = gson.fromJson(response, Contrats.class);

                            contrats.setId(ContartReponse.getId());
                            contrats.setType(types);
                            contrats.setDateEcheance(date_echeance);
                            contrats.setPhoto(ContartReponse.getPhoto());
                            contrats.setId_user(Id_User);
                            C_list.add(contrats);

                            RecapitulatifContrat.MyAsyncTask myAsyncTask = new RecapitulatifContrat.MyAsyncTask();
                            myAsyncTask.execute();
                            resetvalue();
//                            loading.dismiss();
                            //startActivity(new Intent(getApplicationContext(), MesProduit.class));

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
                    hmap.put("type", types);
                    hmap.put("decheance", date_echeance);
                    int test = AjouterPhotoContrat.listPhotoContarat.size();
                    if(test!=0){
                        hmap.put("photo", item = getStringImage(AjouterPhotoContrat.listPhotoContarat.get(0)));
                    }else {
                        hmap.put("photo","");
                    }
                    hmap.put("user_id", Id_User);
                    return hmap;
                }
            };
            int socketTimeout = 30000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            request.setRetryPolicy(policy);
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        }
    }

    public void getDate(View view) {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        picker = new DatePickerDialog(this,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                editor.putLong("Date_Mili", myCalendar.getTimeInMillis());
                editor.apply();
                updateLabel();
            }
        }, year, month, day);
        picker.show();
    }
    private void updateLabel() {
        String myFormat = "dd MMMM yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
        Date_Echeance.setText(sdf.format(myCalendar.getTime()));
        Date_Echeance.setError(null);
    }
    class MyAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            for (int i = 0; i < AjouterPhotoContrat.listPhotoContarat.size(); i++) {

                item = getStringImage(AjouterPhotoContrat.listPhotoContarat.get(i));
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        "http://13.80.41.22/stillvalid/StillValid/web/app_dev.php/Papier",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {

                                if (!s.isEmpty()) {
                                    resetvalue();


                                } else {
                                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {

                                Toast.makeText(getApplicationContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new Hashtable<String, String>();

                        params.put("photo", item);
                        params.put("id_contrat", ID_Contrat);
                        return params;
                    }
                };
                //Creating a Request Queue
                int socketTimeout = 50000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
             ProgressDialog loading = ProgressDialog.show(RecapitulatifContrat.this, "Uploading...", "Please wait...", false, false);
            loading.show();
            super.onPostExecute(s);
              Toast.makeText(getApplicationContext(), "Votre Contrat a été ajouteé avec succès", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(getApplicationContext(), MesProduit.class));
        }
        @Override
        protected void onPreExecute() {

        }
    }


    public String getStringImage(Bitmap bitmap) {
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] b = baos.toByteArray();
            baos.flush();
            baos.close();
            return Base64.encodeToString(b, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null ;

    }

    private boolean valider() {
        boolean valide = true;
//        if (types.isEmpty()) {
//            Types.setError(getString(R.string.champs_obligatoir));
//            valide = false;
//        }
        if (date_echeance.isEmpty()) {
            Date_Echeance.setError(getString(R.string.champs_obligatoir));
            valide = false;
        }
        return valide;
    }

    public void resetvalue() {
        SharedPreferences.Editor editor = getSharedPreferences("Contrat", MODE_PRIVATE).edit();
        editor.remove("Types");
        editor.remove("Date_Echeance");
        editor.remove("ID_Type");
        editor.remove("Photo_Contrat");
        editor.apply();
    }

    private void Remplir_Value() {

        String restoredtype = prefs.getString("Type", null);
        String restoreddate = prefs.getString("Date_Echeance", null);
        String restoredimgC = prefs.getString("Photo_Contrat", null);

//        if (restoredtype != null) {
//            Types.setText(restoredtype);
//        }
        if (restoreddate != null) {
            Date_Echeance.setText(restoreddate);
        }
        if (restoredimgC != null) {
            Img_Contrat.setImageResource(R.drawable.ic_checked);
            URI_contrat = restoredimgC;
        } else {
            Img_Contrat.setImageResource(R.drawable.ic_x);
            URI_contrat = "";
        }
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
                        startActivity(new Intent(RecapitulatifContrat.this, MesProduit.class));
                        return true;
                    case R.id.dropdown_menu2:
                        startActivity(new Intent(RecapitulatifContrat.this, AjoutProduit.class));
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

    public void precedent(View view) {
        startActivity(new Intent(this, AjouterPhotoContrat.class));
    }

    public void EditePhoto_contrat(View view) {
        startActivity(new Intent(this, AjouterPhotoContrat.class));
    }
}
