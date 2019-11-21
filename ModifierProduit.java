package com.stillvalid.asus.stillvalid;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
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
import com.squareup.picasso.Picasso;
import com.stillvalid.asus.stillvalid.Models.Config_URL;
import com.stillvalid.asus.stillvalid.Models.Contrats;
import com.stillvalid.asus.stillvalid.Models.Marques;
import com.stillvalid.asus.stillvalid.Models.Produit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.stillvalid.asus.stillvalid.MarqueProduit.List_Marques;
import static com.stillvalid.asus.stillvalid.MesProduit.C_list;
import static com.stillvalid.asus.stillvalid.MesProduit.P_list;

public class ModifierProduit extends AppCompatActivity {

    public static final int REQUEST_IMAGE = 100;
    public static final int REQUEST_FACTUR = 300;
    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog picker;
    CircleImageView Img_produit;
    EditText Enseigne, Marque, Nom, DateAchat, Garentie;
    int Id;
    String enseigne, sav, marque, nom, date, garentie, imageFacture, imageArticle, dateFin;
    ArrayList<Produit> List = new ArrayList<Produit>();
    Uri imageUri, imageUrifacture;
    Bitmap bitmapArticle = null, bitmapFacture = null;
    Boolean clicArticle = false, clicFacture = false;
    Produit produit = new Produit();
    ArrayList<Marques> list = new ArrayList<>();
    ArrayAdapter<String> Adapter;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifier_produit);
        Enseigne = findViewById(R.id.enseigne);
        Nom = findViewById(R.id.nom);
        spinner = findViewById(R.id.marque);
        DateAchat = findViewById(R.id.dateAchat);
        Garentie = findViewById(R.id.garantie);
        Img_produit = findViewById(R.id.img_produit);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Id = bundle.getInt("ID_Produit");

            produit.setId(Id);
            produit.setEnseigne(P_list.get(Id).getEnseigne());
            produit.setNom(P_list.get(Id).getNom());
            produit.setPhoto(P_list.get(Id).getPhoto());
            produit.setFacture(P_list.get(Id).getFacture());
            imageFacture = P_list.get(Id).getFacture();
            imageArticle = P_list.get(Id).getPhoto();
            produit.setDuree(P_list.get(Id).getDuree());
            produit.setMarque(P_list.get(Id).getMarque());
            produit.setDateAchat(P_list.get(Id).getDateAchat());
            produit.setDateFin(P_list.get(Id).getDateFin());
            produit.setUser_id(P_list.get(Id).getUser_id());
            produit.setSav(P_list.get(Id).getSav());

            List.add(produit);
            Enseigne.setText(List.get(0).getEnseigne());
