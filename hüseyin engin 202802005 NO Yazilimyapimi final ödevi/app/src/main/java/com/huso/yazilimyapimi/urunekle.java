package com.huso.yazilimyapimi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;

public class urunekle extends AppCompatActivity {
    Spinner spinner;
    EditText miktar,kgfiyati;
    Button urunekle;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urunekle);

        spinner=findViewById(R.id.spinner_urunler);
        miktar=findViewById(R.id.miktar_kg);
        urunekle=findViewById(R.id.urunekle_buton);
        kgfiyati=findViewById(R.id.kgfiyati_edittext);

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        ArrayAdapter<String> myadapter=new ArrayAdapter<String>(urunekle.this, android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.names));
        myadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myadapter);

    }
    public void uruneklebutonu(View view){
        String urunler=spinner.getSelectedItem().toString();
        String miktartoplami=miktar.getText().toString();
        String kgfiyatmiktari=kgfiyati.getText().toString();
        String urunuekleyenkullanici=firebaseUser.getUid();

        firebaseFirestore.collection("Profiller").document(urunuekleyenkullanici).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot!=null) {
                    String ad = documentSnapshot.getString("ad");
                    String soyad = documentSnapshot.getString("soyad");
                    Double miktar = Double.parseDouble(miktartoplami);
                    Double kgfiyati = Double.parseDouble(kgfiyatmiktari);
                    if (ad != null && soyad != null) {
                        HashMap<String, Object> urundata = new HashMap();
                        urundata.put("urunler", urunler);
                        urundata.put("miktar", miktar);
                        urundata.put("urunekleyenkisi", urunuekleyenkullanici);
                        urundata.put("kgfiyati", kgfiyati);
                        firebaseFirestore.collection("Urunler").add(urundata).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(urunekle.this, "Urun Onaylanmak Icın Gonderilmistir.", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(urunekle.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Toast.makeText(urunekle.this, "!!!!!Profil bilgileriniz bos lütfen duzenleyiniz!!!!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}