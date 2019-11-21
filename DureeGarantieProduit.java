package com.stillvalid.asus.stillvalid;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static com.stillvalid.asus.stillvalid.MesProduit.C_list;
import static com.stillvalid.asus.stillvalid.MesProduit.P_list;

public class DureeGarantieProduit extends AppCompatActivity {

    EditText Duree_Garantie;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String dateAchat;
    static final int CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duree_garantie_produit);

        prefs = getApplicationContext().getSharedPreferences("Produit", MODE_PRIVATE);
        editor = prefs.edit();
        Duree_Garantie = findViewById(R.id.dure_garantie);

        String restoredduree = prefs.getString("Duree_Garantie", null);
        String restoreddate = prefs.getString("Date_Achat", null);
        if (restoredduree != null) {
            Duree_Garantie.setText(restoredduree);
        }
        if (restoreddate != null) {
            dateAchat=restoreddate;
        }
    }

    public void vocale(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something!");
        try {
            startActivityForResult(intent, CODE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Sorry! Your device doesn't support speech language!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CODE:
                if (resultCode == RESULT_OK && data != null) {

                    ArrayList<String> listResult = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Duree_Garantie.setText(listResult.get(0));
                }
                break;
        }
    }

    public void Activite_suivant(View view) {
        String duree = Duree_Garantie.getText().toString();
        if (!duree.isEmpty()) {
            if (duree.matches("-?\\d+(.\\d+)?")) {
                Calendar cal = Calendar.getInstance();
                String myFormat = "dd MMMM yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
                try {
                    cal.setTime(sdf.parse(dateAchat));
                    cal.add(Calendar.MONTH, Integer.parseInt(duree));
                    editor.putString("Duree_Garantie", duree);
                    editor.putString("Date_Fin", sdf.format(cal.getTime()));
                    editor.apply();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(this, AjoutPhotoProduit.class));
            } else {
                Duree_Garantie.setError("Champ invalide");
            }
        } else {
            Duree_Garantie.setError(getString(R.string.champs_obligatoir));
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
                        startActivity(new Intent(DureeGarantieProduit.this, MesProduit.class));
                        return true;
                    case R.id.dropdown_menu2:
                        startActivity(new Intent(DureeGarantieProduit.this, AjoutProduit.class));
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
        startActivity(new Intent(this, DateAchatProduit.class));
    }

    public void effacer(View view) {
        Duree_Garantie.setText("");
    }
}
