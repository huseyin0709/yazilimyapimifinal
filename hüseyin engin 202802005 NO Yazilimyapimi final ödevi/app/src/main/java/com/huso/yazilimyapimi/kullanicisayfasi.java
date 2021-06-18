package com.huso.yazilimyapimi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class kullanicisayfasi extends AppCompatActivity {
    EditText kullanicigiris,sifregiris;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kullanicisayfasi);

        kullanicigiris=findViewById(R.id.kullaniciadigiris_edittext);
        sifregiris=findViewById(R.id.sifregiris_edittext);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        if (firebaseUser!=null){
            Intent intent=new Intent(getApplicationContext(),anasayfa.class);
            startActivity(intent);
            finish();
        }

    }
    public void girisyapbutonu(View view){
        String kullaniciadi=kullanicigiris.getText().toString();
        String sifre=sifregiris.getText().toString();
        if (kullaniciadi.matches("")||sifre.matches("")){
            Toast.makeText(kullanicisayfasi.this,"Bos Alan Mevcut..", Toast.LENGTH_LONG).show();
        }
        else{
            firebaseAuth.signInWithEmailAndPassword(kullaniciadi,sifre).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Toast.makeText(kullanicisayfasi.this, "Giriş Başarılı", Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(kullanicisayfasi.this,anasayfa.class);
                    startActivity(intent);
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(kullanicisayfasi.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }

    }
    public void hesapolusturbutonu(View view){
        Intent intent=new Intent(kullanicisayfasi.this,hesapolustur.class);
        startActivity(intent);
    }
}