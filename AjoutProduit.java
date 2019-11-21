package com.stillvalid.asus.stillvalid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static com.stillvalid.asus.stillvalid.MesProduit.C_list;
import static com.stillvalid.asus.stillvalid.MesProduit.P_list;

public class AjoutProduit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_produit);
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
                        startActivity(new Intent(AjoutProduit.this, MesProduit.class));
                        return true;
                    case R.id.dropdown_menu2:
                        startActivity(new Intent(AjoutProduit.this, AjoutProduit.class));
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

    public void Ajout_Prod(View view) {
        resetvalueProduit();
        startActivity(new Intent(this, EnseigneAchat.class));
    }

    public void ajout_contrat(View view) {
        resetvalueContrat();
        startActivity(new Intent(this, TypeContrat.class));
    }

    public void resetvalueProduit() {
        SharedPreferences.Editor editor = getSharedPreferences("Produit", MODE_PRIVATE).edit();
        editor.remove("Enseigne");
        editor.remove("Marque");
        editor.remove("ID_Marque");
        editor.remove("Nom_Produit");
        editor.remove("Date_Achat");
        editor.remove("Duree_Garantie");
        editor.remove("Date_Mili");
        editor.remove("Date_Fin");
        editor.remove("Photo_Produit");
        editor.remove("Photo_Facture");
        editor.remove("SAV");
        editor.apply();
        AjoutPhotoProduit.bitmapFacture=null;
        AjoutPhotoProduit.bitmapArticle=null;
    }
    public void resetvalueContrat() {
        SharedPreferences.Editor editor = getSharedPreferences("Contrat", MODE_PRIVATE).edit();
        editor.remove("Types");
        editor.remove("Date_Echeance");
        editor.remove("ID_Type");
        editor.remove("Photo_Contrat");
        editor.putInt("Compteur", 10);
        editor.apply();
        AjouterPhotoContrat.listPhotoContarat.clear();
    }
}
