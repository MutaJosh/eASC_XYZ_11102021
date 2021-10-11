package com.betterise.maladiecorona;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.rdtoolkit.support.interop.RdtIntentBuilder;
import org.rdtoolkit.support.model.session.ProvisionMode;
import org.rdtoolkit.support.model.session.TestSession;

import java.util.UUID;

public class PatientProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private AppCompatTextView result_namesprofile,result_genderprofile,result_telephoneprofile,
            result_dobprofile,result_nidprofile,result_nationalityprofile,result_addressprofile,
            result_ascovprofile,result_rdtresultprofile;

    private TextView tvcaseindex,tvcategory;
    private ImageView btn_backprofile;
    private String databaseId,patientName,patientID;
    private Button btn_startprofile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);
        btn_startprofile=findViewById(R.id.btn_startprofile);
        btn_startprofile.setOnClickListener(this);
        btn_backprofile=findViewById(R.id.btn_backprofile);
        btn_backprofile.setOnClickListener(this);

        tvcaseindex=findViewById(R.id.tvcaseindex);
        tvcategory=findViewById(R.id.tvcategory);
        result_namesprofile=findViewById(R.id.result_namesprofile);
        result_genderprofile=findViewById(R.id.result_genderprofile);
        result_telephoneprofile=findViewById(R.id.result_telephoneprofile);
        result_dobprofile=findViewById(R.id.result_dobprofile);
        result_nidprofile=findViewById(R.id.result_nidprofile);
        result_nationalityprofile=findViewById(R.id.result_nationalityprofile);
        result_addressprofile=findViewById(R.id.result_addressprofile);
        result_ascovprofile=findViewById(R.id.result_ascovprofile);
        result_rdtresultprofile=findViewById(R.id.result_rdtresulti);

        patientID=getIntent().getStringExtra("national_ID");
        patientName=getIntent().getStringExtra("firstname")+getIntent().getStringExtra("lastname");
        databaseId= UUID.randomUUID().toString();


        result_namesprofile.setText(getIntent().getStringExtra("firstname")+getIntent().getStringExtra("lastname"));
        result_genderprofile.setText(getIntent().getStringExtra("patientgender"));
        result_telephoneprofile.setText(getIntent().getStringExtra("patienttelephone"));
        result_dobprofile.setText(getIntent().getStringExtra("dob"));
        result_nidprofile.setText(getIntent().getStringExtra("national_ID"));
        result_nationalityprofile.setText(getIntent().getStringExtra("nationality"));
        result_addressprofile.setText(getIntent().getStringExtra("district")+" "+getIntent().getStringExtra("sector"));
        result_ascovprofile.setText(getIntent().getStringExtra("eascov_result"));
        result_rdtresultprofile.setText(getIntent().getStringExtra("rdt_result"));
        tvcaseindex.setText(getIntent().getStringExtra("Index"));
        tvcategory.setText(getIntent().getStringExtra("category"));


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_backprofile:
                finish();
                break;
            case R.id.btn_startprofile:  finish();break;
        }
    }

    // Starting the RDT activity - Test Provisioning
    public void openrdttest(View view) {

        Intent i = RdtIntentBuilder.forProvisioning()
                .setSessionId(databaseId) // Explicitly declare an ID for the session
                .requestProfileCriteria("sars_cov2", ProvisionMode.CRITERIA_SET_AND) //.requestProfileCriteria("sd_bioline_mal_pf_pv carestart_mal_pf_pv", ProvisionMode.CRITERIA_SET_OR)
                .setFlavorOne(patientName) // Text to differentiate running tests
                .setFlavorTwo(patientID) // Text to differentiate running tests
                .build();


    }









}