package com.huso.yazilimyapimi;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.xml.transform.Result;

public class fiyatolusumadapter extends RecyclerView.Adapter<fiyatolusumadapter.Postfiyat> {
    ArrayList<String> urunidarray;
    ArrayList<Double> miktaridarray;
    ArrayList<Double> kgfiyatiarray;
    ArrayList<String> kullaniciidarray;
    FirebaseFirestore firebaseFirestore;
    String sayfadakikullanici;
    Double paramiktari;
    ArrayList<String> eklenenurunidarray;

    public fiyatolusumadapter(ArrayList<String> urunidarray, ArrayList<Double> miktaridarray, ArrayList<Double> kgfiyatiarray, ArrayList<String> kullaniciidarray, FirebaseFirestore firebaseFirestore, String sayfadakikullanici,ArrayList<String> eklenenurunidarray,Double paramiktari) {
        this.urunidarray = urunidarray;
        this.miktaridarray = miktaridarray;
        this.kgfiyatiarray = kgfiyatiarray;
        this.kullaniciidarray = kullaniciidarray;
        this.firebaseFirestore = firebaseFirestore;
        this.sayfadakikullanici = sayfadakikullanici;
        this.eklenenurunidarray = eklenenurunidarray;
        this.paramiktari = paramiktari;
    }

    @NonNull
    @Override
    public Postfiyat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.fiyatolusumu_recyclerview,parent,false);
        return new fiyatolusumadapter.Postfiyat(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Postfiyat holder, int position) {
        holder.fiyattakiurun.setText("      "+"URUNLER : "+urunidarray.get(position));
        holder.fiyattakimiktar.setText("      "+"Miktar : "+miktaridarray.get(position)+" "+"KG");
        holder.fiyattakikgfiyati.setText("      "+"KG Fiyatı : "+kgfiyatiarray.get(position)+" "+"TL");
        holder.satinal.setVisibility(View.INVISIBLE);
        if (!sayfadakikullanici.matches(kullaniciidarray.get(position))){
            holder.satinal.setVisibility(View.VISIBLE);
            holder.satinal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final EditText miktargiriniz=new EditText(v.getContext());
                    final AlertDialog.Builder miktardialog=new AlertDialog.Builder(v.getContext());
                    miktardialog.setTitle("Miktar Giriniz");
                    miktardialog.setMessage("Lütfen Almak İstediginiz Miktari Giriniz");
                    miktardialog.setView(miktargiriniz);
                    miktardialog.setPositiveButton("Onayliyorum", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String girilen=miktargiriniz.getText().toString();
                            Double girilenmiktar=Double.parseDouble(girilen);
                            Double kgfiyati=kgfiyatiarray.get(position);
                            Double sonuc=miktaridarray.get(position)-girilenmiktar;


                            if (girilenmiktar<=miktaridarray.get(position)){
                                if (girilenmiktar*kgfiyati<paramiktari) {
                                    Double parasonuc = paramiktari - (girilenmiktar * kgfiyati) - (girilenmiktar * kgfiyati)*1/100;
                                    firebaseFirestore.collection("Eklenenpara").document(sayfadakikullanici).update("paramiktar", parasonuc).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });

                                    HashMap<String,Object> satinalinandata=new HashMap<>();
                                    satinalinandata.put("satinurun",urunidarray.get(position));
                                    satinalinandata.put("girilenmiktar",girilenmiktar);
                                    satinalinandata.put("kgfiyati",kgfiyati);
                                    satinalinandata.put("tarih", FieldValue.serverTimestamp());
                                    satinalinandata.put("sayfadakikullanici",sayfadakikullanici);
                                    firebaseFirestore.collection("satinalinanlar").add(satinalinandata).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {

                                        }
                                    });
                                }

                                firebaseFirestore.collection("Eklenenurunler").document(eklenenurunidarray.get(position)).update("miktar",sonuc).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if (sonuc==0){
                                            firebaseFirestore.collection("Eklenenurunler").document(eklenenurunidarray.get(position)).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                }
                                            });
                                        }

                                    }
                                });



                            }

                        }
                    });
                    miktardialog.setNegativeButton("Onaylamiyorum", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    miktardialog.create().show();

                }
            });
        }



    }

    @Override
    public int getItemCount() {
        return urunidarray.size();
    }

    class Postfiyat extends RecyclerView.ViewHolder{
        TextView fiyattakiurun,fiyattakimiktar,fiyattakikgfiyati;
        ImageView satinal;

        public Postfiyat(@NonNull View itemView) {
            super(itemView);
            fiyattakiurun=itemView.findViewById(R.id.almakisteneniurun_textview);
            fiyattakimiktar=itemView.findViewById(R.id.almakistenenmiktar_textview);
            fiyattakikgfiyati=itemView.findViewById(R.id.almakistenenkgfiyati_textview);
            satinal=itemView.findViewById(R.id.satinal_imageview);
        }
    }
}
