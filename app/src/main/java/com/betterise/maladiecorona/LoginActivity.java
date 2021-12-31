package com.betterise.maladiecorona;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.betterise.maladiecorona.managers.AgentManager;
import com.betterise.maladiecorona.networking.singleton.RESTApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
private AppCompatButton btn_loginchw;
private AppCompatEditText login_chw_code;
private AppCompatTextView chw_login_error;
private String  kodee;
private ProgressDialog progressDialog;
private long mBackPressed;
private static final int TIME_INTERVAL = 3000;
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
        this.setContentView(R.layout.activity_login);


        progressDialog=new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage(getString(R.string.loading));
        chw_login_error=findViewById(R.id.chw_login_error);
        login_chw_code=findViewById(R.id.login_chw_code);
        btn_loginchw=findViewById(R.id.btn_loginchw);
        btn_loginchw.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_loginchw:
                if (login_chw_code.getText().toString().trim().isEmpty()){
                    chw_login_error.setVisibility(View.VISIBLE);
                    chw_login_error.setText(getString(R.string.enter_chw_code));
                }else{
                    progressDialog.show();
                    kodee=login_chw_code.getText().toString().trim();
                    Map<String, String> param = new HashMap<>();
                            param.put("code",kodee );
                            out(param);
                }
                break;
        }
    }
    private void out(final Map<String, String> user) {
        Call<ResponseBody> request = RESTApiClient.getInstance().getApi().loginchw(user);
        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                   progressDialog.dismiss();

                String jsondata= null;
                try {
                    jsondata = response.body().string();
                    if (jsondata != null){
                                       JSONObject reader=new JSONObject(jsondata);
                                       String status=reader.getString("status");
                                       String Description=reader.getString("Description");

                                       if (status.equals("200")) {
                                       JSONObject jsonObject=reader.getJSONObject("data");
                                       String names=jsonObject.getString("names");
                                       String phone=jsonObject.getString("telephone");
                                      
                         //  Toast.makeText(getBaseContext(), "chw info"+names+"\n"+phone, Toast.LENGTH_SHORT).show();

                           new AgentManager().saveAgentNumber(LoginActivity.this, phone);
                           new AgentManager().saveAgentName(LoginActivity.this, names);
                           startActivity(new Intent(getBaseContext(), IntroActivity.class)) ;
                           overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                           finish();
                       } else if (status.equals("404")){
                                 chw_login_error.setVisibility(View.VISIBLE);
                                 chw_login_error.setText(Description);
                          // Toast.makeText(getBaseContext(), status+"\n"+Description, Toast.LENGTH_SHORT).show();
                       }else{
                                           Toast.makeText(getBaseContext(), getString(R.string.internnet_lost), Toast.LENGTH_LONG).show();
                                       }

                    }

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                          progressDialog.dismiss();
                Toast.makeText(getBaseContext(), getString(R.string.internnet_lost), Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            startActivity(new Intent(getBaseContext(), IntroActivity.class));
            finish();
            Toast.makeText(getBaseContext(), "Ongera usubire inyuma kugirango ubashe kuva muri sisitemu", Toast.LENGTH_SHORT).show();
        }          mBackPressed = System.currentTimeMillis();

    }

}