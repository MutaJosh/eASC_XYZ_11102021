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
import android.content.res.Configuration;
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
import java.util.UUID;

import okhttp3.ResponseBody;
import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientDetailsActivity extends AppCompatActivity implements  View.OnClickListener {

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
    private String country,zip,state,city,address,
            longitude,latitude,checkindex, nationality, gender, yearr, month, date, residency, dob,
            numberhousehold="null", VaxxineType="null", Number_Dose="null", NulledVaccination="null";
    private ImageButton btndatepicker;
    private Button btn_next;
    private ImageView btn_back;
    private TextView tvdob;
    private Spinner spinner_VaccineType;
    private int mYear, mMonth, mDay;
    private LinearLayout lay_vaccine;
    private TextView tvindexcode;
    private EditText etfirstname,
            et_numberhousehold, etlastname,
            etnational_ID, etpatientgender, etpatienttelephone,
            etoccupation, et_residence, et_nationality, et_province,et_district,et_sector,et_cell, et_village;
    private String fn, lastname, national_ID, patientgender,
            patienttelephone, occupation, residence, province, district, sector, cell, village;
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
    ArrayList<Province> nationalityList = new ArrayList<Province>();
    ArrayList<Province> residenceList = new ArrayList<Province>();
    ArrayAdapter<Province> provinceAdapter;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);
        //set default language to kinyarwanda instead of english

        String lang = "rw";
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // method to get the location
        getLastLocation();

        progressDialogi=new ProgressDialog(PatientDetailsActivity.this);
        progressDialogi.setMessage(getString(R.string.loading));
        progressDialogi.setCanceledOnTouchOutside(false);

        progressiio=new ProgressDialog(PatientDetailsActivity.this);
        progressiio.setMessage(getString(R.string.loading));
        progressiio.setCanceledOnTouchOutside(false);


       et_nationality=findViewById(R.id.et_nationality);
       et_residence=findViewById(R.id.et_residence);
       et_province=findViewById(R.id.et_province);
       et_district=findViewById(R.id.et_district);
       et_sector=findViewById(R.id.et_sector);
       et_cell=findViewById(R.id.et_cell);
       et_village=findViewById(R.id.et_village);

        laypatientdatahide=findViewById(R.id.laypatientdatahide);
        layindex=findViewById(R.id.layindex);

        tvindexcode=findViewById(R.id.tvindexcode);
        spinner_VaccineType=findViewById(R.id.spinner_VaccineType);
        spinner_Number_Dose=findViewById(R.id.spinner_Number_Dose);
        lay_vaccine=findViewById(R.id.lay_vaccine);
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


        List<String> listnumbervacc = new ArrayList<String>();

        listnumbervacc.add(getString(R.string.dose1));
        listnumbervacc.add(getString(R.string.dose2));
        listnumbervacc.add(getString(R.string.dose3));
        listnumbervacc.add(getString(R.string.dontknow));


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


        List<String> listtypevacc = new ArrayList<String>();

        listtypevacc.add(" AstraZenecca");
        listtypevacc.add("Moderna");
        listtypevacc.add("Pfizer");
        listtypevacc.add("Johnson Johnson");
        listtypevacc.add("Sputnik-V");
        listtypevacc.add("Snovak");
        listtypevacc.add(getString(R.string.dontknow));
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

        btn_next = findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);
        btn_back = findViewById(R.id.btn_backpat);
        btn_back.setOnClickListener(this);



       String uuid= UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();

       if(getIntent().getStringExtra("category").equals("contact")){
           tvindexcode.setText(getIntent().getStringExtra("uuid")+"-"+uuid.substring(0,8));
       }else{ tvindexcode.setText(uuid.substring(0,8));}
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
                 occupation.isEmpty() || patienttelephone.isEmpty() ||numberhousehold.isEmpty() ||
                et_nationality.getText().toString().trim().isEmpty()||et_residence.getText().toString().trim().isEmpty()||
                et_province.getText().toString().trim().isEmpty()||et_district.getText().toString().trim().isEmpty()||
                et_sector.getText().toString().trim().isEmpty()||et_cell.getText().toString().trim().isEmpty()
                        ||et_village.getText().toString().trim().isEmpty()){
                    Toast.makeText(getBaseContext(), R.string.enter_all_fieldss, Toast.LENGTH_LONG).show();
                }else {

                   // etpatienttelephone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

                    if (etpatienttelephone.getText().toString().trim().length()<9 ||etpatienttelephone.getText().toString().trim().length()>13){
                        Toast.makeText(getBaseContext(), R.string.valid_phone, Toast.LENGTH_LONG).show();

                    }else {
                            new AgentManager().savecategory(this, getIntent().getStringExtra("category"));

                            Intent intent = new Intent(PatientDetailsActivity.this, QuestionActivity.class);
                            intent.putExtra("firstname", fn);
                            intent.putExtra("lastname", lastname);
                            intent.putExtra("national_ID", etnational_ID.getText().toString().trim());
                            intent.putExtra("patientgender", gender);
                            intent.putExtra("patienttelephone", etpatienttelephone.getText().toString().trim());
                            intent.putExtra("dob", tvdob.getText().toString());
                            intent.putExtra("occupation", etoccupation.getText().toString().trim());
                            intent.putExtra("residence", et_residence.getText().toString().trim());
                            intent.putExtra("nationality", et_nationality.getText().toString().trim());
                            intent.putExtra("province", et_province.getText().toString().trim());
                            intent.putExtra("district", et_district.getText().toString().trim());
                            intent.putExtra("sector", et_sector.getText().toString().trim());
                            intent.putExtra("cell", et_cell.getText().toString().trim());
                            intent.putExtra("village", et_village.getText().toString().trim());
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
                break;

            case R.id.btn_back:
                startActivity(new Intent(PatientDetailsActivity.this,PatientDetailsActivity.class));
                overridePendingTransition(R.anim.fadeout, R.anim.fadein);
                finish();
                break;

        }
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