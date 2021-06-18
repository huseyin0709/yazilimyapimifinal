package com.huso.yazilimyapimi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class anasayfa extends AppCompatActivity {
private ImageView onayimageview;
private FirebaseAuth firebaseAuth;
private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anasayfa);

        onayimageview=findViewById(R.id.onay_imageview);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        onayimageview.setVisibility(View.INVISIBLE);

        adminkullanicisi(firebaseUser);

    }
    public void adminkullanicisi(FirebaseUser firebaseUser){
        if (firebaseUser.getUid().matches("rhKrVE4xprYoxMXST5jxiyBfz753")) {
            onayimageview.setVisibility(View.VISIBLE);
            onayimageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentonay=new Intent(anasayfa.this,onayislemleri.class);
                    startActivity(intentonay);
                }
            });
        }
    }

    public void profilsayfasi(View view){
        Intent intentprofilegit=new Intent(anasayfa.this,profilimiduzenle.class);
        startActivity(intentprofilegit);
    }
    public void uruneklesayfasi(View view){
        Intent intenturunekle=new Intent(anasayfa.this,urunekle.class);
        startActivity(intenturunekle);
    }
    public void paraeklesayfasi(View view){
        Intent intentparaekle=new Intent(anasayfa.this,paraekle.class);
        startActivity(intentparaekle);
    }
    public void fiyatlarsayfasi(View view){
        Intent intentfiyatolusumu=new Intent(anasayfa.this,fiyatolusumu.class);
        startActivity(intentfiyatolusumu);
    }
    public void raporalsayfasi(View view){
        Intent intentraporal=new Intent(anasayfa.this,raporal.class);
        startActivity(intentraporal);
    }
    public void almakistenilenlersayfasi(View view){
        Intent intentalmakistenilenler=new Intent(anasayfa.this,almakistenilenlersayfasi.class);
        startActivity(intentalmakistenilenler);
    }

    public void cikissayfasi(View view){
        AlertDialog.Builder builder=new AlertDialog.Builder(anasayfa.this);
        builder.setTitle("ÇIKIŞ YAP");
        builder.setMessage("Çıkış Yapmak İstermisiniz ?");
        builder.setNegativeButton("HAYIR",null);
        builder.setPositiveButton("EVET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                firebaseAuth.signOut();
                Intent intenttosignup=new Intent(anasayfa.this,kullanicisayfasi.class);
                startActivity(intenttosignup);
                finish();
            }
        });
        builder.show();
    }


}