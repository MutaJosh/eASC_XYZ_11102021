package com.betterise.maladiecorona;

import static com.betterise.maladiecorona.managers.PollManager.PREF_POLLS;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.betterise.maladiecorona.managers.AgentManager;
import com.betterise.maladiecorona.managers.PollManager;
import com.betterise.maladiecorona.managers.QuestionManager;
import com.google.gson.Gson;

import org.rdtoolkit.support.interop.RdtIntentBuilder;
import org.rdtoolkit.support.interop.RdtUtils;
import org.rdtoolkit.support.model.session.ProvisionMode;
import org.rdtoolkit.support.model.session.TestSession;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class PatientProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private AppCompatTextView result_namesprofile,result_genderprofile,result_telephoneprofile,
            result_dobprofile,result_nidprofile,result_nationalityprofile,result_addressprofile,
            result_ascovprofile,result_rdtresultprofile;

    private TextView tvcaseindex,tvcategory;
    private ImageView btn_backprofile;
    private String databaseId,patientName,patientID;
    private Button btn_startprofile,btn_startRdt;

    private static int SPLASH_TIME_OUT = 6000;

    private final static  int RDTOOLKIT_ACTIVITY_REQUEST_CODE =1;
    private final static  int RDTOOLKIT_CAPTURE_RESULT_REQUEST_CODE = 2;

    private final static  int ACTIVITY_PROVISION = 1;
    private final static int ACTIVITY_CAPTURE = 2;
    private String CW_SESSION_ID = UUID.randomUUID().toString();
    private String CLOUDWORKS_DSN = "https://vmi682749.contaboserver.net/ingest/0aa05fe0a88e1ee06df6fa7d74fbcce276d7eadb";
    private String  COVID_TEST_PROFILE = "sd_standard_q_c19";
    private String RDT_PENDING_STATUS = "pending";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);


        btn_startprofile=findViewById(R.id.btn_startprofile);
        btn_startprofile.setOnClickListener(this);
        btn_backprofile=findViewById(R.id.btn_backprofile);
        btn_backprofile.setOnClickListener(this);
        btn_startRdt=findViewById(R.id.btn_startRdt);
        btn_startRdt.setOnClickListener(this);
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
//
//        if (getIntent().getStringExtra("rdt_result").equals("null")){
//            btn_startprofile.setVisibility(View.GONE);
//            btn_startRdt.setVisibility(View.VISIBLE);
//        }else{
//            btn_startprofile.setVisibility(View.VISIBLE);
//            btn_startRdt.setVisibility(View.GONE);
//        }

        result_namesprofile.setText(getIntent().getStringExtra("firstname")+getIntent().getStringExtra("lastname"));
        result_genderprofile.setText(getIntent().getStringExtra("patientgender"));
        result_telephoneprofile.setText(getIntent().getStringExtra("patienttelephone"));
        result_dobprofile.setText(getIntent().getStringExtra("dob"));
        result_nidprofile.setText(getIntent().getStringExtra("national_ID"));
        result_nationalityprofile.setText(getIntent().getStringExtra("nationality"));
        result_addressprofile.setText(getIntent().getStringExtra("district")+", "+getIntent().getStringExtra("sector"));
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
            case R.id.btn_startRdt:
               // Toast.makeText(getBaseContext(), "proceed to rdt", Toast.LENGTH_SHORT).show();

                requestRDTScan();

                break;
        }
    }

//    // Starting the RDT activity - Test Provisioning
//    public void openrdttest(View view) {
//
//        Intent i = RdtIntentBuilder.forProvisioning()
//                .setSessionId(databaseId) // Explicitly declare an ID for the session
//                .requestProfileCriteria("sars_cov2", ProvisionMode.CRITERIA_SET_AND) //.requestProfileCriteria("sd_bioline_mal_pf_pv carestart_mal_pf_pv", ProvisionMode.CRITERIA_SET_OR)
//                .setFlavorOne(patientName) // Text to differentiate running tests
//                .setFlavorTwo(patientID) // Text to differentiate running tests
//                .build();
//
//
//    }





private void requestRDTScan(){

        try {
            Intent intent = RdtIntentBuilder.forProvisioning()
                    .setSessionId(CW_SESSION_ID)
                    .setFlavorOne(getIntent().getStringExtra("firstname")+" "+getIntent().getStringExtra("lastname"))
                    .setFlavorTwo(getIntent().getStringExtra("national_ID"))
                    .setCloudworksBackend(CLOUDWORKS_DSN,"")
                    .requestTestProfile(COVID_TEST_PROFILE)
                    .setSecondaryCaptureRequirements("capture_windowed")
                    .setInTestQaMode()
                    .build();

            startActivityForResult(intent, RDTOOLKIT_ACTIVITY_REQUEST_CODE);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("tag to rdt",e.getMessage());

    }

}


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == ACTIVITY_PROVISION && resultCode == RESULT_OK) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                            Intent intent = RdtIntentBuilder.forCapture()
                                    .setSessionId(CW_SESSION_ID)
                                    .build();
                            startActivityForResult(intent, RDTOOLKIT_CAPTURE_RESULT_REQUEST_CODE);
                        //Toast.makeText(getBaseContext(), "start for capture intent", Toast.LENGTH_SHORT).show();


                    }catch (Exception e){
                        Log.e("Tag ",e.getMessage());
                    }
                }
            }, SPLASH_TIME_OUT);
        }

        if (requestCode == ACTIVITY_CAPTURE && resultCode == RESULT_OK) {
            TestSession session = RdtUtils.getRdtSession(data);
            TestSession.TestResult result = session.getResult();
        //    Toast.makeText(getBaseContext(), "Rdt result is \n"+result.toString(), Toast.LENGTH_LONG).show();


            // new AgentManager().saverdt_result(this,"null");
         //  new PollManager().addPoll(this, new QuestionManager().createPoll(this));

            List<String> u = Arrays.asList(result.toString().split(","));

            String ibisubizoo = u.get(4).substring(u.get(4).indexOf("{") + 1, u.get(4).indexOf("}"));

            //result_rdtresultprofile.setText(ibisubizoo);

//            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(PatientProfileActivity.this);
//            SharedPreferences.Editor editor = prefs.edit();
//            Gson gson = new Gson();
//            String json = gson.toJson(list);
//            editor.putString(PREF_POLLS, json);
//            editor.apply();

             if (ibisubizoo.equals("sars_cov2=sars_cov2_pos")) {
                result_rdtresultprofile.setText(getString(R.string.rdt_result_pos));
            } else if (ibisubizoo.equals("sars_cov2=sars_cov2_neg")) {
                 result_rdtresultprofile.setText(getString(R.string.rdt_result_neg));
            }else{
                result_rdtresultprofile.setText(getString(R.string.rdt_result_invalid));
            }



        }

        }

    }

