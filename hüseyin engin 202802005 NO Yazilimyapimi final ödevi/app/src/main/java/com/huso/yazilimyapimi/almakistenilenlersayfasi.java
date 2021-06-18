package com.huso.yazilimyapimi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class almakistenilenlersayfasi extends AppCompatActivity {
    RecyclerView almakistenenlerrecyclerview;
    almakistenilenleradapter almakistenilenleradapter;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    ArrayList<String> almakistenenurunleridarray;
    ArrayList<Double> almakistenenmiktarlaridarray;
    ArrayList<Double> almakistenenkgfiyatlariidarray;
    ArrayList<String> almakistenenleridarray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {//gerekli tanimlari yapiyoruz
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_almakistenilenlersayfasi);

        almakistenenlerrecyclerview=findViewById(R.id.almakistenilenler_recyclerview);
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        almakistenenleridarray=new ArrayList<String>();
        almakistenenurunleridarray=new ArrayList<String>();
        almakistenenmiktarlaridarray=new ArrayList<Double>();
        almakistenenkgfiyatlariidarray=new ArrayList<Double>();


        almakistenenler(almakistenenurunleridarray,almakistenenmiktarlaridarray,almakistenenkgfiyatlariidarray,firebaseFirestore,firebaseUser);//asagidaki fonksiyona bilgileri gonderiyoruz
    }
    //almak istediklerimizi firebaseden cekiyoruz almak istedilerimiz recyclerview a gonderiyoruz orada eklenenurunlerdeki bilgilerle almakistediklerimiz kontrol edilecek
    public void almakistenenler(ArrayList<String> almakistenenurunleridarray, ArrayList<Double> almakistenenmiktarlaridarray, ArrayList<Double> almakistenenkgfiyatlariidarray,FirebaseFirestore firebaseFirestore,FirebaseUser firebaseUser){
        firebaseFirestore.collection("almakistenenler").document(firebaseUser.getUid()).collection("alinanlar").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                if (querySnapshot!=null) {
                    for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {
                        Map<String,Object> urundata=snapshot.getData();
                        String almakistenenurunler=(String) urundata.get("almakistenenurunler");
                        Double almakistenenmiktar=(Double) urundata.get("almakistenenmiktar");
                        Double almakistenenkgfiyati=(Double) urundata.get("almakistenenkgfiyati");
                        String almakistenenidler=snapshot.getId();



                        almakistenenurunleridarray.add(almakistenenurunler);
                        almakistenenmiktarlaridarray.add(almakistenenmiktar);
                        almakistenenkgfiyatlariidarray.add(almakistenenkgfiyati);
                        almakistenenleridarray.add(almakistenenidler);


                        firebaseFirestore.collection("Eklenenurunler").addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot querySnapshot1, @Nullable FirebaseFirestoreException error) {
                                if (querySnapshot1!=null) {
                                    for (DocumentSnapshot snapshot : querySnapshot1.getDocuments()) {
                                        Map<String, Object> urunbilgileridata = snapshot.getData();
                                        String urun = (String) urunbilgileridata.get("urunler");
                                        Double miktar = (Double) urunbilgileridata.get("miktar");
                                        Double kgfiyati = (Double) urunbilgileridata.get("kgfiyati");
                                        String idler = snapshot.getId();

                                        firebaseFirestore.collection("Eklenenpara").document(firebaseUser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                                                if (documentSnapshot!=null) {
                                                    Double para = documentSnapshot.getDouble("paramiktar");
                                                    if (documentSnapshot != null) {
                                                        almakistenenlerrecyclerview.setLayoutManager(new LinearLayoutManager(almakistenilenlersayfasi.this));
                                                        almakistenilenleradapter = new almakistenilenleradapter(almakistenenurunleridarray, almakistenenmiktarlaridarray, almakistenenkgfiyatlariidarray, firebaseFirestore, firebaseUser, almakistenenleridarray, urun, miktar, kgfiyati, idler, para, almakistenenurunler);
                                                        almakistenenlerrecyclerview.setAdapter(almakistenilenleradapter);
                                                        almakistenilenleradapter.notifyDataSetChanged();
                                                    }
                                                }

                                            }
                                        });
                                    }
                                }
                            }
                        });

                    }
                }
            }
        });


    }
}