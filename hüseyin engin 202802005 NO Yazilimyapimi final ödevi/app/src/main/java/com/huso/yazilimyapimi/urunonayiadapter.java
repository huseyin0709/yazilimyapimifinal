package com.huso.yazilimyapimi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

public class urunonayiadapter extends RecyclerView.Adapter<urunonayiadapter.Posturun> {
    ArrayList<String> urunarray;
    ArrayList<Double> miktararray;
    ArrayList<String> kullaniciarray;
    FirebaseFirestore firebaseFirestore;
    String sayfadakikisi;
    ArrayList<String> urunidarray;
    ArrayList<Double> kgfiyatiarray;

    public urunonayiadapter(ArrayList<String> urunarray, ArrayList<Double> miktararray, ArrayList<String> kullaniciarray,FirebaseFirestore firebaseFirestore,String sayfadakikisi, ArrayList<String> urunidarray,ArrayList<Double> kgfiyatiarray) {
        this.urunarray = urunarray;
        this.miktararray = miktararray;
        this.kullaniciarray = kullaniciarray;
        this.firebaseFirestore=firebaseFirestore;
        this.sayfadakikisi=sayfadakikisi;
        this.urunidarray = urunidarray;
        this.kgfiyatiarray = kgfiyatiarray;
    }

    @NonNull
    @Override
    public Posturun onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.urunonayi_recyclerview,parent,false);
        return new urunonayiadapter.Posturun(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Posturun holder, int position) {
        firebaseFirestore.collection("Profiller").document(kullaniciarray.get(position)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot!=null) {
                    String ad = documentSnapshot.getString("ad");
                    String soyad = documentSnapshot.getString("soyad");
                    String urunadsoyad = ad + " " + soyad;
                    holder.isimsoyisim.setText("  " + urunadsoyad);
                }
            }
        });

        holder.urun.setText("      "+"URUNLER : "+urunarray.get(position));
        holder.miktar.setText("      "+"Miktar : "+miktararray.get(position)+ " " + "KG");
        holder.kgfiyati.setText("      "+"KG Fiyatı : "+kgfiyatiarray.get(position)+ " "+"TL" );

        holder.ekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference documentReference=firebaseFirestore.collection("Eklenenurunler").document(urunidarray.get(position));
                HashMap<String,Object> eklenenurundata=new HashMap<>();
                eklenenurundata.put("urunler",urunarray.get(position));
                eklenenurundata.put("miktar",miktararray.get(position));
                eklenenurundata.put("kullaniciid",kullaniciarray.get(position));
                eklenenurundata.put("kgfiyati",kgfiyatiarray.get(position));
                documentReference.set(eklenenurundata).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        firebaseFirestore.collection("Urunler").document(urunidarray.get(position)).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
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

        holder.sil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Urunler").document(urunidarray.get(position)).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
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
        return urunarray.size();
    }

    class Posturun extends RecyclerView.ViewHolder{
        TextView isimsoyisim,urun,miktar,kgfiyati;
        ImageView ekle,sil;

        public Posturun(@NonNull View itemView) {
            super(itemView);
            isimsoyisim=itemView.findViewById(R.id.isimsoyisim_textview);
            urun=itemView.findViewById(R.id.urun_textview);
            miktar=itemView.findViewById(R.id.miktar_textview);
            kgfiyati=itemView.findViewById(R.id.kgfiyati_textview);
            ekle=itemView.findViewById(R.id.ekle_imageview);
            sil=itemView.findViewById(R.id.sil_imageview);

        }
    }
}
