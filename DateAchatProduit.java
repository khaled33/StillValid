package com.stillvalid.asus.stillvalid;

import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.ActionMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.stillvalid.asus.stillvalid.MesProduit.C_list;
import static com.stillvalid.asus.stillvalid.MesProduit.P_list;

public class DateAchatProduit extends AppCompatActivity {

    EditText Date_Achat;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date;
    DatePickerDialog picker;
    static final int CODE = 100;
    Boolean ValideDate=true;
    String day;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_achat_produit);

        prefs = getApplicationContext().getSharedPreferences("Produit", MODE_PRIVATE);
        editor = prefs.edit();
        Date_Achat = findViewById(R.id.date_achat);

        String restoreddate = prefs.getString("Date_Achat", null);
        if (restoreddate != null) {
            Date_Achat.setText(restoreddate);
        }
    }
    public void vocale(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.FRANCE);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Dis quelquechose!");
        try {
            startActivityForResult(intent, CODE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Pardon! Votre appareil ne prend pas en charge le langage vocal!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CODE:
                if (resultCode == RESULT_OK && data != null) {

                    ArrayList<String> listResult = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Date_Achat.setText(listResult.get(0));

                    try {
                        Calendar cldr = Calendar.getInstance();
                        String myFormat = "dd MMMM yyyy"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
                        cldr.setLenient(false);
                        cldr.setTime(sdf.parse(listResult.get(0)));
                         day = String.valueOf(Date_Achat.getText()).substring(0, 2);

                        if ( (day.equals("0 "))  ){

                            ValideDate=false;
                        } else if (!day.equals("0 ")){
                            int valideday= Integer.parseInt(day.trim());
                            if (valideday>31){
                                ValideDate=false;
                            }else ValideDate=true;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();

                        ValideDate=false;
                    }
                }
                break;
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

                        updateLabel();
                        ValideDate=true;
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

    public void Activite_suivant(View view) {
        String date = Date_Achat.getText().toString();
        if (!date.isEmpty()) {
                if (ValideDate) {
                    editor.putString("Date_Achat", date);
                    editor.apply();
                    startActivity(new Intent(this, DureeGarantieProduit.class));
                }else {
                    Date_Achat.setError(("Date Invalide"));
                    Toast.makeText(this, "Date Invalide (ex: 01 janvier 2019)", Toast.LENGTH_SHORT).show();

                }
        } else {
            Date_Achat.setError(getString(R.string.champs_obligatoir));
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
                        startActivity(new Intent(DateAchatProduit.this, MesProduit.class));
                        return true;
                    case R.id.dropdown_menu2:
                        startActivity(new Intent(DateAchatProduit.this, AjoutProduit.class));
                        return true;
                    case R.id.dropdown_menu3:
                        startActivity(new Intent(getApplicationContext(), BoutiqueActivity.class));
                        return true;
                    case R.id.dropdown_menu4:
                        SharedPreferences.Editor prefes = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE).edit();
                        prefes.putInt("Connexion", 0).apply();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finishAffinity();
                        P_list.clear();
                        C_list.clear();
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
        startActivity(new Intent(this, NomProduit.class));
    }

    public void effacer(View view) {
        Date_Achat.setText("");
    }
}
