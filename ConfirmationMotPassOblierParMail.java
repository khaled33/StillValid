package com.stillvalid.asus.stillvalid;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.stillvalid.asus.stillvalid.Models.MailBody;
import com.stillvalid.asus.stillvalid.Models.SendMail;

import java.util.Random;

public class ConfirmationMotPassOblierParMail extends AppCompatActivity {
EditText Codedeverification;
Context context;
String mail,MotPassTemporer,code_de_verification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_mot_pass_oblier_par_mail);
        Codedeverification=findViewById(R.id.Codedeverification);
        context=this;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mail = bundle.getString("Email");
            MotPassTemporer = bundle.getString("MotPassTemporer");
        }



    }

    public void ValiderMotdePasse(View view) {
        code_de_verification=Codedeverification.getText().toString().trim();
        if(!code_de_verification.equals("")){
            if (code_de_verification.equals(MotPassTemporer)){

                Intent intent = new Intent(getApplicationContext(), MotDePassOblie.class);
                intent.putExtra("Email", mail);
                startActivity(intent);
            }else {
                Codedeverification.setError("Code Invalide");
            }
        }else {
            Codedeverification.setError(getString(R.string.champs_obligatoir));
        }


    }
}
