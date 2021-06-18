package com.huso.yazilimyapimi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.JsonObject;
import com.google.rpc.context.AttributeContext;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.XMLFormatter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class paraekle extends AppCompatActivity {
    EditText paraekleedittext;
    Button paraeklebuton;
    Spinner paralarspinner;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paraekle);

        paraekleedittext = findViewById(R.id.paraekle_edittext);
        paraeklebuton=findViewById(R.id.paraekle_buton);
        paralarspinner=findViewById(R.id.paralar_spinner);

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        ArrayAdapter<String> myadapter=new ArrayAdapter<String>(paraekle.this, android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.paralar));
        myadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paralarspinner.setAdapter(myadapter);

    }
    public void paraeklebutonu(View view){
        String paralar=paralarspinner.getSelectedItem().toString();
        String paramiktari=paraekleedittext.getText().toString();
        String parayiekleyenkullanici=firebaseUser.getUid();
        Double para=Double.parseDouble(paramiktari);

        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String doviz="https://www.tcmb.gov.tr/kurlar/today.xml";//tcmb sayfasindan istenilenleri cekiyoruz
        HttpURLConnection baglanti=null;

        try {//baglanti islemlerini gerceklestiriyoruz
            URL url=new URL(doviz);
            baglanti=(HttpURLConnection) url.openConnection();

            int baglanti_durumu=baglanti.getResponseCode();
            if(baglanti_durumu==HttpURLConnection.HTTP_OK){//istedimiz para biriminin verilerini cekiyoruz
                BufferedInputStream stream=new BufferedInputStream(baglanti.getInputStream());
                DocumentBuilderFactory documentBuilderFactory=DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder=documentBuilderFactory.newDocumentBuilder();

                Document document=documentBuilder.parse(stream);

                NodeList dovizkurlari=document.getElementsByTagName("Currency");

                Element elementabddolari=(Element) dovizkurlari.item(0);
                NodeList nodeListsatisabddolari=elementabddolari.getElementsByTagName("ForexSelling");
                String satisabddolari=nodeListsatisabddolari.item(0).getFirstChild().getNodeValue();


                Element elementEuro=(Element) dovizkurlari.item(3);
                NodeList nodeListsatiseuro=elementEuro.getElementsByTagName("ForexSelling");
                String satiseuro=nodeListsatiseuro.item(0).getFirstChild().getNodeValue();


                Element elementsterlin=(Element) dovizkurlari.item(4);
                NodeList nodeListsatissterlin=elementsterlin.getElementsByTagName("ForexSelling");
                String satissterlin=nodeListsatissterlin.item(0).getFirstChild().getNodeValue();

                 //burada if kosullariyla istenilen para birimine gore hesap islemlerini yapiyoruz
                if (paralar.matches("ABD DOLARI")){
                    Double satisdolar=Double.parseDouble(satisabddolari);
                    para=para*satisdolar;
                    firebaseparaekle(paralar,para,parayiekleyenkullanici);
                }
                else if(paralar.matches("EURO")){
                    Double satisEuro=Double.parseDouble(satiseuro);
                    para=para*satisEuro;
                    firebaseparaekle(paralar,para,parayiekleyenkullanici);

                }else if (paralar.matches("İNGİLİZ STERLİNİ")){
                    Double satisSterlin=Double.parseDouble(satissterlin);
                    para=para*satisSterlin;
                    firebaseparaekle(paralar,para,parayiekleyenkullanici);
                }
                else if (paralar.matches("TL")){
                    firebaseparaekle(paralar,para,parayiekleyenkullanici);
                }


            }

        }catch (Exception e){
            e.printStackTrace();

        }finally {
            if (baglanti!=null){
                baglanti.disconnect();
            }
        }


    }
    public void firebaseparaekle(String paralar,Double para,String parayiekleyenkullanici){
        firebaseFirestore.collection("Profiller").document(parayiekleyenkullanici).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot!=null) {
                    String ad = documentSnapshot.getString("ad");
                    String soyad = documentSnapshot.getString("soyad");
                    if (ad != null && soyad != null) {
                        HashMap<String, Object> paradata = new HashMap();
                        paradata.put("para", para);
                        paradata.put("paralar", paralar);
                        paradata.put("paraekleyenkisi", parayiekleyenkullanici);
                        firebaseFirestore.collection("Para").add(paradata).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(paraekle.this, "Para Onaylanmak Icin Gonderilmistir.", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(paraekle.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(paraekle.this, "!!!!!Profil bilgileriniz bos lütfen duzenleyiniz!!!!!", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }


}