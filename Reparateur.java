package com.stillvalid.asus.stillvalid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Reparateur extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reparateur);

    }


    public void RetourDetailProduit(View view) {
//        startActivity(new Intent(this, DetailProduit.class));
    }
}
