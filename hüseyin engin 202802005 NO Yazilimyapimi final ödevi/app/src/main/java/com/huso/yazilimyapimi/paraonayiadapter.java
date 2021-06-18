package com.huso.yazilimyapimi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;

public class paraonayiadapter extends RecyclerView.Adapter<paraonayiadapter.Postpara> {
    ArrayList<Double> paraarray;
    ArrayList<String> parakullaniciarray;
    FirebaseFirestore firebaseFirestore;
    String sayfadakikisi;
    ArrayList<String> paraidarray;

    public paraonayiadapter(ArrayList<Double> paraarray, ArrayList<String> parakullaniciarray,FirebaseFirestore firebaseFirestore,String sayfadakikisi,ArrayList<String> paraidarray) {
        this.paraarray = paraarray;
        this.parakullaniciarray = parakullaniciarray;
        this.firebaseFirestore=firebaseFirestore;
        this.sayfadakikisi=sayfadakikisi;
        this.paraidarray=paraidarray;

    }

    @NonNull
    @Override
    public Postpara onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.paraonayi_recyclerview,parent,false);
        return new paraonayiadapter.Postpara(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Postpara holder, int position) {
        firebaseFirestore.collection("Profiller").document(parakullaniciarray.get(position)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot!=null) {
                    String ad = documentSnapshot.getString("ad");
                    String soyad = documentSnapshot.getString("soyad");
                    String paraadsoyad = ad + " " + soyad;
                    holder.paraisimsoyisim.setText("  " + paraadsoyad);
                }
            }
        });
        holder.paramiktari.setText("      "+"Miktar : "+paraarray.get(position)+ " " + "TL");

        holder.paraekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference documentReference=firebaseFirestore.collection("Eklenenpara").document(parakullaniciarray.get(position));
                HashMap<String,Object> eklenenparadata=new HashMap<>();
                eklenenparadata.put("paramiktar",paraarray.get(position));
                eklenenparadata.put("kullaniciid",parakullaniciarray.get(position));
                documentReference.set(eklenenparadata).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        firebaseFirestore.collection("Para").document(paraidarray.get(position)).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });
        holder.parasil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Para").document(paraidarray.get(position)).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return paraarray.size();
    }

    class Postpara extends RecyclerView.ViewHolder{
        TextView paraisimsoyisim,paramiktari;
        ImageView paraekle,parasil;

        public Postpara(@NonNull View itemView) {
            super(itemView);

            paraisimsoyisim=itemView.findViewById(R.id.paraisimsoyisim_textview);
            paramiktari=itemView.findViewById(R.id.paramiktari_textview);
            paraekle=itemView.findViewById(R.id.paraekle_imageview);
            parasil=itemView.findViewById(R.id.parasil_imageview);


        }
    }
}
