package com.huso.yazilimyapimi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class onayislemleri extends AppCompatActivity {
    TabLayout onaytablayout;
    RecyclerView urunonayrecyclerview,paraonayrecyclerview;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    urunonayiadapter urunonayiadapter;
    paraonayiadapter paraonayiadapter;
    ArrayList<String> urunarray;
    ArrayList<Double> miktararray;
    ArrayList<String> kullaniciarray;
    ArrayList<Double> kgfiyatiarray;
    ArrayList<String> Adsoyadarray;
    ArrayList<String> urunidarray;
    ArrayList<Double> paraarray;
    ArrayList<String> parakullaniciarray;
    ArrayList<String> paraAdsoyadarray;
    ArrayList<String> paraidarray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onayislemleri);

        onaytablayout=findViewById(R.id.onay_tablayout);
        onaytablayout.addTab(onaytablayout.newTab().setText("Urun Onayi"));
        onaytablayout.addTab(onaytablayout.newTab().setText("Para Onayi"));
        urunonayrecyclerview=findViewById(R.id.fiyatolusumu_recyclerview);
        paraonayrecyclerview=findViewById(R.id.paraonay_recyclerView);
        urunarray=new ArrayList<String>();
        miktararray=new ArrayList<Double>();
        kullaniciarray=new ArrayList<String>();
        Adsoyadarray=new ArrayList<String>();
        paraarray=new ArrayList<Double>();
        parakullaniciarray=new ArrayList<String>();
        paraAdsoyadarray=new ArrayList<String>();
        urunidarray=new ArrayList<String>();
        paraidarray=new ArrayList<String>();
        kgfiyatiarray=new ArrayList<Double>();


        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        paraonayrecyclerview.setVisibility(View.INVISIBLE);
        onaytablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition()==0){
                    urunonayrecyclerview.setVisibility(View.VISIBLE);
                    paraonayrecyclerview.setVisibility(View.INVISIBLE);

                }
                else if(tab.getPosition()==1){
                    urunonayrecyclerview.setVisibility(View.INVISIBLE);
                    paraonayrecyclerview.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        urunler(firebaseFirestore,firebaseUser);
        para(firebaseFirestore,firebaseUser);
    }

    public void urunler(FirebaseFirestore firebaseFirestore,FirebaseUser firebaseUser){
        firebaseFirestore.collection("Urunler").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                urunarray.clear();
                miktararray.clear();
                if (querySnapshot!=null) {
                    for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {
                        Map<String,Object> urundata=snapshot.getData();
                        String urunler=(String) urundata.get("urunler");
                        Double miktar=(Double) urundata.get("miktar");
                        String urunekleyenkullanici=(String) urundata.get("urunekleyenkisi");
                        Double kgfiyati=(Double) urundata.get("kgfiyati");
                        String urunidler=snapshot.getId();
                        String sayfadakikisi=firebaseUser.getUid();

                        urunarray.add(urunler);
                        miktararray.add(miktar);
                        kullaniciarray.add(urunekleyenkullanici);
                        urunidarray.add(urunidler);
                        kgfiyatiarray.add(kgfiyati);

                        urunonayrecyclerview.setLayoutManager(new LinearLayoutManager(onayislemleri.this));
                        urunonayiadapter = new urunonayiadapter(urunarray, miktararray, kullaniciarray,firebaseFirestore,sayfadakikisi,urunidarray,kgfiyatiarray);
                        urunonayrecyclerview.setAdapter(urunonayiadapter);
                        urunonayiadapter.notifyDataSetChanged();

                    }
                }
            }
        });
    }
    public void para(FirebaseFirestore firebaseFirestore,FirebaseUser firebaseUser){
        firebaseFirestore.collection("Para").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                paraarray.clear();
                if (querySnapshot!=null) {
                    for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {
                        Map<String,Object> paradata=snapshot.getData();
                        Double para=(Double) paradata.get("para");
                        String paraekleyenkullanici=(String) paradata.get("paraekleyenkisi");
                        String paraidler=snapshot.getId();
                        paraarray.add(para);
                        parakullaniciarray.add(paraekleyenkullanici);
                        paraidarray.add(paraidler);
                        String sayfadakikisi=firebaseUser.getUid();

                        paraonayrecyclerview.setLayoutManager(new LinearLayoutManager(onayislemleri.this));
                        paraonayiadapter = new paraonayiadapter(paraarray,parakullaniciarray,firebaseFirestore,sayfadakikisi,paraidarray);
                        paraonayrecyclerview.setAdapter(paraonayiadapter);
                        paraonayiadapter.notifyDataSetChanged();


                    }
                }
            }
        });

    }
}