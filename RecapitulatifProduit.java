package com.stillvalid.asus.stillvalid;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
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
import com.stillvalid.asus.stillvalid.Models.Config_URL;
import com.stillvalid.asus.stillvalid.Models.Marques;
import com.stillvalid.asus.stillvalid.Models.Produit;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import static com.stillvalid.asus.stillvalid.MarqueProduit.List_Marques;
import static com.stillvalid.asus.stillvalid.MarqueProduit.indexMarque;
import static com.stillvalid.asus.stillvalid.MesProduit.C_list;
import static com.stillvalid.asus.stillvalid.MesProduit.P_list;

public class RecapitulatifProduit extends AppCompatActivity {
    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog picker;
    SharedPreferences prefs, pref;
    SharedPreferences.Editor editor;
    ImageView Img_Produit, Img_Facture;
    EditText Enseigne, Nom_Prod, Date_Achat, Duree_Garantie;
    String Id_User, enseigne, marque, nom_Prod, date_Achat, sav, duree_Garantie, Date_Fin;
    String URI_prod, URI_fact;
    String imageFacture, imageArticle;
    ArrayList<Marques> List = new ArrayList<>();
    Produit produit,produitReponse;
    ArrayAdapter<String> Adapter;
    Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recapitulatif_produit);

        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();

        pref = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
        prefs = getApplicationContext().getSharedPreferences("Produit", MODE_PRIVATE);
        editor = prefs.edit();
        Id_User = pref.getString("ID", null);
        Enseigne = findViewById(R.id.txtenseigne);
        spinner = findViewById(R.id.txtmarque);
        Nom_Prod = findViewById(R.id.txtnom_prod);
        Date_Achat = findViewById(R.id.txtdate);
        Duree_Garantie = findViewById(R.id.txtdure);
        Img_Produit = findViewById(R.id.img_prod);
        Img_Facture = findViewById(R.id.img_facture);
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Config_URL.URL_Marque, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    com.stillvalid.asus.stillvalid.Models.Marques marques;
                    for (int i = 0; i < response.length(); i++) {
                        marques = new Marques();
                        marques.setId(response.getJSONObject(i).getInt("id"));
                        marques.setSupport(response.getJSONObject(i).getString("support"));
                        marques.setSav(response.getJSONObject(i).getString("sav"));
                        marques.setLibelle(response.getJSONObject(i).getString("libelle"));
                        List.add(marques);
                    }
                    Adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, List_Marques);
                    spinner.setAdapter(Adapter);
                    spinner.setSelection(indexMarque);

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            marque=Adapter.getItem(i);
                            sav=List.get(i).getSav();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                    loading.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RecapitulatifProduit.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);


        Remplir_Value();
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
                updateLabel();
            }
        }, year, month, day);
        picker.show();
    }
    private void updateLabel() {
        String myFormat = "dd MMMM yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
        Date_Achat.setText(sdf.format(myCalendar.getTime()));
        Date_Achat.setError(null);
    }
    class MyAsyncTask extends AsyncTask<Void, Void, String> {
         ProgressDialog loading;

        @Override
        protected String doInBackground(Void... voids) {

            if (AjoutPhotoProduit.bitmapArticle != null) {
                imageArticle = getStringImage(AjoutPhotoProduit.bitmapArticle);
            } else {
                imageArticle = "";
            }
            if (AjoutPhotoProduit.bitmapFacture != null) {
                imageFacture = getStringImage(AjoutPhotoProduit.bitmapFacture);
            } else {
                imageFacture = "";
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.i("tt",imageFacture);
            Log.i("aa",imageArticle);
            loading.dismiss();
            super.onPostExecute(s);
        }
        @Override
        protected void onPreExecute() {
            loading = ProgressDialog.show(RecapitulatifProduit.this, "Traitement Des Données...", "Attendez S'il Vous Plaît...", false, false);


        }
    }

    public void valider(View view) {
        enseigne = Enseigne.getText().toString().trim();
//        marque = Marques.getText().toString().trim();
        nom_Prod = Nom_Prod.getText().toString().trim();
        date_Achat = Date_Achat.getText().toString().trim();
        duree_Garantie = Duree_Garantie.getText().toString().trim();
        Calendar cal = Calendar.getInstance();
        String myFormat = "dd MMMM yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
        try {
            cal.setTime(sdf.parse(date_Achat));
            cal.add(Calendar.MONTH, Integer.parseInt(duree_Garantie));
            Date_Fin=sdf.format(cal.getTime());
            editor.putString("Duree_Garantie", duree_Garantie);
            editor.putString("Date_Fin", sdf.format(cal.getTime()));
            editor.apply();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (valider()) {
            final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    Config_URL.URL_PRODUIT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {

                            loading.dismiss();
                            if (!s.isEmpty()) {

                                P_list.clear();
                        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "http://13.80.41.22/WebServicePhp/Articles.php/?id=" + Id_User, null, new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    Produit produit;
                                    for (int i = 0; i < response.length(); i++) {
                                        produit = new Produit();
                                        produit.setId(response.getJSONObject(i).getInt("id"));
                                        produit.setEnseigne(response.getJSONObject(i).getString("enseigne"));
                                        produit.setNom(response.getJSONObject(i).getString("nom"));
                                        produit.setPhoto(response.getJSONObject(i).getString("photo"));
                                        produit.setFacture(response.getJSONObject(i).getString("facture"));
                                        produit.setDuree(response.getJSONObject(i).getString("garantie"));
                                        produit.setMarque(response.getJSONObject(i).getString("marque"));
                                        produit.setDateAchat(response.getJSONObject(i).getString("dAchat"));
                                        produit.setDateFin(response.getJSONObject(i).getString("dFin"));
                                        produit.setUser_id(response.getJSONObject(i).getString("user_id"));
                                        produit.setSav(response.getJSONObject(i).getString("sav"));
                                        P_list.add(produit);

                                        resetvalue();
                                        startActivity(new Intent(getApplicationContext(), MesProduit.class));

                                    }
                                    Toast.makeText(getApplicationContext(), R.string.Insertion_produit, Toast.LENGTH_SHORT).show();


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
//                        int socketTimeout = 5000;
//                        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//                        request.setRetryPolicy(policy);
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        queue.add(request);



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

                    params.put("user", Id_User);
                    params.put("enseigne", enseigne);
                    params.put("marque", marque);
                    params.put("nom", nom_Prod);
                    params.put("dachat", date_Achat);
                    params.put("garantie", duree_Garantie);
                    params.put("photo", imageArticle);
                    params.put("facture", imageFacture);
                    params.put("dfin", Date_Fin);
                    params.put("sav", sav);

                    return params;
                }
            };
            //Creating a Request Queue
            int socketTimeout = 50000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
    }

    public String getStringImage(Bitmap bitmap) {
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
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
        if (enseigne.isEmpty()) {
            Enseigne.setError(getString(R.string.champs_obligatoir));
            valide = false;
        }

        if (nom_Prod.isEmpty()) {
            Nom_Prod.setError(getString(R.string.champs_obligatoir));
            valide = false;
        }
        if (date_Achat.isEmpty()) {
            Date_Achat.setError(getString(R.string.champs_obligatoir));
            valide = false;
        }
        if (duree_Garantie.isEmpty()) {
            Duree_Garantie.setError(getString(R.string.champs_obligatoir));
            valide = false;
        }
        return valide;
    }

    public void resetvalue() {
        SharedPreferences.Editor editor = getSharedPreferences("Produit", MODE_PRIVATE).edit();
        editor.remove("Enseigne");
        editor.remove("Marque");
        editor.remove("ID_Marque");
        editor.remove("Nom_Produit");
        editor.remove("Date_Achat");
        editor.remove("Duree_Garantie");
        editor.remove("Date_Fin");
        editor.remove("Photo_Produit");
        editor.remove("Photo_Facture");
        editor.remove("SAV");
        editor.apply();
    }

    public void Remplir_Value() {
        String restoredmarque = prefs.getString("Marque", null);
        String restoredenseigne = prefs.getString("Enseigne", null);
        String restorednom_prod = prefs.getString("Nom_Produit", null);
        String restoreddate = prefs.getString("Date_Achat", null);
        String restoreddateFin = prefs.getString("Date_Fin", null);
        String restoredduree = prefs.getString("Duree_Garantie", null);
        String restoredprod = prefs.getString("Photo_Produit", null);
        String restoredfact = prefs.getString("Photo_Facture", null);
        String restoredsav = prefs.getString("SAV", null);
        if (restoredsav != null) {
            sav = restoredsav;
        } else {
            sav = "";
        }
        if (restoredenseigne != null) {
            Enseigne.setText(restoredenseigne);
        }
        if (restorednom_prod != null) {
            Nom_Prod.setText(restorednom_prod);
        }
        if (restoreddate != null) {
            Date_Achat.setText(restoreddate);
        }
        if (restoreddateFin != null) {
            Date_Fin = restoreddateFin;
        }
        if (restoredduree != null) {
            Duree_Garantie.setText(restoredduree);
        }
        if (restoredprod != null) {
            Img_Produit.setImageResource(R.drawable.ic_checked);
            URI_prod = restoredprod;
        } else {
            Img_Produit.setImageResource(R.drawable.ic_x);
            URI_prod = "";
        }
        if (restoredfact != null) {
            Img_Facture.setImageResource(R.drawable.ic_checked);
            URI_fact = restoredfact;
        } else {
            Img_Facture.setImageResource(R.drawable.ic_x);
            URI_fact = "";
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
                        startActivity(new Intent(RecapitulatifProduit.this, MesProduit.class));
                        return true;
                    case R.id.dropdown_menu2:
                        startActivity(new Intent(RecapitulatifProduit.this, AjoutProduit.class));
                        return true;
                    case R.id.dropdown_menu3:
                        startActivity(new Intent(getApplicationContext(), BoutiqueActivity.class));
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

    public void acueil(View view) {
        startActivity(new Intent(this, Home.class));
    }

    public void precedent(View view) {
        startActivity(new Intent(this, AjoutPhotoProduit.class));
    }

    public void EditePhoto(View view) {
        startActivity(new Intent(this, AjoutPhotoProduit.class));
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
