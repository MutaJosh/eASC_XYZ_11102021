package com.betterise.maladiecorona;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.betterise.maladiecorona.managers.AgentManager;
import com.betterise.maladiecorona.managers.QuestionManager;
import com.betterise.maladiecorona.model.IndexCode;
import com.betterise.maladiecorona.model.Item_session;
import com.betterise.maladiecorona.networking.singleton.RESTApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientDetailsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {


    private String statusNID,codeNID,messageNID,fNameNID,lNameNID,dobNID,nationalityNID;

    private static final int PRIVATE_MODE =0 ;
  private AppCompatRadioButton radioYes;
  private LinearLayout laypatientdatahide,layindex;


    ArrayList<IndexCode> mExampleListi;

private RadioGroup radiogrouptype;
    private Spinner sp_gender, sp_nationality, sp_residence,spinner_Number_Dose,sp_district,sp_province,sp_sector;
    private String checkindex,nationality, gender, yearr, month, date, residency, dob,numberhousehold,VaxxineType,Number_Dose,NulledVaccination;
    private ImageButton btndatepicker;
    private Button btn_next;
    private ImageView btn_back;
    private TextView tvdob;
    private Spinner spinner_VaccineType;
    private int mYear, mMonth, mDay;
    private LinearLayout lay_vaccine;
    private TextView tvindexcode;
    private EditText etfirstname,et_numberhousehold, etlastname, etnational_ID, etpatientgender, etpatienttelephone, etoccupation, etresidence, etnationality, etcell, etvillage;
    private String fn, lastname, national_ID, patientgender, patienttelephone, occupation, residence, province, district, sector, cell, village;
public static final String PREF_FIRSTNAME = "firstname";
    public static final String PREF_LASTNAME = "lastname";

    private ProgressDialog progressDialogi;

    private  ProgressDialog progresso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);


        progressDialogi=new ProgressDialog(PatientDetailsActivity.this);
        progressDialogi.setMessage(getString(R.string.loading));
        progressDialogi.setCanceledOnTouchOutside(false);

        sp_province=findViewById(R.id.sp_province);

        laypatientdatahide=findViewById(R.id.laypatientdatahide);
        layindex=findViewById(R.id.layindex);

        tvindexcode=findViewById(R.id.tvindexcode);
        spinner_VaccineType=findViewById(R.id.spinner_VaccineType);
        spinner_Number_Dose=findViewById(R.id.spinner_Number_Dose);
        lay_vaccine=findViewById(R.id.lay_vaccine);
        sp_district=findViewById(R.id.sp_district);
        radiogrouptype=(RadioGroup)findViewById(R.id.radiogrouptype);
        //long l = ByteBuffer.wrap(getIntent().getStringExtra("uuid").toString().getBytes()).getLong();
    //  String a= Long.toString(l, Character.MAX_RADIX);

        et_numberhousehold=findViewById(R.id.et_numberhousehold);
        tvindexcode.setTextIsSelectable(true);

        progresso=new ProgressDialog(PatientDetailsActivity.this);
        progresso.setMessage(getString(R.string.loading));
        progresso.setCanceledOnTouchOutside(false);


        radiogrouptype.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioYes =(AppCompatRadioButton) findViewById(selectedId);

                if (radioYes.getText().equals(getString(R.string.yesi))){
                    lay_vaccine.setVisibility(View.VISIBLE);
                    NulledVaccination="yes";
                    new AgentManager().savereceived_status(getBaseContext(),"yes");


                }else {

                    new AgentManager().savereceived_status(getBaseContext(),"no");
                    NulledVaccination="no";
                    lay_vaccine.setVisibility(View.GONE);
                }
            }
        });


        etfirstname=findViewById(R.id.firstname);
        etlastname=findViewById(R.id.etlastname);
        etnational_ID=findViewById(R.id.national_ID);
        etpatienttelephone=findViewById(R.id.patient_telephone);
        etoccupation=findViewById(R.id.etoccupation);
        etcell=findViewById(R.id.etcell);
        etvillage=findViewById(R.id.etvillage);

        sp_gender = findViewById(R.id.sp_gender);
        sp_nationality = findViewById(R.id.sp_nationality);
        sp_residence = findViewById(R.id.spresidence);

        tvdob = findViewById(R.id.tvdob);
        btndatepicker = findViewById(R.id.btndatepicker);
        btndatepicker.setOnClickListener(this);



       // Toast.makeText(PatientDetailsActivity.this, "clicked outside", Toast.LENGTH_SHORT).show();


