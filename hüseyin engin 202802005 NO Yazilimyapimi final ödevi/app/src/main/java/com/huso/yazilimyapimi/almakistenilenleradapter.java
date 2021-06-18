package com.huso.yazilimyapimi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class almakistenilenleradapter extends RecyclerView.Adapter<almakistenilenleradapter.Postalmakistenilenler> {
    ArrayList<String> almakistenenurunleridarray;
    ArrayList<Double> almakistenenmiktarlaridarray;
    ArrayList<Double> almakistenenkgfiyatlariidarray;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    ArrayList<String> almakistenenleridarray;
    String eklenenurun;
    Double eklenenmiktar;
    Double eklenenkgfiyati;
    Double para;
    String eklenenidler;
    String almakistenenurun;

//constructor ile diger sayfadan gonderilenler bu sayfada cekiyoruz
    public almakistenilenleradapter(ArrayList<String> almakistenenurunleridarray,ArrayList<Double> almakistenenmiktarlaridarray,ArrayList<Double> almakistenenkgfiyatlariidarray,FirebaseFirestore firebaseFirestore,FirebaseUser firebaseUser,ArrayList<String> almakistenenleridarray,String eklenenurun,Double eklenenmiktar,Double eklenenkgfiyati,String eklenenidler,Double para,String almakistenenurun) {
        this.almakistenenurunleridarray = almakistenenurunleridarray;
        this.almakistenenmiktarlaridarray = almakistenenmiktarlaridarray;
        this.almakistenenkgfiyatlariidarray = almakistenenkgfiyatlariidarray;
        this.firebaseFirestore = firebaseFirestore;
        this.firebaseUser = firebaseUser;
        this.almakistenenleridarray=almakistenenleridarray;
        this.eklenenurun=eklenenurun;
        this.eklenenmiktar=eklenenmiktar;
        this.eklenenkgfiyati=eklenenkgfiyati;
        this.eklenenidler=eklenenidler;
        this.para=para;
        this.almakistenenurun=almakistenenurun;

    }

    @NonNull
    @Override
    public Postalmakistenilenler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());//ayarladigimz recyclerviewin tanimini yapiyoruz
        View view=layoutInflater.inflate(R.layout.almakistenen_recyclerview,parent,false);
        return new almakistenilenleradapter.Postalmakistenilenler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Postalmakistenilenler holder, int position) {//aldigimiz bilgileri recyclerviewdakilara entegre ediyoruz
        holder.almakistenenurun.setText("      "+"URUNLER : "+almakistenenurunleridarray.get(position));
        holder.almakistenenmiktar.setText("      "+"Miktar : "+almakistenenmiktarlaridarray.get(position)+" "+"KG");
        holder.almakistenenkgfiyati.setText("      "+"KG Fiyatı : "+almakistenenkgfiyatlariidarray.get(position)+" "+"TL");
        holder.guncellebutonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eklenenurun.matches(almakistenenurun) && almakistenenmiktarlaridarray.get(position)<=eklenenmiktar && almakistenenkgfiyatlariidarray.get(position)<=eklenenkgfiyati){//istediklerimiz fiyat olusumundaki ile kontol ediyoruz
                    Double yenimiktar = eklenenmiktar - almakistenenmiktarlaridarray.get(position);
                    if (yenimiktar>0){
                        firebaseFirestore.collection("Eklenenurunler").document(eklenenidler).update("miktar",yenimiktar).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                firebaseFirestore.collection("almakistenenler").document(firebaseUser.getUid()).collection("alinanlar").document(almakistenenleridarray.get(position)).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                });
                            }
                        });
                        Double parasonuc = para - (almakistenenmiktarlaridarray.get(position) * almakistenenkgfiyatlariidarray.get(position)) - (almakistenenmiktarlaridarray.get(position) * almakistenenkgfiyatlariidarray.get(position))*1/100;//satin aldigimiz urunden muhasebe ucreti %1 kisinin parasindan cekiyoruz
                        if (parasonuc>0){
                            firebaseFirestore.collection("Eklenenpara").document(firebaseUser.getUid()).update("paramiktar",parasonuc).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });

                        }else {
                            firebaseFirestore.collection("Eklenenpara").document(firebaseUser.getUid()).update("paramiktar",0).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });
                        }

                    }
                    else {
                        firebaseFirestore.collection("Eklenenurunler").document(eklenenidler).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });
                    }
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return almakistenenurunleridarray.size();
    }

    class Postalmakistenilenler extends RecyclerView.ViewHolder{//recyclerviewdaki gerekli tanimlari yapiyoruz
        TextView almakistenenurun,almakistenenmiktar,almakistenenkgfiyati;
        Button guncellebutonu;
        ArrayList<String> idarray;
        ArrayList<Double> miktararray;
        public Postalmakistenilenler(@NonNull View itemView) {
            super(itemView);
            almakistenenurun=itemView.findViewById(R.id.almakisteneniurun_textview);
            almakistenenmiktar=itemView.findViewById(R.id.almakistenenmiktar_textview);
            almakistenenkgfiyati=itemView.findViewById(R.id.almakistenenkgfiyati_textview);
            guncellebutonu=itemView.findViewById(R.id.guncelle_butonu);
            idarray=new ArrayList<>();
            miktararray=new ArrayList<>();
        }
    }
}
