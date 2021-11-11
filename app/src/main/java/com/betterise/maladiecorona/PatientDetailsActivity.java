package com.betterise.maladiecorona;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.betterise.maladiecorona.networking.singleton.RESTApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientDetailsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {


    private String statusNID,codeNID,messageNID,fNameNID,lNameNID,dobNID,nationalityNID;

    private static final int PRIVATE_MODE =0 ;
  private AppCompatRadioButton radioYes;
  private LinearLayout laypatientdatahide,layindex;

private RadioGroup radiogrouptype;
    private Spinner sp_gender, sp_nationality, sp_residence,spinner_Number_Dose,sp_district,sp_province;
    private String nationality, gender, yearr, month, date, residency, dob,numberhousehold,VaxxineType,Number_Dose,NulledVaccination;
    private ImageButton btndatepicker;
    private Button btn_next;
    private ImageView btn_back;
    private TextView tvdob;
    private Spinner spinner_VaccineType;
    private int mYear, mMonth, mDay;
    private LinearLayout lay_vaccine;
    private TextView tvindexcode;
    private EditText etfirstname,et_numberhousehold, etlastname, etnational_ID, etpatientgender, etpatienttelephone, etoccupation, etresidence, etnationality, etsector, etcell, etvillage;
    private String fn, lastname, national_ID, patientgender, patienttelephone, occupation, residence, province, district, sector, cell, village;
public static final String PREF_FIRSTNAME = "firstname";
    public static final String PREF_LASTNAME = "lastname";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);

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


        radiogrouptype.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioYes =(AppCompatRadioButton) findViewById(selectedId);

                if (radioYes.getText().equals("Yes")){
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
        etsector=findViewById(R.id.etsector);
        etcell=findViewById(R.id.etcell);
        etvillage=findViewById(R.id.etvillage);

        sp_gender = findViewById(R.id.sp_gender);
        sp_nationality = findViewById(R.id.sp_nationality);
        sp_residence = findViewById(R.id.spresidence);

        tvdob = findViewById(R.id.tvdob);
        btndatepicker = findViewById(R.id.btndatepicker);
        btndatepicker.setOnClickListener(this);


       // Toast.makeText(PatientDetailsActivity.this, "clicked outside", Toast.LENGTH_SHORT).show();


        etnational_ID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //callNIDAPI();
            }
        });


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

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        List<String> listdis = new ArrayList<String>();

        listdis.add("Gasabo");
        listdis.add("Kicukiro");
        listdis.add("Nyarugenge");
        listdis.add("Bugesera");
        listdis.add("Burera");
        listdis.add("Gakenke");
        listdis.add("Gatsibo");
        listdis.add("Gicumbi");
        listdis.add("Gisagara");
        listdis.add("Huye");
        listdis.add("Kamonyi");
        listdis.add("Karongi");
        listdis.add("Kayonza");
        listdis.add("Kirehe");
        listdis.add("Muhanga");
        listdis.add("Musanze");
        listdis.add("Ngoma");
        listdis.add("Ngororero");
        listdis.add("Nyabihu");
        listdis.add("Nyagatare");
        listdis.add("Nyamagabe");
        listdis.add("Nyamasheke");
        listdis.add("Nyanza");
        listdis.add("Nyaruguru");
        listdis.add("Rubavu");
        listdis.add("Ruhango");
        listdis.add("Rulindo");
        listdis.add("Rusizi");
        listdis.add("Rutsiro");
        listdis.add("Rwamagana");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapterdis = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listdis);
        // Drop down layout style - list view with radio button
        dataAdapterdis.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_district.setAdapter(dataAdapterdis);


        sp_district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                district=adapterView.getItemAtPosition(i).toString();
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

        listgender.add("Female");
        listgender.add("Male");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdaptergender = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listgender);
        // Drop down layout style - list view with radio button
        dataAdaptergender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_gender.setAdapter(dataAdaptergender);

        sp_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
             //   Toast.makeText(PatientDetailsActivity.this, "sex is " + parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();

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
                sector = etsector.getText().toString().trim();
                cell = etcell.getText().toString().trim();
                village = etvillage.getText().toString().trim();
                occupation=etoccupation.getText().toString().trim();
                patienttelephone=etpatienttelephone.getText().toString().trim();
                numberhousehold=et_numberhousehold.getText().toString().trim();

                if (fn.isEmpty() || lastname.isEmpty() || national_ID.isEmpty() ||  sector.isEmpty() || sector.isEmpty() || cell.isEmpty()
                || village.isEmpty() || occupation.isEmpty() || patienttelephone.isEmpty() ||numberhousehold.isEmpty()  ){
                    Toast.makeText(getBaseContext(), R.string.enter_all_fieldss, Toast.LENGTH_LONG).show();
                }else {

                    new AgentManager().savecategory(this,getIntent().getStringExtra("category"));

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
                    intent.putExtra("rdt_result","null");
                    intent.putExtra("Indexi",tvindexcode.getText().toString().trim());
                    intent.putExtra("number_household",numberhousehold);
                    if (NulledVaccination.equals("no")){

                        intent.putExtra("vaccine_type", "null");
                        intent.putExtra("vaccine_dose", "null");
                    }else if(NulledVaccination.equals("yes")){
                        intent.putExtra("vaccine_type", VaxxineType);
                        intent.putExtra("vaccine_dose", Number_Dose);
                    }
                    startActivity(intent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    finish();

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
                                tvindexcode.setText(codeNID);
                                Toast.makeText(getBaseContext(), R.string.not_in_covid_system, Toast.LENGTH_LONG).show();
                                btn_next.setVisibility(View.VISIBLE);
                            }

                            if (getIntent().getStringExtra("category").equals("contact")){
                                Log.e("contact ",codeNID+"-\n"+getIntent().getStringExtra("uuid"));
                                tvindexcode.setText(codeNID+" - "+getIntent().getStringExtra("uuid"));
                                btn_next.setVisibility(View.VISIBLE);
                            }
                            Log.e("Data from  DB",statusNID+"\n"+codeNID+"\n"+messageNID);

                        }else{
                            
                            Toast.makeText(PatientDetailsActivity.this, "response error -null", Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                      //  Toast.makeText(PatientDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                       // Toast.makeText(PatientDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }else{
                  
                    Toast.makeText(PatientDetailsActivity.this, getString(R.string.check_internet)+response.message(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //failure
               
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

}