package com.betterise.maladiecorona;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.core.widget.TextViewCompat;

import android.app.ProgressDialog;
import android.content.Intent;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category);
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
                    if (radiobtnindex.getText().equals("Index")){



                        progressDialog2.show();

                        AsyncHttpClient client = new AsyncHttpClient();
                        client.setBasicAuth("EAC_test", "EACPass@2021");
                        client.get("http://161.97.184.144:8080/api/33/trackedEntityAttributes/MSWzPQhISym/generateAndReserve?numberToReserve=1", new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                String json = new String(responseBody); // This is the json.
                                progressDialog2.dismiss();
                            //   Toast.makeText(getBaseContext(), "data is \n"+json, Toast.LENGTH_LONG).show();



                                try {
                                    JSONArray jsonArray = new JSONArray(json);
                                    for (int i=0; i<jsonArray.length(); i++) {
                                        JSONObject item = jsonArray.getJSONObject(i);
                                         value = item.getString("value");
                                    }


                        Intent intent=new Intent(ActivityChooseCategory.this,PatientDetailsActivity.class);
                        category="Index";
                        intent.putExtra("uuid", value);
                        intent.putExtra("category",category);

                        new AgentManager().saveindexcode(getBaseContext(),value);
                        startActivity(intent);
                        finish();


                                   // Log.e("Value of index  is : ",value);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                progressDialog2.dismiss();
                                Toast.makeText(getBaseContext(), getString(R.string.internnet_lost)+error.getMessage() , Toast.LENGTH_SHORT).show();
                            }
                        });



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
                                intt.putExtra("indexcodee", eTindexcode.getText().toString().trim());
                                intt.putExtra("category", category);
                                new AgentManager().saveindexcode(getBaseContext(),eTindexcode.getText().toString().trim());
                                startActivity(intt);
                            }else{
                                Toast.makeText(getBaseContext(), R.string.invalid_contact_code, Toast.LENGTH_SHORT).show();
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


}