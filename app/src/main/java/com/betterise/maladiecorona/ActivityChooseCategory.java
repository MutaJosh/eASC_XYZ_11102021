package com.betterise.maladiecorona;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.TextViewCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.betterise.maladiecorona.managers.*;

import com.betterise.maladiecorona.model.Index;
import com.betterise.maladiecorona.model.IndexCode;
import com.betterise.maladiecorona.model.Item_session;
import com.betterise.maladiecorona.networking.singleton.RESTApiClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;
import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityChooseCategory extends AppCompatActivity  implements View.OnClickListener {
private AppCompatRadioButton radiobtnindex;
private Button btn_start;
private String category,code,message;
private EditText eTindexcode;
private String value;
private LinearLayout lay_validate;
private ImageView btn_backfromcategory;
private ProgressDialog progressDialog,progressDialog2;
private RadioGroup radioGroup;
    private OtpTextView otpTextView;
private TextView errorBulletcate;
    private static final int TIME_INTERVAL = 3000;
    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String languageToLoad  = "rw"; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        this.setContentView(R.layout.activity_choose_category);

        radioGroup=(RadioGroup) findViewById(R.id.radiogroup);
        btn_start=findViewById(R.id.btn_startcategory);
        btn_backfromcategory=findViewById(R.id.btn_backfromcategory);
        btn_backfromcategory.setOnClickListener(this);
        btn_start.setOnClickListener(this);
        errorBulletcate=findViewById(R.id.errorBulletcate);
        lay_validate=findViewById(R.id.lay_validate);
        otpTextView = findViewById(R.id.otp_view);
    progressDialog=new ProgressDialog(ActivityChooseCategory.this);
    progressDialog.setCanceledOnTouchOutside(false);
    progressDialog.setMessage(getString(R.string.validate_code));

        progressDialog2=new ProgressDialog(ActivityChooseCategory.this);
        progressDialog2.setCanceledOnTouchOutside(false);
        progressDialog2.setMessage(getString(R.string.fetching_index));



        eTindexcode=findViewById(R.id.eTindexcode);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                int selectedId = radioGroup.getCheckedRadioButtonId();
                radiobtnindex =(AppCompatRadioButton) findViewById(selectedId);

                if(selectedId==-1){
                    errorBulletcate.setVisibility(View.VISIBLE);
                } else{
                    if (radiobtnindex.getText().equals(getString(R.string.Index))){
                        Intent intent=new Intent(ActivityChooseCategory.this,PatientDetailsActivity.class);
                        category="Index";
                        intent.putExtra("category",category);

                        //save inex code
//                        new AgentManager().saveindexcode(getBaseContext(),value);
                        startActivity(intent);

                        //finish();

                        lay_validate.setVisibility(View.GONE);
                    }else{
                        lay_validate.setVisibility(View.VISIBLE);
                        btn_start.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn_backfromcategory:
                startActivity(new Intent(ActivityChooseCategory.this,IntroActivity.class));
                break;
            case R.id.btn_startcategory:
                if (eTindexcode.getText().toString().isEmpty()){ Toast.makeText(getBaseContext(), R.string.enter_index_first, Toast.LENGTH_SHORT).show();

                }else{callsendcodeapi();}
                break;

        }
    }
    // create API
    public void callsendcodeapi() {
        // create user body object
        IndexCode index =new IndexCode();
        index.setIndex(eTindexcode.getText().toString().trim());
        progressDialog.show();

        Map<String, String> param = new HashMap<>();
        param.put("index", eTindexcode.getText().toString().trim());
        out(param);
    }


    private void out(final Map<String, String> user) {
        Call<ResponseBody> request = RESTApiClient.getInstance().getApi().validatecode(user);

        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //Response
                if (Integer.toString(response.code()).equals("200")) {
                    progressDialog.dismiss();
                    try {
                        String jsondata = response.body().string();

                        if (jsondata != null) {

                            progressDialog.dismiss();
                            JSONObject reader = new JSONObject(jsondata);

                            String desc = reader.getString("Description");
                             message=reader.getString("message");

                            if (message.equals("Found")) {
                                //for temporary testing ,validation needed
                                Intent intt = new Intent(ActivityChooseCategory.this, PatientDetailsActivity.class);
                                category = "contact";
                                intt.putExtra("uuid", eTindexcode.getText().toString().trim());
                                intt.putExtra("category", "contact");
                              //  new AgentManager().saveindexcode(getBaseContext(),eTindexcode.getText().toString().trim());
                                startActivity(intt);
                            }else{
                                Toast.makeText(getBaseContext(), R.string.invalid_contact_code, Toast.LENGTH_LONG).show();
                            }

                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(ActivityChooseCategory.this, "response error", Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(ActivityChooseCategory.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ActivityChooseCategory.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("log",e.getMessage());
                    }

                }else{
                    progressDialog.dismiss();
                    Toast.makeText(ActivityChooseCategory.this, "error ", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //failure
                progressDialog.dismiss();
                Toast.makeText(ActivityChooseCategory.this, "Error"+t.getLocalizedMessage().toLowerCase(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            startActivity(new Intent(ActivityChooseCategory.this,IntroActivity.class));
            finish();
            Toast.makeText(ActivityChooseCategory.this, "Ongera usubire inyuma kugirango ubashe kuva muri sisitemu", Toast.LENGTH_SHORT).show();
        }

        mBackPressed = System.currentTimeMillis();
    }
}