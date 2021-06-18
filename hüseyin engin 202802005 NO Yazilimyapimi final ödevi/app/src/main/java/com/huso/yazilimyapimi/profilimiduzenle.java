package com.huso.yazilimyapimi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class profilimiduzenle extends AppCompatActivity {
    EditText adedittext,soyadedittext,tcedittext,kullaniciadiedittext,telefonedittext,adresedittext;
    Button profilkaydetbutton;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilimiduzenle);

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        adedittext=findViewById(R.id.ad_edittext);
        soyadedittext=findViewById(R.id.soyad_edittext);
        tcedittext=findViewById(R.id.tc_edittext);
        kullaniciadiedittext=findViewById(R.id.kullaniciadi_edittext);
        telefonedittext=findViewById(R.id.telefon_edittext);
        adresedittext=findViewById(R.id.adres_edittext);
        profilkaydetbutton=findViewById(R.id.profilkaydet_button);


    }
    public void profilkaydetbutonu(View view){
        String ad=adedittext.getText().toString();
        String soyad=soyadedittext.getText().toString();
        String tc=tcedittext.getText().toString();
        String kullaniciadi=kullaniciadiedittext.getText().toString();
        String telefon=telefonedittext.getText().toString();
        String adres=adresedittext.getText().toString();
        String kaydedenkisi=firebaseUser.getUid();

        DocumentReference documentReference=firebaseFirestore.collection("Profiller").document(kaydedenkisi);
        HashMap<String,Object> profildata=new HashMap<>();
        profildata.put("ad",ad);
        profildata.put("soyad",soyad);
        profildata.put("tc",tc);
        profildata.put("kullaniciadi",kullaniciadi);
        profildata.put("telefon",telefon);
        profildata.put("adres",adres);
        profildata.put("kaydedenkisi",kaydedenkisi);
        documentReference.set(profildata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(profilimiduzenle.this,"Basari ile kaydedildi..",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(profilimiduzenle.this,anasayfa.class);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(profilimiduzenle.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}