package com.betterise.maladiecorona;

import static com.betterise.maladiecorona.managers.PollManager.PREFS;
import static com.betterise.maladiecorona.managers.PollManager.PREF_POLLS;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.betterise.maladiecorona.Adapter.ExampleAdapter;
import com.betterise.maladiecorona.model.Item_session;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TestSessionsActivity extends AppCompatActivity implements View.OnClickListener, ExampleAdapter.OnNoteListener {

    private static final int PRIVATE_MODE = 0;
    private RecyclerView mRecyclerView;

    ArrayList<Item_session> mExampleList;
    private ExampleAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ImageView btn_backtointro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_sessions);

        mRecyclerView=findViewById(R.id.recyclerviewsessions);
        btn_backtointro=findViewById(R.id.btn_backtointro);
        btn_backtointro.setOnClickListener(this);


        loadsessions();
        buildRecyclerView();
    }

//function to fetch poll of patient data into a recylerview
    private void loadsessions() {


        @SuppressLint("WrongConstant")
        String json  = getSharedPreferences(PREFS, PRIVATE_MODE).getString(PREF_POLLS, "");

        Gson gson = new Gson();

        Type type = new TypeToken<ArrayList<Item_session>>() {}.getType();
        mExampleList = gson.fromJson(json, type);

        if (mExampleList == null) {
            mExampleList = new ArrayList<>();

        }
    }

    private void buildRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ExampleAdapter(mExampleList,this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_backtointro: finish();
                break;
        }
    }

    @Override
    public void onNoteClick(int position) {

        Intent intent = new Intent(TestSessionsActivity.this, PatientProfileActivity.class);
        intent.putExtra("firstname", mExampleList.get(position).getFirstname());
        intent.putExtra("lastname", mExampleList.get(position).getLastname());
        intent.putExtra("national_ID", mExampleList.get(position).getNational_ID());
        intent.putExtra("patientgender", mExampleList.get(position).getPatientgender());
        intent.putExtra("patienttelephone", mExampleList.get(position).getPatienttelephone());
        intent.putExtra("dob",mExampleList.get(position).getDob());
        intent.putExtra("occupation", mExampleList.get(position).getOccupation());
        intent.putExtra("residence", mExampleList.get(position).getResidence());
        intent.putExtra("nationality", mExampleList.get(position).getNationality());
        intent.putExtra("province", mExampleList.get(position).getProvince());
        intent.putExtra("district", mExampleList.get(position).getDistrict());
        intent.putExtra("sector", mExampleList.get(position).getSector());
        intent.putExtra("cell", mExampleList.get(position).getCell());
        intent.putExtra("village", mExampleList.get(position).getVillage());
        intent.putExtra("rdt_result",mExampleList.get(position).getRdt_result());
        intent.putExtra("ascov_resulti",mExampleList.get(position).getAscov_resulti());
        intent.putExtra("Index",mExampleList.get(position).getIndex().toUpperCase());
        intent.putExtra("category",mExampleList.get(position).getCategory());

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        startActivity(intent);

    }
}