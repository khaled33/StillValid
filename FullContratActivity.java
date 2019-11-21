package com.stillvalid.asus.stillvalid;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;
import com.stillvalid.asus.stillvalid.Models.Config_URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.stillvalid.asus.stillvalid.DetailContrat.List_Papiers;
import static com.stillvalid.asus.stillvalid.MesProduit.C_list;

public class FullContratActivity extends AppCompatActivity {

    ImageView imageView, reglage;
    int Id,Id_contrat;
    String NbJour;
    public static final int REQUEST_IMAGE = 300;
    public static final int REQUEST_IMAGE_imp = 100;
    Uri imageContratgalery, imageContratcam;
    Bitmap bitmapContratcam, bitmapContaratgalery;
    String Imagecam, Imagegalerry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_contrat);
        imageView = findViewById(R.id.imageView18);
        reglage = findViewById(R.id.imageView20);

        if (DetailContrat.PAGE == 1) {
            reglage.setVisibility(View.VISIBLE);
        } else {
            reglage.setVisibility(View.INVISIBLE);
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            NbJour = bundle.getString("NbJOUR");
            Id = (bundle.getInt("ID_Image"));
            Id_contrat = (bundle.getInt("ID_Contrat"));
            Picasso.get()
                    .load(List_Papiers.get(Id).getPath())
                    .resize(400, 500)
                    .into(imageView);
        }

    }

    public void Edite(View view) {
        selectImage();
    }

    private void selectImage() {
        final String etat1 = getString(R.string.etat1);
        final String etat2 = getString(R.string.etat2);
        final CharSequence[] items = {etat1, etat2};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(etat1)) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                    imageContratcam = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageContratcam);
                    startActivityForResult(intent, REQUEST_IMAGE);
                } else if (items[item].equals(etat2)) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQUEST_IMAGE_imp);
                }
            }
        });
        builder.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_imp && resultCode == RESULT_OK) {
            imageContratgalery = data.getData();
            try {
                Bitmap thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), imageContratgalery);
                Imagegalerry = getRealPathFromURI(imageContratgalery);
                bitmapContaratgalery = rotationImage(thumbnail, Imagegalerry);
                imageView.setImageBitmap(bitmapContaratgalery);
                final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
                StringRequest request = new StringRequest(Request.Method.PUT, Config_URL.URL_PAPIER + "/" + List_Papiers.get(Id).getId()
                        , new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject object = new JSONObject(response);

                            if (!response.isEmpty()) {
                                loading.dismiss();
                                Intent intent = new Intent(getApplicationContext(), DetailContrat.class);
                                intent.putExtra("ID_Contrat", Id_contrat);
                                List_Papiers.get(Id).setPath(object.getString("path"));
                                C_list.get(Id_contrat).setPhoto(object.getString("path"));
                                List_Papiers.clear();
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
                        Toast.makeText(FullContratActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> hmap = new HashMap<String, String>();
                        hmap.put("photo", getStringImage(bitmapContaratgalery));
                        return hmap;
                    }
                };
                int socketTimeout = 50000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                request.setRetryPolicy(policy);
                RequestQueue queue = Volley.newRequestQueue(this);
                queue.add(request);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            try {
                Bitmap thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), imageContratcam);
                Imagecam = getRealPathFromURI(imageContratcam);
                bitmapContratcam = rotationImage(thumbnail, Imagecam);
                imageView.setImageBitmap(bitmapContratcam);
                final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
                StringRequest request = new StringRequest(Request.Method.PUT, Config_URL.URL_PAPIER + "/" + List_Papiers.get(Id).getId()
                        , new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject object = new JSONObject(response);

                            if (!response.isEmpty()) {
                                loading.dismiss();
                                Intent intent = new Intent(getApplicationContext(), DetailContrat.class);
                                intent.putExtra("ID_Contrat", Id_contrat);
                                List_Papiers.get(Id).setPath(object.getString("path"));
                                C_list.get(Id_contrat).setPhoto(object.getString("path"));
                                List_Papiers.clear();
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
                        Toast.makeText(FullContratActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> hmap = new HashMap<String, String>();
                        hmap.put("photo", getStringImage(bitmapContratcam));
                        return hmap;
                    }
                };
                int socketTimeout = 50000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                request.setRetryPolicy(policy);
                RequestQueue queue = Volley.newRequestQueue(this);
                queue.add(request);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
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

    public void Retour(View view) {
        Intent intent = new Intent(getApplicationContext(), ConsulterContrat.class);
        intent.putExtra("ID_Contrat", Id_contrat);
        intent.putExtra("ID_Page", 1);
        intent.putExtra("NbJOUR", NbJour);
        startActivity(intent);
    }
}