//        etnational_ID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                //callNIDAPI();
//            }
//        });


        sp_nationality.setOnItemSelectedListener(this);

        List<String> listcountry = new ArrayList<String>();

        listcountry.add("Rwanda");
        listcountry.add("Tanzania");
        listcountry.add("Burundi");
        listcountry.add("Kenya");
        listcountry.add("Uganda");
        listcountry.add("DRC");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listcountry);
        // Drop down layout style - list view with radio button
        ArrayAdapter<String> dataAdapte = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listcountry);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapte.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_nationality.setAdapter(dataAdapter);
        sp_residence.setAdapter(dataAdapte);

        sp_sector=findViewById(R.id.sp_sector);


        List<String> listnumbervacc = new ArrayList<String>();

        listnumbervacc.add(getString(R.string.dose1));
        listnumbervacc.add(getString(R.string.dose2));
        listnumbervacc.add(getString(R.string.dose3));

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapternumbervacc = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listnumbervacc);
        // Drop down layout style - list view with radio button
        dataAdapternumbervacc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_Number_Dose.setAdapter(dataAdapternumbervacc);

        spinner_Number_Dose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Number_Dose=adapterView.getItemAtPosition(i)+"";
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        List<String> listprovince=new ArrayList<>();

        listprovince.add(getString(R.string.kigali));
        listprovince.add(getString(R.string.south));
        listprovince.add(getString(R.string.east));
        listprovince.add(getString(R.string.north));
        listprovince.add(getString(R.string.west));
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapterprovi = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listprovince);
        // Drop down layout style - list view with radio button
        dataAdapterprovi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_province.setAdapter(dataAdapterprovi);
        sp_province.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                province=adapterView.getItemAtPosition(i).toString();
                if (province.equals(getString(R.string.kigali))){
                    List<String> listdis = new ArrayList<String>();

                    listdis.add("Gasabo");
                    listdis.add("Kicukiro");
                    listdis.add("Nyarugenge");
                    // Creating adapter for spinner
                    ArrayAdapter<String> dataAdapterdis = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listdis);
                    // Drop down layout style - list view with radio button
                    dataAdapterdis.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_district.setAdapter(dataAdapterdis);

                    sp_district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            district=adapterView.getItemAtPosition(i).toString();

                            if (district.equals("Nyarugenge")){
                                List<String> listsector = new ArrayList<String>();

                                listsector.add("Gitega");
                                listsector.add("Kanyinya");
                                listsector.add("Kigali");
                                listsector.add("Kimisagara");
                                listsector.add("Mageregere");
                                listsector.add("Muhima");
                                listsector.add("Nyakabanda");
                                listsector.add("Nyamirambo");
                                listsector.add("Nyarugenge");
                                listsector.add("Rwezamenyo");

                                // Creating adapter for spinner
                                ArrayAdapter<String> dataAdaptersector = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listsector);
                                // Drop down layout style - list view with radio button
                                dataAdaptersector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp_sector.setAdapter(dataAdaptersector);

                                sp_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        sector=adapterView.getItemAtPosition(i)+"";
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                            }
                            else if (district.equals("Gasabo")){
                                List<String> listsector = new ArrayList<String>();

                                listsector.add("Bumbogo");
                                listsector.add("Gatsata");
                                listsector.add("Gikomero");
                                listsector.add("Gisozi");
                                listsector.add("Jabana");
                                listsector.add("Jali");
                                listsector.add("Kacyiru");
                                listsector.add("Kimihurura");
                                listsector.add("Kimironko");
                                listsector.add("Kinyinya");
                                listsector.add("Ndera");
                                listsector.add("Rusororo");
                                listsector.add("Rutunga");
                                listsector.add("Remera");
                                listsector.add("Nduba");

                                // Creating adapter for spinner
                                ArrayAdapter<String> dataAdaptersector = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listsector);
                                // Drop down layout style - list view with radio button
                                dataAdaptersector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp_sector.setAdapter(dataAdaptersector);

                                sp_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        sector=adapterView.getItemAtPosition(i)+"";
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                            }
                            else if (district.equals("Kicukiro")){
                                List<String> listsector = new ArrayList<String>();

                                listsector.add("Kanombe");
                                listsector.add("Gahanga");
                                listsector.add("Gatenga");
                                listsector.add("Gikondo");
                                listsector.add("Kagarama");
                                listsector.add("Kicukiro");
                                listsector.add("Kigarama");
                                listsector.add("Masaka");
                                listsector.add("Niboye");
                                listsector.add("Nyarugunga");

                                // Creating adapter for spinner
                                ArrayAdapter<String> dataAdaptersector = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listsector);
                                // Drop down layout style - list view with radio button
                                dataAdaptersector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp_sector.setAdapter(dataAdaptersector);

                                sp_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        sector=adapterView.getItemAtPosition(i)+"";
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                            }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }
                else if (province.equals(getString(R.string.east))){

                    List<String> listdis = new ArrayList<String>();

                    listdis.add("Bugesera");
                    listdis.add("Gatsibo");
                    listdis.add("Kayonza");
                    listdis.add("Kirehe");
                    listdis.add("Ngoma");
                    listdis.add("Nyagatare");
                    listdis.add("Rwamagana");

                    // Creating adapter for spinner
                    ArrayAdapter<String> dataAdapterdis = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listdis);
                    // Drop down layout style - list view with radio button
                    dataAdapterdis.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_district.setAdapter(dataAdapterdis);

                    sp_district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            district=adapterView.getItemAtPosition(i).toString();
                            if (district.equals("Bugesera")){
                                List<String> listsector = new ArrayList<String>();

                                listsector.add("Gashora");
                                listsector.add("Juru");
                                listsector.add("Kamabuye");
                                listsector.add("Ntarama");
                                listsector.add("Mareba");
                                listsector.add("Mayange");
                                listsector.add("Musenyi");
                                listsector.add("Mwogo");
                                listsector.add("Ngeruka");
                                listsector.add("Nyamata");
                                listsector.add("Nyarugenge");
                                listsector.add("Rilima");
                                listsector.add("Ruhuha");
                                listsector.add("Rweru");
                                listsector.add("Shyara");

                                // Creating adapter for spinner
                                ArrayAdapter<String> dataAdaptersector = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listsector);
                                // Drop down layout style - list view with radio button
                                dataAdaptersector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp_sector.setAdapter(dataAdaptersector);

                                sp_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        sector=adapterView.getItemAtPosition(i)+"";
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                            }
                            else if (district.equals("Gatsibo")){
                                List<String> listsector = new ArrayList<String>();

                                listsector.add("Gasange");
                                listsector.add("Gatsibo");
                                listsector.add("Gitoki");
                                listsector.add("Kabarore");
                                listsector.add("Kageyo");
                                listsector.add("Kiramuruzi");
                                listsector.add("Kiziguro");
                                listsector.add("Muhura");
                                listsector.add("Murambi");
                                listsector.add("Ngarama");
                                listsector.add("Nyagihanga");
                                listsector.add("Remera");
                                listsector.add("Rugarama");
                                listsector.add("Rwimbogo");

                                // Creating adapter for spinner
                                ArrayAdapter<String> dataAdaptersector = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listsector);
                                // Drop down layout style - list view with radio button
                                dataAdaptersector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp_sector.setAdapter(dataAdaptersector);

                                sp_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        sector=adapterView.getItemAtPosition(i)+"";
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                            }
                            else if (district.equals("Kayonza")){
                                List<String> listsector = new ArrayList<String>();

                                listsector.add("Gahini");
                                listsector.add("Kabare");
                                listsector.add("Kabarondo");
                                listsector.add("Mukarange");
                                listsector.add("Murama");
                                listsector.add("Murundi");
                                listsector.add("Mwiri");
                                listsector.add("Ndego");
                                listsector.add("Nyamirama");
                                listsector.add("Rukara");
                                listsector.add("Ruramira");
                                listsector.add("Rwinkwavu");

                                // Creating adapter for spinner
                                ArrayAdapter<String> dataAdaptersector = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listsector);
                                // Drop down layout style - list view with radio button
                                dataAdaptersector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp_sector.setAdapter(dataAdaptersector);

                                sp_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        sector=adapterView.getItemAtPosition(i)+"";
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                            }
                            else if (district.equals("Kirehe")){
                                List<String> listsector = new ArrayList<String>();

                                listsector.add("Gahara");
                                listsector.add("Gatore");
                                listsector.add("Kigarama");
                                listsector.add("Kigina");
                                listsector.add("Kirehe");
                                listsector.add("Mahama");
                                listsector.add("Mpanga");
                                listsector.add("Musaza");
                                listsector.add("Mushikiri");
                                listsector.add("Nasho");
                                listsector.add("Nyamugari");
                                listsector.add("Nyarubuye");

                                // Creating adapter for spinner
                                ArrayAdapter<String> dataAdaptersector = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listsector);
                                // Drop down layout style - list view with radio button
                                dataAdaptersector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp_sector.setAdapter(dataAdaptersector);

                                sp_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        sector=adapterView.getItemAtPosition(i)+"";
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                            }
                            else if (district.equals("Ngoma")){
                                List<String> listsector = new ArrayList<String>();

                                listsector.add("Gashanda");
                                listsector.add("Jarama");
                                listsector.add("Karembo");
                                listsector.add("Kazo");
                                listsector.add("Kibungo");
                                listsector.add("Mugesera");
                                listsector.add("Murama");
                                listsector.add("Mutenderi");
                                listsector.add("Remera");
                                listsector.add("Rukira");
                                listsector.add("Rukumberi");
                                listsector.add("Rurenge");
                                listsector.add("Sake");
                                listsector.add("Zaza");

                                // Creating adapter for spinner
                                ArrayAdapter<String> dataAdaptersector = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listsector);
                                // Drop down layout style - list view with radio button
                                dataAdaptersector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp_sector.setAdapter(dataAdaptersector);

                                sp_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        sector=adapterView.getItemAtPosition(i)+"";
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                            }
                            else if (district.equals("Nyagatare")){
                                List<String> listsector = new ArrayList<String>();

                                listsector.add("Gatunda");
                                listsector.add("Kiyombe");
                                listsector.add("Karama");
                                listsector.add("Karangazi");
                                listsector.add("Katabagemu");
                                listsector.add("Matimba");
                                listsector.add("Mimuli");
                                listsector.add("Mukama");
                                listsector.add("Musheli");
                                listsector.add("Nyagatare");
                                listsector.add("Rukomo");
                                listsector.add("Rwempasha");
                                listsector.add("Rwimiyaga");
                                listsector.add("Tabagwe");

                                // Creating adapter for spinner
                                ArrayAdapter<String> dataAdaptersector = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listsector);
                                // Drop down layout style - list view with radio button
                                dataAdaptersector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp_sector.setAdapter(dataAdaptersector);

                                sp_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        sector=adapterView.getItemAtPosition(i)+"";
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                            }
                            else if (district.equals("Rwamagana")){
                                List<String> listsector = new ArrayList<String>();

                                listsector.add("Fumbwe");
                                listsector.add("Gahengeri");
                                listsector.add("Gishari");
                                listsector.add("Karenge");
                                listsector.add("Kigabiro");
                                listsector.add("Muhazi");
                                listsector.add("Munyaga");
                                listsector.add("Munyiginya");
                                listsector.add("Musha");
                                listsector.add("Muyumbu");
                                listsector.add("Mwulire");
                                listsector.add("Nyakariro");
                                listsector.add("Nzige");
                                listsector.add("Rubona");

                                // Creating adapter for spinner
                                ArrayAdapter<String> dataAdaptersector = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listsector);
                                // Drop down layout style - list view with radio button
                                dataAdaptersector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp_sector.setAdapter(dataAdaptersector);

                                sp_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        sector=adapterView.getItemAtPosition(i)+"";
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                            }


                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }
                else if (province.equals(getString(R.string.south))){

                    List<String> listdis = new ArrayList<String>();
                    listdis.add("Gisagara");
                    listdis.add("Huye");
                    listdis.add("Kamonyi");
                    listdis.add("Muhanga");
                    listdis.add("Nyamagabe");
                    listdis.add("Nyanza");
                    listdis.add("Nyaruguru");
                    listdis.add("Ruhango");


                    // Creating adapter for spinner
                    ArrayAdapter<String> dataAdapterdis = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listdis);
                    // Drop down layout style - list view with radio button
                    dataAdapterdis.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_district.setAdapter(dataAdapterdis);

                    sp_district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            district=adapterView.getItemAtPosition(i).toString();
                            if (district.equals("Gisagara")){
                                List<String> listsector = new ArrayList<String>();

                                listsector.add("Gikonko");
                                listsector.add("Gishubi");
                                listsector.add("Kansi");
                                listsector.add("Kibilizi");
                                listsector.add("Kigembe");
                                listsector.add("Mamba");
                                listsector.add("Muganza");
                                listsector.add("Mugombwa");
                                listsector.add("Mukindo");
                                listsector.add("Musha");
                                listsector.add("Ndora");
                                listsector.add("Nyanza");
                                listsector.add("Save");

                                // Creating adapter for spinner
                                ArrayAdapter<String> dataAdaptersector = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listsector);
                                // Drop down layout style - list view with radio button
                                dataAdaptersector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp_sector.setAdapter(dataAdaptersector);

                                sp_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        sector=adapterView.getItemAtPosition(i)+"";
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                            }
                            else if (district.equals("Huye")){
                                List<String> listsector = new ArrayList<String>();

                                listsector.add("Gishamvu");
                                listsector.add("Karama");
                                listsector.add("Kigoma");
                                listsector.add("Kinazi");
                                listsector.add("Maraba");
                                listsector.add("Mbazi");
                                listsector.add("Mukura");
                                listsector.add("Ngoma");
                                listsector.add("Ruhashya");
                                listsector.add("Huye");
                                listsector.add("Rusatira");
                                listsector.add("Rwaniro");
                                listsector.add("Simbi");
                                listsector.add("Tumba");

                                // Creating adapter for spinner
                                ArrayAdapter<String> dataAdaptersector = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listsector);
                                // Drop down layout style - list view with radio button
                                dataAdaptersector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp_sector.setAdapter(dataAdaptersector);

                                sp_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        sector=adapterView.getItemAtPosition(i)+"";
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                            }
                            else if (district.equals("Kamonyi")){
                                List<String> listsector = new ArrayList<String>();

                                listsector.add("Gacurabwenge");
                                listsector.add("Karama");
                                listsector.add("Kayenzi");
                                listsector.add("Kayumbu");
                                listsector.add("Mugina");
                                listsector.add("Musambira");
                                listsector.add("Ngamba");
                                listsector.add("Nyamiyaga");
                                listsector.add("Nyarubaka");
                                listsector.add("Rugalika");
                                listsector.add("Rukoma");
                                listsector.add("Runda");

                                // Creating adapter for spinner
                                ArrayAdapter<String> dataAdaptersector = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listsector);
                                // Drop down layout style - list view with radio button
                                dataAdaptersector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp_sector.setAdapter(dataAdaptersector);

                                sp_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        sector=adapterView.getItemAtPosition(i)+"";
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                            }
                            else if (district.equals("Muhanga")){
                                List<String> listsector = new ArrayList<String>();

                                listsector.add(" Muhanga");
                                listsector.add("Cyeza");
                                listsector.add("Kibangu");
                                listsector.add("Kiyumba");
                                listsector.add("Mushishiro");
                                listsector.add("Kabacuzi");
                                listsector.add("Nyabinoni");
                                listsector.add("Nyamabuye");
                                listsector.add("Nyarusange");
                                listsector.add("Rongi");
                                listsector.add("Rugendabari");
                                listsector.add("Shyogwe");

                                // Creating adapter for spinner
                                ArrayAdapter<String> dataAdaptersector = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listsector);
                                // Drop down layout style - list view with radio button
                                dataAdaptersector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp_sector.setAdapter(dataAdaptersector);

                                sp_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        sector=adapterView.getItemAtPosition(i)+"";
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                            }
                            else if (district.equals("Nyamagabe")){
                                List<String> listsector = new ArrayList<String>();

                                listsector.add("Buruhukiro");
                                listsector.add("Cyanika");
                                listsector.add("Gatare");
                                listsector.add("Kaduha");
                                listsector.add("Kamegeli");
                                listsector.add("Kibirizi");
                                listsector.add("Kibumbwe");
                                listsector.add("Kitabi");
                                listsector.add("Mbazi");
                                listsector.add("Mugano");
                                listsector.add("Musange");
                                listsector.add("Musebeya");
                                listsector.add("Mushubi");
                                listsector.add("Nkomane");
                                listsector.add("Gasaka");
                                listsector.add("Tare");
                                listsector.add("Uwinkingi");

                                // Creating adapter for spinner
                                ArrayAdapter<String> dataAdaptersector = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listsector);
                                // Drop down layout style - list view with radio button
                                dataAdaptersector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp_sector.setAdapter(dataAdaptersector);

                                sp_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        sector=adapterView.getItemAtPosition(i)+"";
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                            }
                            else if (district.equals("Nyanza")){
                                List<String> listsector = new ArrayList<String>();

                                listsector.add("Busasamana");
                                listsector.add("Busoro");
                                listsector.add("Cyabakamyi");
                                listsector.add("Kibirizi");
                                listsector.add("Kigoma");
                                listsector.add("Mukingo");
                                listsector.add("Muyira");
                                listsector.add("Ntyazo");
                                listsector.add("Nyagisozi");
                                listsector.add("Rwabicuma");
                                // Creating adapter for spinner
                                ArrayAdapter<String> dataAdaptersector = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listsector);
                                // Drop down layout style - list view with radio button
                                dataAdaptersector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp_sector.setAdapter(dataAdaptersector);

                                sp_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        sector=adapterView.getItemAtPosition(i)+"";
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                            }
                            else if (district.equals("Nyaruguru")){
                                List<String> listsector = new ArrayList<String>();

                                listsector.add("Cyahinda");
                                listsector.add("Busanze");
                                listsector.add("Kibeho");
                                listsector.add("Mata");
                                listsector.add("Munini");
                                listsector.add("Kivu");
                                listsector.add("Ngera");
                                listsector.add("Ngoma");
                                listsector.add("Nyabimata");
                                listsector.add("Nyagisozi");
                                listsector.add("Muganza");
                                listsector.add("Ruheru");
                                listsector.add("Ruramba");
                                listsector.add("Rusenge");
                                // Creating adapter for spinner
                                ArrayAdapter<String> dataAdaptersector = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listsector);
                                // Drop down layout style - list view with radio button
                                dataAdaptersector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp_sector.setAdapter(dataAdaptersector);

                                sp_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        sector=adapterView.getItemAtPosition(i)+"";
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                            }
                            else if (district.equals("Ruhango")){
                                List<String> listsector = new ArrayList<String>();

                                listsector.add("Kinazi");
                                listsector.add("Byimana");
                                listsector.add("Bweramana");
                                listsector.add("Mbuye");
                                listsector.add("Ruhango");
                                listsector.add("Mwendo");
                                listsector.add("Kinihira");
                                listsector.add("Ntongwe");
                                listsector.add("Kabagari");
                                // Creating adapter for spinner
                                ArrayAdapter<String> dataAdaptersector = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listsector);
                                // Drop down layout style - list view with radio button
                                dataAdaptersector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp_sector.setAdapter(dataAdaptersector);

                                sp_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        sector=adapterView.getItemAtPosition(i)+"";
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                            }



                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }
                else if (province.equals(getString(R.string.north))){

                    List<String> listdis = new ArrayList<String>();
                    listdis.add("Burera");
                    listdis.add("Gakenke");
                    listdis.add("Gicumbi");
                    listdis.add("Musanze");
                    listdis.add("Rulindo");


                    // Creating adapter for spinner
                    ArrayAdapter<String> dataAdapterdis = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listdis);
                    // Drop down layout style - list view with radio button
                    dataAdapterdis.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_district.setAdapter(dataAdapterdis);

                    sp_district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            district=adapterView.getItemAtPosition(i).toString();
                            if (district.equals("Burera")){
                                List<String> listsector = new ArrayList<String>();

                                listsector.add("Bungwe");
                                listsector.add("Butaro");
                                listsector.add("Cyanika");
                                listsector.add("Cyeru");
                                listsector.add("Gahunga");
                                listsector.add("Gatebe");
                                listsector.add("Gitovu");
                                listsector.add("Kagogo");
                                listsector.add("Kinoni");
                                listsector.add("Kinyababa");
                                listsector.add("Kivuye");
                                listsector.add("Nemba");
                                listsector.add("Rugarama");
                                listsector.add("Rugendabari");
                                listsector.add("Ruhunde");
                                listsector.add("Rusarabuye");
                                listsector.add("Rwerere");
                                // Creating adapter for spinner
                                ArrayAdapter<String> dataAdaptersector = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listsector);
                                // Drop down layout style - list view with radio button
                                dataAdaptersector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp_sector.setAdapter(dataAdaptersector);

                                sp_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        sector=adapterView.getItemAtPosition(i)+"";
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                            }
                            else if (district.equals("Gakenke")){
                                List<String> listsector = new ArrayList<String>();

                                listsector.add("Busengo");
                                listsector.add("Coko");
                                listsector.add("Cyabingo");
                                listsector.add("Gakenke");
                                listsector.add("Gashenyi");
                                listsector.add("Mugunga");
                                listsector.add("Janja");
                                listsector.add("Kamubuga");
                                listsector.add("Karambo");
                                listsector.add("Kivuruga");
                                listsector.add("Mataba");
                                listsector.add("Minazi");
                                listsector.add("Muhondo");
                                listsector.add("Muyongwe");
                                listsector.add("Muzo");
                                listsector.add("Nemba");
                                listsector.add("Ruli");
                                listsector.add("Rusasa");
                                listsector.add("Rushashi");
                                // Creating adapter for spinner
                                ArrayAdapter<String> dataAdaptersector = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listsector);
                                // Drop down layout style - list view with radio button
                                dataAdaptersector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp_sector.setAdapter(dataAdaptersector);

                                sp_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        sector=adapterView.getItemAtPosition(i)+"";
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                            }
                            else if (district.equals("Gicumbi")){
                                List<String> listsector = new ArrayList<String>();

                                listsector.add("Bukure");
                                listsector.add("Bwisige");
                                listsector.add("Byumba");
                                listsector.add("Cyumba");
                                listsector.add("Giti");
                                listsector.add("Kaniga");
                                listsector.add("Manyagiro");
                                listsector.add("Miyove");
                                listsector.add("Kageyo");
                                listsector.add("Mukarange");
                                listsector.add("Muko");
                                listsector.add("Mutete");
                                listsector.add("Nyamiyaga");
                                listsector.add("Nyankenke II");
                                listsector.add("Rubaya");
                                listsector.add("Rukomo");
                                listsector.add("Rushaki");
                                listsector.add("Rutare");
                                listsector.add("Ruvune");
                                listsector.add("Rwamiko");
                                listsector.add("Shangasha");
                                // Creating adapter for spinner
                                ArrayAdapter<String> dataAdaptersector = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listsector);
                                // Drop down layout style - list view with radio button
                                dataAdaptersector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp_sector.setAdapter(dataAdaptersector);

                                sp_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        sector=adapterView.getItemAtPosition(i)+"";
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                            }

                            else if (district.equals("Musanze")){
                                List<String> listsector = new ArrayList<String>();

                                listsector.add("Busogo");
                                listsector.add("Cyuve");
                                listsector.add("Gacaca");
                                listsector.add("Gashaki");
                                listsector.add("Gataraga");
                                listsector.add("Kimonyi");
                                listsector.add("Kinigi");
                                listsector.add("Muhoza");
                                listsector.add("Muko");
                                listsector.add("Musanze");
                                listsector.add("Nkotsi");
                                listsector.add("Nyange");
                                listsector.add("Remera");
                                listsector.add("Rwaza");
                                listsector.add("Shingiro");
                                // Creating adapter for spinner
                                ArrayAdapter<String> dataAdaptersector = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listsector);
                                // Drop down layout style - list view with radio button
                                dataAdaptersector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp_sector.setAdapter(dataAdaptersector);

                                sp_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        sector=adapterView.getItemAtPosition(i)+"";
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                            }
                            else if (district.equals("Rulindo")){
                                List<String> listsector = new ArrayList<String>();

                                listsector.add("Base");
                                listsector.add("Burega");
                                listsector.add("Bushoki");
                                listsector.add("Buyoga");
                                listsector.add("Cyinzuzi");
                                listsector.add("Cyungo");
                                listsector.add("Kinihira");
                                listsector.add("Kisaro");
                                listsector.add("Masoro");
                                listsector.add("Mbogo");
                                listsector.add("Murambi");
                                listsector.add("Ngoma");
                                listsector.add("Ntarabana");
                                listsector.add("Rukozo");
                                listsector.add("Rusiga");
                                listsector.add("Shyorongi");
                                listsector.add("Tumba");
                                // Creating adapter for spinner
                                ArrayAdapter<String> dataAdaptersector = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listsector);
                                // Drop down layout style - list view with radio button
                                dataAdaptersector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp_sector.setAdapter(dataAdaptersector);

                                sp_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        sector=adapterView.getItemAtPosition(i)+"";
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                            }




                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }
                else if (province.equals(getString(R.string.west))){

                    List<String> listdis = new ArrayList<String>();
                    listdis.add("Karongi");
                    listdis.add("Ngororero");
                    listdis.add("Nyabihu");
                    listdis.add("Nyamasheke");
                    listdis.add("Rubavu");
                    listdis.add("Rusizi");
                    listdis.add("Rutsiro");


                    // Creating adapter for spinner
                    ArrayAdapter<String> dataAdapterdis = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listdis);
                    // Drop down layout style - list view with radio button
                    dataAdapterdis.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_district.setAdapter(dataAdapterdis);

                    sp_district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            district=adapterView.getItemAtPosition(i).toString();

                             if (district.equals("Karongi")){
                                List<String> listsector = new ArrayList<String>();

                                listsector.add("Bwishyura");
                                listsector.add("Gishyita");
                                listsector.add("Gishari");
                                listsector.add("Gitesi");
                                listsector.add("Mubuga");
                                listsector.add("Murambi");
                                listsector.add("Murundi");
                                listsector.add("Mutuntu");
                                listsector.add("Rubengera");
                                listsector.add("Rugabano");
                                listsector.add("Ruganda");
                                listsector.add("Rwankuba");
                                listsector.add("Twumba");
                                listsector.add("Rusiga");
                                listsector.add("Shyorongi");
                                listsector.add("Tumba");
                                // Creating adapter for spinner
                                ArrayAdapter<String> dataAdaptersector = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listsector);
                                // Drop down layout style - list view with radio button
                                dataAdaptersector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp_sector.setAdapter(dataAdaptersector);

                                sp_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        sector=adapterView.getItemAtPosition(i)+"";
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                            }
                             else if (district.equals("Ngororero")){
                                 List<String> listsector = new ArrayList<String>();

                                 listsector.add("Bwira");
                                 listsector.add("Gatumba");
                                 listsector.add("Hindiro");
                                 listsector.add("Kabaya");
                                 listsector.add("Kageyo");
                                 listsector.add("Kavumu");
                                 listsector.add("Matyazo");
                                 listsector.add("Muhanda");
                                 listsector.add("Muhororo");
                                 listsector.add("Ndaro");
                                 listsector.add("Ngororero");
                                 listsector.add("Nyange");
                                 listsector.add("Sovu");
                                 listsector.add("Rusiga");
                                 // Creating adapter for spinner
                                 ArrayAdapter<String> dataAdaptersector = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listsector);
                                 // Drop down layout style - list view with radio button
                                 dataAdaptersector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                 sp_sector.setAdapter(dataAdaptersector);

                                 sp_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                     @Override
                                     public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                         sector=adapterView.getItemAtPosition(i)+"";
                                     }

                                     @Override
                                     public void onNothingSelected(AdapterView<?> adapterView) {

                                     }
                                 });

                             }
                             else if (district.equals("Nyabihu")){
                                 List<String> listsector = new ArrayList<String>();

                                 listsector.add("Mukamira");
                                 listsector.add("Bigogwe");
                                 listsector.add("Jenda");
                                 listsector.add("Jomba");
                                 listsector.add("Kabatwa");
                                 listsector.add("Karago");
                                 listsector.add("Kintobo");
                                 listsector.add("Mukamira");
                                 listsector.add("Muringa");
                                 listsector.add("Rambura");
                                 listsector.add("Rugera");
                                 listsector.add("Rurembo");
                                 listsector.add("Shyira");
                                 // Creating adapter for spinner
                                 ArrayAdapter<String> dataAdaptersector = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listsector);
                                 // Drop down layout style - list view with radio button
                                 dataAdaptersector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                 sp_sector.setAdapter(dataAdaptersector);

                                 sp_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                     @Override
                                     public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                         sector=adapterView.getItemAtPosition(i)+"";
                                     }

                                     @Override
                                     public void onNothingSelected(AdapterView<?> adapterView) {

                                     }
                                 });

                             }
                             else if (district.equals("Nyamasheke")){
                                 List<String> listsector = new ArrayList<String>();

                                 listsector.add("Ruharambuga");
                                 listsector.add("Bushekeri");
                                 listsector.add("Bushenge");
                                 listsector.add("Cyato");
                                 listsector.add("Gihombo");
                                 listsector.add("Kagano");
                                 listsector.add("Kanjongo");
                                 listsector.add("Karambi");
                                 listsector.add("Karengera");
                                 listsector.add("Kirimbi");
                                 listsector.add("Macuba");
                                 listsector.add("Nyabitekeri");
                                 listsector.add("Mahembe");
                                 listsector.add("Rangiro");
                                 listsector.add("Shangi");
                                 // Creating adapter for spinner
                                 ArrayAdapter<String> dataAdaptersector = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listsector);
                                 // Drop down layout style - list view with radio button
                                 dataAdaptersector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                 sp_sector.setAdapter(dataAdaptersector);

                                 sp_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                     @Override
                                     public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                         sector=adapterView.getItemAtPosition(i)+"";
                                     }

                                     @Override
                                     public void onNothingSelected(AdapterView<?> adapterView) {

                                     }
                                 });

                             }
                             else if (district.equals("Rubavu")){
                                 List<String> listsector = new ArrayList<String>();

                                 listsector.add("Bugeshi");
                                 listsector.add("Busasamana");
                                 listsector.add("Cyanzarwe");
                                 listsector.add("Gisenyi");
                                 listsector.add("Kanama");
                                 listsector.add("Kanzenze");
                                 listsector.add("Mudende");
                                 listsector.add("Nyakiliba");
                                 listsector.add("Nyamyumba");
                                 listsector.add("Nyundo");
                                 listsector.add("Rubavu");
                                 listsector.add("Rugerero");
                                 // Creating adapter for spinner
                                 ArrayAdapter<String> dataAdaptersector = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listsector);
                                 // Drop down layout style - list view with radio button
                                 dataAdaptersector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                 sp_sector.setAdapter(dataAdaptersector);

                                 sp_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                     @Override
                                     public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                         sector=adapterView.getItemAtPosition(i)+"";
                                     }

                                     @Override
                                     public void onNothingSelected(AdapterView<?> adapterView) {

                                     }
                                 });

                             }
                             else if (district.equals("Rusizi")){
                                 List<String> listsector = new ArrayList<String>();

                                 listsector.add("Bugarama");
                                 listsector.add("Butare");
                                 listsector.add("Bweyeye");
                                 listsector.add("Gikundamvura");
                                 listsector.add("Gashonga");
                                 listsector.add("Giheke");
                                 listsector.add("Gihundwe");
                                 listsector.add("Gitambi");
                                 listsector.add("Kamembe");
                                 listsector.add("Muganza");
                                 listsector.add("Mururu");
                                 listsector.add("Nkanka");
                                 listsector.add("Nkombo");
                                 listsector.add("Nkungu");
                                 listsector.add("Nyakabuye");
                                 listsector.add("Nyakarenzo");
                                 listsector.add("Nzahaha");
                                 listsector.add("Rwimbogo");


                                 // Creating adapter for spinner
                                 ArrayAdapter<String> dataAdaptersector = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listsector);
                                 // Drop down layout style - list view with radio button
                                 dataAdaptersector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                 sp_sector.setAdapter(dataAdaptersector);

                                 sp_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                     @Override
                                     public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                         sector=adapterView.getItemAtPosition(i)+"";
                                     }

                                     @Override
                                     public void onNothingSelected(AdapterView<?> adapterView) {

                                     }
                                 });

                             }
                             else if (district.equals("Rutsiro")){
                                 List<String> listsector = new ArrayList<String>();

                                 listsector.add("Boneza");
                                 listsector.add("Gihango");
                                 listsector.add("Kigeyo");
                                 listsector.add("Kivumu");
                                 listsector.add("Manihira");
                                 listsector.add("Mukura");
                                 listsector.add("Murunda");
                                 listsector.add("Musasa");
                                 listsector.add("Mushonyi");
                                 listsector.add("Mushubati");
                                 listsector.add("Nyabirasi");
                                 listsector.add("Ruhango");
                                 listsector.add("Rusebeya");



                                 // Creating adapter for spinner
                                 ArrayAdapter<String> dataAdaptersector = new ArrayAdapter<String>(PatientDetailsActivity.this, android.R.layout.simple_spinner_item, listsector);
                                 // Drop down layout style - list view with radio button
                                 dataAdaptersector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                 sp_sector.setAdapter(dataAdaptersector);

                                 sp_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                     @Override
                                     public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                         sector=adapterView.getItemAtPosition(i)+"";
                                     }

                                     @Override
                                     public void onNothingSelected(AdapterView<?> adapterView) {

                                     }
                                 });

                             }



                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        List<String> listtypevacc = new ArrayList<String>();

        listtypevacc.add(" AstraZenecca");
        listtypevacc.add("Moderna");
        listtypevacc.add("Pfizer");
        listtypevacc.add("Johnson Johnson");
        listtypevacc.add("Sputnik-V");
        listtypevacc.add("Snovak");
        listtypevacc.add("Other");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdaptertyepVacc = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listtypevacc);
        // Drop down layout style - list view with radio button
        dataAdaptertyepVacc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_VaccineType.setAdapter(dataAdaptertyepVacc);

        spinner_VaccineType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                VaxxineType=adapterView.getItemAtPosition(i) + "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        List<String> listgender = new ArrayList<String>();

        listgender.add(getString(R.string.female));
        listgender.add(getString(R.string.male));
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdaptergender = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listgender);
        // Drop down layout style - list view with radio button
        dataAdaptergender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_gender.setAdapter(dataAdaptergender);

        sp_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(PatientDetailsActivity.this, "sex is " + parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();

                gender = parent.getItemAtPosition(position) + "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp_residence.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                residency = parent.getItemAtPosition(position).toString();
            //    Toast.makeText(PatientDetailsActivity.this, "country of residence is " + residency, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btn_next = findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);
        btn_back = findViewById(R.id.btn_backpat);
        btn_back.setOnClickListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        nationality = parent.getItemAtPosition(position).toString();
        //Toast.makeText(this, "taped " + nationality, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btndatepicker:

// Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(PatientDetailsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                dob = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;

                                tvdob.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                month = (monthOfYear + 1) + "";
                                yearr = year + "";
                                date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();


                break;

            case R.id.btn_next:
                fn = etfirstname.getText().toString().trim();
                lastname = etlastname.getText().toString().trim();
                national_ID = etnational_ID.getText().toString().trim();
                cell = etcell.getText().toString().trim();
                village = etvillage.getText().toString().trim();
                occupation=etoccupation.getText().toString().trim();
                patienttelephone=etpatienttelephone.getText().toString().trim();
                numberhousehold=et_numberhousehold.getText().toString().trim();

                if (fn.isEmpty() || lastname.isEmpty() || national_ID.isEmpty() ||
                 occupation.isEmpty() || patienttelephone.isEmpty() ||numberhousehold.isEmpty()  ){
                    Toast.makeText(getBaseContext(), R.string.enter_all_fieldss, Toast.LENGTH_LONG).show();
                }else {

                   // etpatienttelephone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());



                    if (etpatienttelephone.getText().toString().trim().length()<9 ||etpatienttelephone.getText().toString().trim().length()>13){
                        Toast.makeText(getBaseContext(), R.string.valid_phone, Toast.LENGTH_LONG).show();

                    }else {
                        progresso.show();
                        if (checkindex.equals("no")){
                            new AgentManager().savecategory(this, getIntent().getStringExtra("category"));

                            Intent intent = new Intent(PatientDetailsActivity.this, QuestionActivity.class);
                            intent.putExtra("firstname", fn);
                            intent.putExtra("lastname", lastname);
                            intent.putExtra("national_ID", etnational_ID.getText().toString().trim());
                            intent.putExtra("patientgender", gender);
                            intent.putExtra("patienttelephone", etpatienttelephone.getText().toString().trim());
                            intent.putExtra("dob", tvdob.getText().toString());
                            intent.putExtra("occupation", etoccupation.getText().toString().trim());
                            intent.putExtra("residence", residency);
                            intent.putExtra("nationality", nationality);
                            intent.putExtra("province", province);
                            intent.putExtra("district", district);
                            intent.putExtra("sector", sector);
                            intent.putExtra("cell", cell);
                            intent.putExtra("village", village);
                            intent.putExtra("rdt_result", "null");
                            intent.putExtra("Indexi", tvindexcode.getText().toString().trim());
                            intent.putExtra("number_household", numberhousehold);
                            if (NulledVaccination.equals("no")) {

                                intent.putExtra("vaccine_type", "null");
                                intent.putExtra("vaccine_dose", "null");
                            } else if (NulledVaccination.equals("yes")) {
                                intent.putExtra("vaccine_type", VaxxineType);
                                intent.putExtra("vaccine_dose", Number_Dose);
                            }
                            startActivity(intent);
                            overridePendingTransition(R.anim.fadein, R.anim.fadeout);

                        }
                         // finish();

                    }
                }
                break;

            case R.id.btn_back:
                startActivity(new Intent(PatientDetailsActivity.this,PatientDetailsActivity.class));
                overridePendingTransition(R.anim.fadeout, R.anim.fadein);
                finish();
                break;

        }
    }

    private void callNIDAPI(){
        progressDialogi.show();
        IndexCode index =new IndexCode();
        index.setIndex(etnational_ID.getText().toString().trim());
        
        Map<String, String> param = new HashMap<>();
        param.put("NID", etnational_ID.getText().toString().trim());
        out(param);
    }
    private void out(final Map<String, String> user) {
        Call<ResponseBody> request = RESTApiClient.getInstance().getApi().sendNID(user);

        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //Response
                if (Integer.toString(response.code()).equals("200")) {
                    progressDialogi.dismiss();
                    laypatientdatahide.setVisibility(View.VISIBLE);
                    layindex.setVisibility(View.VISIBLE);
                 
                    try {
                        String jsondata = response.body().string();

                        if (jsondata != null) {

                           
                            JSONObject reader = new JSONObject(jsondata);

                            statusNID = reader.getString("status");
                           codeNID=reader.getString("code");
                            messageNID=reader.getString("message");
                            if (statusNID.equals("200")) {
            checkindex="no";
                                    tvindexcode.setText(codeNID);
                                btn_next.setVisibility(View.VISIBLE);
                                JSONObject jsonObject = new JSONObject(jsondata).getJSONObject("data");
                                lNameNID=jsonObject.getString("lName");
                                fNameNID=jsonObject.getString("fName");
                                dobNID=jsonObject.getString("dob");
                                nationalityNID=jsonObject.getString("natinality");
                                etfirstname.setText(fNameNID);
                                etlastname.setText(lNameNID);
                                tvdob.setText(dobNID);

                                Log.e("Done",lNameNID+fNameNID+dobNID+nationalityNID);

                            }
                            if (statusNID.equals("404")) {

                                tvindexcode.setText("N/A");
                               Toast.makeText(getBaseContext(), R.string.not_in_covid_system, Toast.LENGTH_LONG).show();
                               btn_next.setVisibility(View.VISIBLE);

                                checkindex="yes";

                                callreservedindexes();


                            }

                            if (getIntent().getStringExtra("category").equals("contact")){
                                Log.e("contact ",codeNID+"-\n"+getIntent().getStringExtra("uuid"));
                                tvindexcode.setText(codeNID+" - "+getIntent().getStringExtra("uuid"));
                                btn_next.setVisibility(View.VISIBLE);
                            }
                            Log.e("Data from  DB",statusNID+"\n"+codeNID+"\n"+messageNID);

                        }else{
                            
                            Toast.makeText(getBaseContext(), R.string.response_error, Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                      //  Toast.makeText(PatientDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                       // Toast.makeText(PatientDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }else{
                    progressDialogi.dismiss();
                  
                    Toast.makeText(PatientDetailsActivity.this, getString(R.string.check_internet)+response.message(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //failure
                progressDialogi.dismiss();
                Toast.makeText(PatientDetailsActivity.this, getString(R.string.check_internet)+"\nError"+t.getLocalizedMessage().toLowerCase(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onclickcallapisearchnid(View view){
        if (etnational_ID.getText().toString().isEmpty()){
            Toast.makeText(getBaseContext(), R.string.enter_nid, Toast.LENGTH_LONG).show();
        }else{
        callNIDAPI();
    }
    }

    private void callreservedindexes(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.setBasicAuth("EAC_test","EACPass@2021");
        client.get("http://161.97.184.144:8080/api/33/trackedEntityAttributes/MSWzPQhISym/generateAndReserve?numberToReserve=1", new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                //Here response will be received in form of JSONArray
                Log.e("index-reserved",response.toString());
                try {
                   JSONArray jsonArray =new JSONArray(response.toString());
                   for (int i=0;i<jsonArray.length();i++){
                       JSONObject item=jsonArray.getJSONObject(i);
                       String value =item.getString("value");
                      progresso.dismiss();
                       tvindexcode.setText(value);
                      // Toast.makeText(getBaseContext(), "data is "+value, Toast.LENGTH_LONG).show();
                       if (checkindex.equals("yes")){
                           new AgentManager().savecategory(PatientDetailsActivity.this, getIntent().getStringExtra("category"));

                           Intent intent = new Intent(PatientDetailsActivity.this, QuestionActivity.class);
                           intent.putExtra("firstname", fn);
                           intent.putExtra("lastname", lastname);
                           intent.putExtra("national_ID", etnational_ID.getText().toString().trim());
                           intent.putExtra("patientgender", gender);
                           intent.putExtra("patienttelephone", etpatienttelephone.getText().toString().trim());
                           intent.putExtra("dob", tvdob.getText().toString());
                           intent.putExtra("occupation", etoccupation.getText().toString().trim());
                           intent.putExtra("residence", residency);
                           intent.putExtra("nationality", nationality);
                           intent.putExtra("province", province);
                           intent.putExtra("district", district);
                           intent.putExtra("sector", sector);
                           intent.putExtra("cell", cell);
                           intent.putExtra("village", village);
                           intent.putExtra("rdt_result", "null");
                           intent.putExtra("Indexi", tvindexcode.getText().toString().trim());
                           intent.putExtra("number_household", numberhousehold);
                           if (NulledVaccination.equals("no")) {

                               intent.putExtra("vaccine_type", "null");
                               intent.putExtra("vaccine_dose", "null");
                           } else if (NulledVaccination.equals("yes")) {
                               intent.putExtra("vaccine_type", VaxxineType);
                               intent.putExtra("vaccine_dose", Number_Dose);
                           }
                           startActivity(intent);
                           overridePendingTransition(R.anim.fadein, R.anim.fadeout);

                       }
                   }

                }
                catch (JSONException e) {
                    progresso.dismiss();
                    e.printStackTrace();
                }


            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                //Here response will be received in form of JSONObject
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getApplicationContext(), "We got an error", Toast.LENGTH_SHORT).show();
                Log.e("index-error",responseString.toString());
                progresso.dismiss();
            }
        });
    }

}