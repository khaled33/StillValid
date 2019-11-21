package com.stillvalid.asus.stillvalid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.stillvalid.asus.stillvalid.Models.Config_URL;
import com.stillvalid.asus.stillvalid.Models.Produit;

import org.json.JSONArray;
import org.json.JSONException;

import static com.stillvalid.asus.stillvalid.MesProduit.P_list;

public class ConsulterFacture extends AppCompatActivity {

    ImageView img_facture;
    int Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulter_facture);

        img_facture = findViewById(R.id.imageView6);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
             Id = bundle.getInt("ID_Produit");

            Picasso.get()
                    .load(P_list.get(Id).getFacture())
                    .resize(400,500)
                    .into(img_facture);

        }
    }

    public void RetourDetailProduit(View view) {
        Intent intent = new Intent(this, DetailProduit.class);
        intent.putExtra("ID_Produit",Id);
        startActivity(intent);

    }


}
