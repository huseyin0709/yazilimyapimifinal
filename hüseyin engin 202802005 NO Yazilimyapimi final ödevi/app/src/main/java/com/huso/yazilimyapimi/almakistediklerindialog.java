package com.huso.yazilimyapimi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;

public class almakistediklerindialog extends AppCompatDialogFragment {
    Spinner urunlerspinner;
    EditText almakistediginmiktar;
    EditText almakistediginkgfiyati;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;

    public almakistediklerindialog(FirebaseFirestore firebaseFirestore, FirebaseUser firebaseUser, FirebaseAuth firebaseAuth) {
        this.firebaseFirestore = firebaseFirestore;
        this.firebaseUser = firebaseUser;
        this.firebaseAuth = firebaseAuth;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {//alertdialog olusturuyorum ve istediklerimizi sisteme giriyoruz ve beklemeye aliniyor
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.almakistediginmiktaralertdialog,null);
        builder.setView(view)
                .setTitle("Lutfen almak istediginiz urun bilgileri giriniz")
                .setNegativeButton("Onaylamiyorum", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("Onayliyorum", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String urunler=urunlerspinner.getSelectedItem().toString();
                String miktar=almakistediginmiktar.getText().toString();
                String kgfiyati=almakistediginkgfiyati.getText().toString();

                Double istenilenmiktar=Double.parseDouble(miktar);
                Double istenilenfiyat=Double.parseDouble(kgfiyati);
                String kullaniciid=firebaseUser.getUid();

                HashMap<String,Object> almakistenendata=new HashMap<>();
                almakistenendata.put("almakistenenurunler",urunler);
                almakistenendata.put("almakistenenmiktar",istenilenmiktar);
                almakistenendata.put("almakistenenkgfiyati",istenilenfiyat);
                firebaseFirestore.collection("almakistenenler").document(kullaniciid).collection("alinanlar").add(almakistenendata).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {


                    }
                });

            }
        });
        urunlerspinner=view.findViewById(R.id.almakistediginurun_spinner);
        ArrayAdapter urunadapter=ArrayAdapter.createFromResource(getContext(),R.array.names, android.R.layout.simple_spinner_item);
        urunadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        urunlerspinner.setAdapter(urunadapter);
        almakistediginmiktar=view.findViewById(R.id.almakistedigimizmiktar_edittext);
        almakistediginkgfiyati=view.findViewById(R.id.almakistediginizkgfiyati_edittext);
        return builder.create();


    }
}
