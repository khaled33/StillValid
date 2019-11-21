package com.stillvalid.asus.stillvalid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.stillvalid.asus.stillvalid.Models.Config_URL;
import com.stillvalid.asus.stillvalid.Models.Contrats;
import com.stillvalid.asus.stillvalid.Receive.NetworkStateChangeReceiver;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Calendar;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

import static com.stillvalid.asus.stillvalid.MesProduit.C_list;

public class Home extends AppCompatActivity {

    TextView date;
    int YEAR;
    BroadcastReceiver mNetworkReceiver;
    static ACProgressFlower dialoge;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        LoginActivity.NB_Activity=3;
        mNetworkReceiver = new NetworkStateChangeReceiver();
        registerNetworkBroadcastForNougat();

        date=findViewById(R.id.date);
        Calendar rightNow = Calendar.getInstance();
        YEAR=rightNow.get(Calendar.YEAR);
        date.setText("All Rights Reserved - "+YEAR);

    }

    public void ajout_produit(View view) {
        Intent intent=new Intent(this,AjoutProduit.class);
        startActivity(intent);

    }

    public void mes_Produit(View view) {
        Intent intent=new Intent(this,MesProduit.class);
        startActivity(intent);
    }

    public void Boutique(View view) {
        Intent intent=new Intent(this,BoutiqueActivity.class);
        startActivity(intent);
    }
    public void VoirReparateur(View view) {

//        startActivity(new Intent(this, Reparateur.class));
    }
    //Methode de test connexion
    public static void Alert_dialog(boolean value, Context context) {

        if (value) {
            context=null;
            dialoge.dismiss();
        } else {
            dialoge = new ACProgressFlower.Builder(context)
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .themeColor(Color.WHITE)
                    .text("Access Denied...").textColor(Color.WHITE)
                    .fadeColor(Color.TRANSPARENT).build();
            dialoge.show();
        }
    }
    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
       ;

    }
}
