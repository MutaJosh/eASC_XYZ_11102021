package com.betterise.maladiecorona;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.betterise.maladiecorona.managers.AgentManager;
import com.betterise.maladiecorona.managers.QuestionManager;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PatientDetailsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {


    private static final int PRIVATE_MODE =0 ;
    /**
     * Created by MJC on 01/07/20.
     */

    private Spinner sp_gender, sp_nationality, sp_residence;
    private String nationality, gender, yearr, month, date, residency, dob;
    private ImageButton btndatepicker;
    private Button btn_next;
    private ImageView btn_back;
    private TextView tvdob;
    private int mYear, mMonth, mDay;
    private TextView tvindexcode;
    private EditText etfirstname, etlastname, etnational_ID, etpatientgender, etpatienttelephone, etoccupation, etresidence, etnationality, etprovince, etdistrict, etsector, etcell, etvillage;
    private String fn, lastname, national_ID, patientgender, patienttelephone, occupation, residence, province, district, sector, cell, village;
public static final String PREF_FIRSTNAME = "firstname";
    public static final String PREF_LASTNAME = "lastname";
    public static final String PREFS="PREFS";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);

        tvindexcode=findViewById(R.id.tvindexcode);

        long l = ByteBuffer.wrap(getIntent().getStringExtra("uuid").toString().getBytes()).getLong();
      String a= Long.toString(l, Character.MAX_RADIX);

        tvindexcode.setTextIsSelectable(true);
        String x=getIntent().getStringExtra("uuid").toString();
        tvindexcode.setText("Index: "+x.substring(9,18));


        etfirstname=findViewById(R.id.firstname);
        etlastname=findViewById(R.id.etlastname);
        etnational_ID=findViewById(R.id.national_ID);
        etpatienttelephone=findViewById(R.id.patient_telephone);
        etoccupation=findViewById(R.id.etoccupation);
        etprovince=findViewById(R.id.etprovince);
        etdistrict=findViewById(R.id.etdistrict);
        etsector=findViewById(R.id.etsector);
        etcell=findViewById(R.id.etcell);
        etvillage=findViewById(R.id.etvillage);

        sp_gender = findViewById(R.id.sp_gender);
        sp_nationality = findViewById(R.id.sp_nationality);
        sp_residence = findViewById(R.id.spresidence);

        tvdob = findViewById(R.id.tvdob);
        btndatepicker = findViewById(R.id.btndatepicker);
        btndatepicker.setOnClickListener(this);

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
                datePickerDialog.show();


                break;

            case R.id.btn_next:



                fn = etfirstname.getText().toString().trim();
                lastname = etlastname.getText().toString().trim();
                national_ID = etnational_ID.getText().toString().trim();
                province = etprovince.getText().toString().trim();
                district = etdistrict.getText().toString().trim();
                sector = etsector.getText().toString().trim();
                cell = etcell.getText().toString().trim();
                village = etvillage.getText().toString().trim();
                occupation=etoccupation.getText().toString().trim();
                patienttelephone=etpatienttelephone.getText().toString().trim();

                if (fn.isEmpty() || lastname.isEmpty() || national_ID.isEmpty() || province.isEmpty() ||
                district.isEmpty() || sector.isEmpty() || sector.isEmpty() || cell.isEmpty()
                || village.isEmpty() || occupation.isEmpty() || patienttelephone.isEmpty()  ){
                    Toast.makeText(this, "Please All fields are required to be filled", Toast.LENGTH_LONG).show();
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

                    startActivity(intent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                   // finish();
                }
                break;

            case R.id.btn_back:
               // startActivity(new Intent(PatientDetailsActivity.this,ActivityChooseCategory.class));
                //overridePendingTransition(R.anim.fadeout, R.anim.fadein);
                finish();
                break;

        }
    }



}