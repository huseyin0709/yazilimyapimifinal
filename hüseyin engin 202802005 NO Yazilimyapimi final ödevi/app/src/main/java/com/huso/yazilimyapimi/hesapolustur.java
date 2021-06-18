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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class hesapolustur extends AppCompatActivity {
    EditText emailedittext,sifreeditext;
    Button kaydetbutton;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hesapolustur);


        emailedittext=findViewById(R.id.email_edittext);
        sifreeditext=findViewById(R.id.sifre_edittext);
        kaydetbutton=findViewById(R.id.kaydet_button);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        firebaseFirestore=FirebaseFirestore.getInstance();
    }

    public void kaydetbutonu(View view){
        String email=emailedittext.getText().toString();
        String sifre=sifreeditext.getText().toString();

        if (email.matches("")||sifre.matches("")){
            Toast.makeText(hesapolustur.this,"Bos Alan Mevcut...",Toast.LENGTH_LONG).show();
        }else {
            firebaseAuth.createUserWithEmailAndPassword(email,sifre).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Toast.makeText(hesapolustur.this,"Başarı İle Kaydedildi..",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(hesapolustur.this,kullanicisayfasi.class);
                    startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(hesapolustur.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
            });


        }

    }


}