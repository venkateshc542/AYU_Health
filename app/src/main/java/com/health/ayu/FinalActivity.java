package com.health.ayu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.health.pojo.PatientData;
import com.health.utils.CoreApplication;
import com.health.utils.CustomSharedPreferences;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by venkatesh on 08-Apr-20.
 */

public class FinalActivity extends AppCompatActivity {

    CoreApplication application = null;
    Button close_btn;
    ArrayList<PatientData> arrayList;
    String name;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_success);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        application = (CoreApplication) getApplication();
        arrayList = application.getPatientList();
        name = CustomSharedPreferences.getStringData(FinalActivity.this, CustomSharedPreferences.SP_KEY.PATIENT_NAME);

        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).getName().equals(name)||arrayList.get(i).getStatus().equalsIgnoreCase("Completed")) {
                arrayList.get(i).setStatus("Completed");
            }else{
                arrayList.get(i).setStatus("Pending");
            }
        }
        application.setPatientList(arrayList);

        close_btn = findViewById(R.id.close_btn);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomSharedPreferences.clear(FinalActivity.this, CustomSharedPreferences.SP_KEY.PATIENT_NAME);
                Intent intent = new Intent(FinalActivity.this, PatientListActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}