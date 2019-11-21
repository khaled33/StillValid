package com.stillvalid.asus.stillvalid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.stillvalid.asus.stillvalid.Adapter.ImageAdapter;
import com.stillvalid.asus.stillvalid.Models.Config_URL;
import com.stillvalid.asus.stillvalid.Models.Papiers;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static com.stillvalid.asus.stillvalid.DetailContrat.List_Papiers;
import static com.stillvalid.asus.stillvalid.MesProduit.C_list;

public class ConsulterContrat extends AppCompatActivity {

    int Id;
    GridView gridView;

    ImageAdapter myAdapter;
    String NbJour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulter_contrat);

        gridView = findViewById(R.id.gridview);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Id = bundle.getInt("ID_Contrat");
            NbJour= bundle.getString("NbJOUR");
        }
        myAdapter = new ImageAdapter(getApplicationContext(), List_Papiers);
        gridView.setAdapter(myAdapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    TextView txt = (TextView) view.findViewById(R.id.grid_item_label);

                    Intent intent = new Intent(getApplicationContext(), FullContratActivity.class);
                    intent.putExtra("ID_Contrat", Id);
                    intent.putExtra("ID_Image", i);
                    intent.putExtra("NbJOUR", NbJour);

                    startActivity(intent);
                }
            });
        }

    public void RetourDetailContrat(View view) {

       if( DetailContrat.PAGE==1){
           Intent intent = new Intent(this, ModifierContrat.class);
           intent.putExtra("ID_Contrat", Id);
           intent.putExtra("NbJOUR", NbJour);
           startActivity(intent);
       }else {
           Intent intent = new Intent(this, DetailContrat.class);
           intent.putExtra("ID_Contrat", Id);
           intent.putExtra("NbJOUR", NbJour);
           startActivity(intent);
       }

    }

    @Override
    public void onBackPressed() {

        ;

    }
}