//                    Marque.setText(List.get(0).getMarque());
            Nom.setText(List.get(0).getNom());
            DateAchat.setText(List.get(0).getDateAchat());
            Garentie.setText(List.get(0).getDuree());
            Picasso.get()
                    .load(List.get(0).getPhoto())
                    .into(Img_produit);

            List_Marques.clear();
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
                            list.add(marques);
                            List_Marques.add(response.getJSONObject(i).getString("libelle"));
                        }
                        Adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, List_Marques);
                        spinner.setAdapter(Adapter);
                        spinner.setSelection(List_Marques.indexOf(List.get(0).getMarque()));

                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                marque = Adapter.getItem(i);
                                sav = list.get(i).getSav();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

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
//       int index= List_Marques.indexOf(List.get(0).getMarque());
//            Toast.makeText(this, ""+List_Marques.indexOf(List.get(0).getMarque()), Toast.LENGTH_SHORT).show();
//        spinner.setSelection(indexMarque);

        }
    }

    public void valider(View view) {
        enseigne = Enseigne.getText().toString().trim();
        nom = Nom.getText().toString().trim();
        date = DateAchat.getText().toString().trim();
        garentie = Garentie.getText().toString().trim();
        Calendar cal = Calendar.getInstance();
        String myFormat = "dd MMMM yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
        if (Valider()) {
            try {
                cal.setTime(sdf.parse(date));
                cal.add(Calendar.MONTH, Integer.parseInt(garentie));
                dateFin = sdf.format(cal.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
            StringRequest request = new StringRequest(Request.Method.PUT, Config_URL.URL_PRODUIT + "/" + P_list.get(Id).getId(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject object = new JSONObject(response);
                        loading.dismiss();
                        if (!response.isEmpty()) {
                            Gson gson = new Gson();
                            Produit produit = gson.fromJson(response, Produit.class);

                            P_list.get(Id).setEnseigne(produit.getEnseigne());
                            P_list.get(Id).setMarque(produit.getMarque());
                            P_list.get(Id).setNom(produit.getNom());
                            P_list.get(Id).setDateAchat(date);
                            P_list.get(Id).setPhoto(produit.getPhoto());
                            P_list.get(Id).setFacture(produit.getFacture());
                            P_list.get(Id).setDateFin(dateFin);
                            P_list.get(Id).setSav(produit.getSav());
                            P_list.get(Id).setDuree(garentie);
                            P_list.get(Id).setSav(sav);

                            Intent intent = new Intent(getApplicationContext(), MesProduit.class);
                            intent.putExtra("ID_Produit", Id);

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
                    if (clicFacture) {
                        imageFacture = getStringImage(bitmapFacture);
                        clicFacture = false;
                    }
                    if (clicArticle) {
                        imageArticle = getStringImage(bitmapArticle);
                        clicFacture = false;
                    }
                    HashMap<String, String> params = new HashMap<String, String>();

                    params.put("enseigne", enseigne);
                    params.put("marque", marque);
                    params.put("nom", nom);
                    params.put("dachat", date);
                    params.put("garantie", garentie);
                    params.put("photo", imageArticle);
                    params.put("facture", imageFacture);
                    params.put("dfin", dateFin);
                    params.put("sav", sav);
                    return params;
                }
            };
            int socketTimeout = 50000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            request.setRetryPolicy(policy);
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        }
    }

    private boolean Valider() {
        boolean valide = true;
        if (enseigne.isEmpty()) {
            Enseigne.setError(getString(R.string.champs_obligatoir));
            valide = false;
        }
        if (marque.isEmpty()) {
            Marque.setError(getString(R.string.champs_obligatoir));
            valide = false;
        }
        if (nom.isEmpty()) {
            Nom.setError(getString(R.string.champs_obligatoir));
            valide = false;
        }
        if (date.isEmpty()) {
            DateAchat.setError(getString(R.string.champs_obligatoir));
            valide = false;
        }
        if (garentie.isEmpty()) {
            Garentie.setError(getString(R.string.champs_obligatoir));
            valide = false;
        }
        return valide;
    }

    public String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public void Photo_Produit(View view) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    public void Photo_facture(View view) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUrifacture = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUrifacture);
        startActivityForResult(intent, REQUEST_FACTUR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            clicArticle = true;
            try {
                Bitmap thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                String realurl = getRealPathFromURI(imageUri);
                bitmapArticle = rotationImage(thumbnail, realurl);
//                editor.putString("Photo_Produit", realurl);
//                editor.apply();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (requestCode == REQUEST_FACTUR && resultCode == RESULT_OK) {
            clicFacture = true;
            try {
                Bitmap thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUrifacture);
                String realurl = getRealPathFromURI(imageUrifacture);
                bitmapFacture = rotationImage(thumbnail, realurl);
//                editor.putString("Photo_Facture", realurl);
//                editor.apply();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void GetDate(View view) {
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
        DateAchat.setText(sdf.format(myCalendar.getTime()));
        DateAchat.setError(null);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public Bitmap rotationImage(Bitmap bitmap, String imageUri) throws IOException {
        ExifInterface exifInterface = new ExifInterface(imageUri);
        int oreintation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (oreintation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);
            default:
                return bitmap;
        }
    }

    private Bitmap flip(Bitmap bitmap, boolean horizontal, boolean verticale) {
        Matrix matrix = new Matrix();
        matrix.postScale(horizontal ? -1 : 1, verticale ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
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

                        startActivity(new Intent(ModifierProduit.this, MesProduit.class));
                        return true;
                    case R.id.dropdown_menu2:

                        startActivity(new Intent(ModifierProduit.this, AjoutProduit.class));
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

}
