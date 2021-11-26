package com.betterise.maladiecorona;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.betterise.maladiecorona.managers.AgentManager;
import com.betterise.maladiecorona.model.IndexCode;
import com.betterise.maladiecorona.model.Province;
import com.betterise.maladiecorona.networking.singleton.RESTApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientDetailsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    // FusedLocationProviderClient
    // object
    FusedLocationProviderClient mFusedLocationClient;

    // Initializing other items
    int PERMISSION_ID = 44;


    private String statusNID, codeNID, messageNID, fNameNID, lNameNID, dobNID, nationalityNID;

    private static final int PRIVATE_MODE = 0;
    private AppCompatRadioButton radioYes;
    private LinearLayout laypatientdatahide, layindex;


    ArrayList<IndexCode> mExampleListi;

    private RadioGroup radiogrouptype;
    private Spinner sp_cell,sp_village,sp_gender, sp_nationality, sp_residence, spinner_Number_Dose, sp_district, sp_province, sp_sector;
    private String country,zip,state,city,address, longitude,latitude,checkindex, nationality, gender, yearr, month, date, residency, dob, numberhousehold, VaxxineType, Number_Dose, NulledVaccination;
    private ImageButton btndatepicker;
    private Button btn_next;
    private ImageView btn_back;
    private TextView tvdob;
    private Spinner spinner_VaccineType;
    private int mYear, mMonth, mDay;
    private LinearLayout lay_vaccine;
    private TextView tvindexcode;
    private EditText etfirstname, et_numberhousehold, etlastname, etnational_ID, etpatientgender, etpatienttelephone, etoccupation, etresidence, etnationality, etcell, etvillage;
    private String fn, lastname, national_ID, patientgender, patienttelephone, occupation, residence, province, district, sector, cell, village;
    public static final String PREF_FIRSTNAME = "firstname";
    public static final String PREF_LASTNAME = "lastname";

    private ProgressDialog progressDialogi;

    private ProgressDialog progresso;

    private ProgressDialog progressiio;
    ArrayList<Province> provinceList = new ArrayList<Province>();
    ArrayList<Province> districtList = new ArrayList<Province>();
    ArrayList<Province> sectorList = new ArrayList<Province>();
    ArrayList<Province> cellList = new ArrayList<Province>();
    ArrayList<Province> villageList = new ArrayList<Province>();
    ArrayAdapter<Province> provinceAdapter;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // method to get the location
        getLastLocation();

        progressDialogi=new ProgressDialog(PatientDetailsActivity.this);
        progressDialogi.setMessage(getString(R.string.loading));
        progressDialogi.setCanceledOnTouchOutside(false);

        progressiio=new ProgressDialog(PatientDetailsActivity.this);
        progressiio.setMessage(getString(R.string.loading));
        progressiio.setCanceledOnTouchOutside(false);


        sp_province=findViewById(R.id.sp_province);
        sp_cell=findViewById(R.id.sp_cell);
        sp_village=findViewById(R.id.sp_village);

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

        requestQueue = Volley.newRequestQueue(this);
        String url = "https://rbc.gov.rw/community/data-sharing/api/organisationUnit.php";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("provinces");
                    for(int i=0; i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Province proObj=new Province();
                        proObj.setName(jsonObject.optString("name"));
                        proObj.setId(jsonObject.optString("id"));

                        provinceList.add(proObj);

                        provinceAdapter = new ArrayAdapter<>(PatientDetailsActivity.this,
                                android.R.layout.simple_spinner_item, provinceList);
                        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sp_province.setAdapter(provinceAdapter);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
        sp_province.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                province=adapterView.getItemAtPosition(i).toString();
                 districtList.clear();
               // Toast.makeText(getBaseContext(), "selected province is: "+provinceList.get(i).getId(), Toast.LENGTH_LONG).show();

                String url = "https://rbc.gov.rw/community/data-sharing/api/organisationUnit.php?province="+provinceList.get(i).getId();
                requestQueue = Volley.newRequestQueue(PatientDetailsActivity.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                        url, null, new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("districts");
                            for(int i=0; i<jsonArray.length();i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Province proObj=new Province();
                                proObj.setName(jsonObject.optString("name"));
                                proObj.setId(jsonObject.optString("id"));

                                districtList.add(proObj);

                                ArrayAdapter<Province>     dsAdapter = new ArrayAdapter<>(PatientDetailsActivity.this,
                                        android.R.layout.simple_spinner_item, districtList);
                                dsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp_district.setAdapter(dsAdapter);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                requestQueue.add(jsonObjectRequest);
                sp_district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                       district=adapterView.getItemAtPosition(i).toString();
                       sectorList.clear();
                        //Toast.makeText(getBaseContext(), "choosed district"+districtList.get(i).getId(), Toast.LENGTH_LONG).show();

                        String url = "https://rbc.gov.rw/community/data-sharing/api/organisationUnit.php?province="+provinceList.get(i).getId()+"&district="+districtList.get(i).getId();
                        requestQueue = Volley.newRequestQueue(PatientDetailsActivity.this);
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                                url, null, new com.android.volley.Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray jsonArray = response.getJSONArray("sectors");
                                    for(int i=0; i<jsonArray.length();i++){
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        Province proObj=new Province();
                                        proObj.setName(jsonObject.optString("name"));
                                        proObj.setId(jsonObject.optString("id"));

                                        sectorList.add(proObj);

                                        ArrayAdapter<Province>     sectorAdapter = new ArrayAdapter<>(PatientDetailsActivity.this,
                                                android.R.layout.simple_spinner_item,sectorList);
                                        sectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        sp_sector.setAdapter(sectorAdapter);

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new com.android.volley.Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
                        requestQueue.add(jsonObjectRequest);
                        sp_sector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                           sector=adapterView.getItemAtPosition(i).toString();
                            cellList.clear();

                                //Toast.makeText(getBaseContext(), "sector is"+sectorList.get(i).getId(), Toast.LENGTH_SHORT).show();

                                String url = "https://rbc.gov.rw/community/data-sharing/api/organisationUnit.php?province="+provinceList.get(i).getId()+"&district="+districtList.get(i).getId()+"&sector="+sectorList.get(i).getId();
                                requestQueue = Volley.newRequestQueue(PatientDetailsActivity.this);
                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                                        url, null, new com.android.volley.Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            JSONArray jsonArray = response.getJSONArray("cells");
                                            for(int i=0; i<jsonArray.length();i++){
                                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                                Province proObj=new Province();
                                                proObj.setName(jsonObject.optString("name"));
                                                proObj.setId(jsonObject.optString("id"));

                                                cellList.add(proObj);

                                                ArrayAdapter<Province>     cellAdapter = new ArrayAdapter<>(PatientDetailsActivity.this,
                                                        android.R.layout.simple_spinner_item,cellList);
                                                cellAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                sp_cell.setAdapter(cellAdapter);

                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new com.android.volley.Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                });
                                requestQueue.add(jsonObjectRequest);
                                sp_cell.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                       // Toast.makeText(getBaseContext(), "cell is:  "+cellList.get(i).getId()+cellList.get(i).getName(), Toast.LENGTH_SHORT).show();
                                  cell=adapterView.getItemAtPosition(i).toString();
                                   villageList.clear();
                                        String url = "https://rbc.gov.rw/community/data-sharing/api/organisationUnit.php?province="+provinceList.get(i).getId() +"&district="+districtList.get(i).getId()+"&sector="+sectorList.get(i).getId()+"&cell="+cellList.get(i).getId();
                                        requestQueue = Volley.newRequestQueue(PatientDetailsActivity.this);
                                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET
                                                ,
                                                url, null, new com.android.volley.Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {

                                                try {
                                                    JSONArray jsonArray = response.getJSONArray("villages");
                                                    for(int i=0; i<jsonArray.length();i++){
                                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                                        Province proObj=new Province();
                                                        proObj.setName(jsonObject.optString("name"));
                                                        proObj.setId(jsonObject.optString("id"));

                                                        villageList.add(proObj);

                                                        ArrayAdapter<Province>     villageAdapter = new ArrayAdapter<>(PatientDetailsActivity.this,
                                                                android.R.layout.simple_spinner_item,villageList);
                                                        villageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                        sp_village.setAdapter(villageAdapter);

                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new com.android.volley.Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }
                                        });
                                        requestQueue.add(jsonObjectRequest);
                                        sp_village.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                               // Toast.makeText(getBaseContext(), "village is: "+villageList.get(i).getId()+villageList.get(i).getName(), Toast.LENGTH_SHORT).show();
                                           village=adapterView.getItemAtPosition(i).toString();
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> adapterView) {

                                            }
                                        });

                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });


                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

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
//                        progresso.show();
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


                        }else if (checkindex.equals("yes")){

                            callreservedindexes();
                        }


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

                                tvindexcode.setText(R.string.not_available_index);
                               Toast.makeText(getBaseContext(), R.string.not_in_covid_system, Toast.LENGTH_LONG).show();
                               btn_next.setVisibility(View.VISIBLE);

                                checkindex="yes";



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
        progressiio.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setBasicAuth("EAC_test","EACPass@2021");
        client.get("http://161.97.184.144:8080/api/33/trackedEntityAttributes/MSWzPQhISym/generateAndReserve?numberToReserve=1", new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                progressiio.dismiss();
                //Here response will be received in form of JSONArray
                Log.e("index-reserved",response.toString());
                try {
                   JSONArray jsonArray =new JSONArray(response.toString());
                   for (int i=0;i<jsonArray.length();i++){
                       JSONObject item=jsonArray.getJSONObject(i);
                       String value =item.getString("value");


                       if (getIntent().getStringExtra("category").equals("contact")){
                           tvindexcode.setText((value+" - "+getIntent().getStringExtra("uuid")));

                       }else { tvindexcode.setText(value); }
                       // Toast.makeText(getBaseContext(), "data is "+value, Toast.LENGTH_LONG).show();

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
                       intent.putExtra("rdt_result", "no");
                       intent.putExtra("Indexi", tvindexcode.getText().toString().trim());
                       intent.putExtra("number_household", numberhousehold);
                       if (NulledVaccination.equals("no")) {

                           intent.putExtra("vaccine_type", "no");
                           intent.putExtra("vaccine_dose", "no");
                       } else if (NulledVaccination.equals("yes")) {
                           intent.putExtra("vaccine_type", VaxxineType);
                           intent.putExtra("vaccine_dose", Number_Dose);
                       }
                       startActivity(intent);
                       overridePendingTransition(R.anim.fadein, R.anim.fadeout);

                   }

                }
                catch (JSONException e) {

                    e.printStackTrace();
                }


            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressiio.dismiss();
                //Here response will be received in form of JSONObject
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getApplicationContext(), "We got an error", Toast.LENGTH_SHORT).show();
                Log.e("index-error",responseString.toString());
                progressiio.dismiss();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            latitude=location.getLatitude() + "";
                            longitude=location.getLongitude() + "";


                            Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());


                            try {
                                List<Address> addresses  = null;
                                addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);

                                address = addresses.get(0).getAddressLine(0);
                                 city = addresses.get(0).getLocality();
                               state = addresses.get(0).getAdminArea();
                                zip = addresses.get(0).getPostalCode();
                                country = addresses.get(0).getCountryName();

                                new AgentManager().saveuser_location(PatientDetailsActivity.this, latitude+","+longitude+","+address+","+
                                        city+","+state+","+zip+","+country);

                                Log.e("My location",latitude+"\n"+longitude+"\n"+address+"\n"+city+"\n"+state+"\n"+ zip+"\n"+country);

                               // Toast.makeText(getBaseContext(), "Amerekezo \n"+latitude+"\n"+longitude+"\n"+address+"\n"+city+"\n"+state+"\n"+ zip+"\n"+country, Toast.LENGTH_LONG).show();


                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
            } else {
                Toast.makeText( PatientDetailsActivity.this, R.string.turn_on_location, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }
    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitude=mLastLocation.getLatitude() + "";
            latitude=mLastLocation.getLongitude() + "";
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

}