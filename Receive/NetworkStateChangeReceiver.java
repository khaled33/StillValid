package com.stillvalid.asus.stillvalid.Receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.stillvalid.asus.stillvalid.Activation;
import com.stillvalid.asus.stillvalid.Home;
import com.stillvalid.asus.stillvalid.InscriptionActivity;
import com.stillvalid.asus.stillvalid.LoginActivity;

public class NetworkStateChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (isOnline(context)) {
                switch (LoginActivity.NB_Activity) {
                    case 0:
                        LoginActivity.Alert_dialog(true, context);
                        break;
                    case 1:
                        InscriptionActivity.Alert_dialog(true, context);
                        break;
                    case 2:
                        Activation.Alert_dialog(true, context);
                        break;
                    case 3:
                        Home.Alert_dialog(true, context);
                        break;
                }
            } else {
                switch (LoginActivity.NB_Activity) {
                    case 0:
                        LoginActivity.Alert_dialog(false, context);
                        break;
                    case 1:
                        InscriptionActivity.Alert_dialog(false, context);
                        break;
                    case 2:
                        Activation.Alert_dialog(false, context);
                        break;
                    case 3:
                        Home.Alert_dialog(false, context);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }


}
