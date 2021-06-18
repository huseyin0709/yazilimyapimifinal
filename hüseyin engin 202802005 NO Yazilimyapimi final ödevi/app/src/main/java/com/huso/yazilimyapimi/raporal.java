package com.huso.yazilimyapimi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.Page;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class  raporal extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 200;
    TextView baslangictarihi,bitistarihi;
    Button Raporalbutonu;
    DatePickerDialog.OnDateSetListener baslangiccdateSetListener,bitisdateSetListener;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    int pageHeight = 1120;
    int pagewidth = 792;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raporal);

        baslangictarihi=findViewById(R.id.baslangictarihi_textview);
        bitistarihi=findViewById(R.id.bitistarihi_textview);
        Raporalbutonu=findViewById(R.id.raporal_butonu);
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        baslangictarihi.setOnClickListener(new View.OnClickListener() {//baslangic tarihi gun ay yil seklinde ayarlamasi yapilir
            @Override
            public void onClick(View v) {
                Calendar calendar=Calendar.getInstance();
                int yil=calendar.get(Calendar.YEAR);
                int ay=calendar.get(Calendar.MONTH);
                int gun=calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog=new DatePickerDialog(
                        raporal.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        baslangiccdateSetListener,yil,ay,gun);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        bitistarihi.setOnClickListener(new View.OnClickListener() {//Bitis tarihi gun ay yil seklinde ayarlamasi yapilir
            @Override
            public void onClick(View v) {
                Calendar calendar=Calendar.getInstance();
                int yil=calendar.get(Calendar.YEAR);
                int ay=calendar.get(Calendar.MONTH);
                int gun=calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog=new DatePickerDialog(
                        raporal.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        bitisdateSetListener,yil,ay,gun);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        baslangiccdateSetListener=new DatePickerDialog.OnDateSetListener() {//uste ayarladigimiz baslangic tarihini sisteme entegre ediyoruz
            @Override
            public void onDateSet(DatePicker view, int yil, int ay, int gun) {
                ay=ay+1;
                String baslangicdate=gun+"/"+ay+"/"+yil;
                baslangictarihi.setText(baslangicdate);
            }
        };
        bitisdateSetListener=new DatePickerDialog.OnDateSetListener() {//uste ayarladigimiz bitis tarihini sisteme entegre ediyoruz
            @Override
            public void onDateSet(DatePicker view, int yil, int ay, int gun) {
                ay=ay+1;
                String bitisdate=gun+"/"+ay+"/"+yil;
                bitistarihi.setText(bitisdate);

            }
        };

        if (checkPermission()) {//sayfaya girilirken izin verildimi diye kontrol ediyoruz
            Toast.makeText(this, "izin verildi", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }

    }
    private boolean checkPermission() {//sistemdeki izinlerin kontrolunu yapiyoruz
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "izin verildi..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "izin verildi.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    public void raporalbutonu(View view){//satin al butonuna tikladigimiz zaman sisteme kaydedilen satin alinanlar cekilir
        firebaseFirestore.collection("satinalinanlar").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                if (querySnapshot!=null) {
                    for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {
                        Map<String,Object> satinalinanbilgiler=snapshot.getData();
                        Double miktar=(Double) satinalinanbilgiler.get("girilenmiktar");
                        Double kgfiyati=(Double) satinalinanbilgiler.get("kgfiyati");
                        String urun=(String) satinalinanbilgiler.get("satinurun");
                        String sayfadakikullanici=(String) satinalinanbilgiler.get("sayfadakikullanici");
                        Timestamp tarih=(Timestamp) satinalinanbilgiler.get("tarih");

                        String yenimiktar=miktar.toString();
                        String yenikgfiyati=kgfiyati.toString();

                        firebaseFirestore.collection("Profiller").document(sayfadakikullanici).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                                if (documentSnapshot!=null) {

                                    String adi = documentSnapshot.getString("ad");
                                    String soyadi = documentSnapshot.getString("soyad");
                                    String advesoyad = adi + " " + soyadi;

                                    PdfDocument pdfDocument = new PdfDocument();//pdf documentini olustururuz
                                    Paint title = new Paint();
                                    PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create();
                                    PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);
                                    Canvas canvas = myPage.getCanvas();

                                    title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));//burada ve asagi satirlarda pdf de istediklerimizi ve boyutlar,renklerin ayarlamalarini yapariz
                                    title.setTextSize(45);
                                    title.setColor(ContextCompat.getColor(raporal.this, R.color.purple_200));
                                    canvas.drawText("Satin Alinanlar Listesi", 209, 40, title);

                                    title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                                    title.setTextSize(45);
                                    title.setColor(ContextCompat.getColor(raporal.this, R.color.black));
                                    canvas.drawText("" + "Adi ve Soyadi : " + advesoyad, 4, 100, title);
                                    canvas.drawText("" + "Alinan Urun : " + urun, 4, 140, title);
                                    canvas.drawText("" + "Alinan Miktar : " + yenimiktar, 4, 180, title);
                                    canvas.drawText("" + "Alinan KG fiyati : " + yenikgfiyati, 4, 220, title);
                                    canvas.drawText("" + "Tarih ve Saat : " + tarih.toDate(), 4, 260, title);
                                    pdfDocument.finishPage(myPage);
                                    File file = new File(Environment.getExternalStorageDirectory(), "Satinalinanlar.pdf");

                                    try {
                                        pdfDocument.writeTo(new FileOutputStream(file));
                                        Toast.makeText(raporal.this, "PDF Basarili Bir Sekilde Kaydedildi.", Toast.LENGTH_SHORT).show();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    pdfDocument.close();
                                }

                            }
                        });

                    }
                }
            }
        });
    }
}