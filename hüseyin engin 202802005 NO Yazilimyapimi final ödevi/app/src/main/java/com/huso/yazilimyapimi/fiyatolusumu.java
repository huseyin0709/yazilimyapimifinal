package com.huso.yazilimyapimi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class fiyatolusumu extends AppCompatActivity {
    TextView hesaptakiparamiktari;
    RecyclerView fiyatolusumurecyclerview;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    fiyatolusumadapter fiyatolusumadapter;
    ArrayList<String> urunidarray;
    ArrayList<Double> miktaridarray;
    ArrayList<Double> kgfiyatiarray;
    ArrayList<String> kullaniciidarray;
    ArrayList<String> eklenenurunidarray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fiyatolusumu);

        hesaptakiparamiktari=findViewById(R.id.hesaptakiparamiktari_textview);
        fiyatolusumurecyclerview=findViewById(R.id.fiyatolusumu_recyclerview);

        urunidarray=new ArrayList<String>();
        miktaridarray=new ArrayList<Double>();
        kgfiyatiarray=new ArrayList<Double>();
        kullaniciidarray=new ArrayList<String>();
        eklenenurunidarray=new ArrayList<String>();

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();


        firebaseFirestore.collection("Eklenenpara").document(firebaseUser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot!=null) {
                    Double paramiktari = documentSnapshot.getDouble("paramiktar");
                    if (paramiktari != null) {
                        if (paramiktari > 0) {
                            hesaptakiparamiktari.setText(paramiktari + " " + "TL");
                        } else {
                            hesaptakiparamiktari.setText("0" + " " + "TL");
                        }
                    } else {
                        hesaptakiparamiktari.setText("0" + " " + "TL");
                    }

                    firebaseFirestore.collection("Eklenenurunler").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                            urunidarray.clear();
                            miktaridarray.clear();
                            kgfiyatiarray.clear();
                            if (querySnapshot != null) {
                                for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {
                                    Map<String, Object> urundata = snapshot.getData();
                                    String urun = (String) urundata.get("urunler");
                                    Double miktar = (Double) urundata.get("miktar");
                                    Double kgfiyati = (Double) urundata.get("kgfiyati");
                                    String kullanici = (String) urundata.get("kullaniciid");
                                    String eklenurunlerid = snapshot.getId();
                                    String sayfadakikullanici = firebaseUser.getUid();


                                    urunidarray.add(urun);
                                    miktaridarray.add(miktar);
                                    kgfiyatiarray.add(kgfiyati);
                                    kullaniciidarray.add(kullanici);
                                    eklenenurunidarray.add(eklenurunlerid);


                                    fiyatolusumurecyclerview.setLayoutManager(new LinearLayoutManager(fiyatolusumu.this));
                                    fiyatolusumadapter = new fiyatolusumadapter(urunidarray, miktaridarray, kgfiyatiarray, kullaniciidarray, firebaseFirestore, sayfadakikullanici, eklenenurunidarray, paramiktari);
                                    fiyatolusumurecyclerview.setAdapter(fiyatolusumadapter);
                                    fiyatolusumadapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
                }



            }
        });

    }
    public void almakistediginmiktarbutonu(View view){
        almakistediklerindialog almakistediklerindialog=new almakistediklerindialog(firebaseFirestore,firebaseUser,firebaseAuth);
        almakistediklerindialog.show(getSupportFragmentManager(),"istekler dialog");
    }
}