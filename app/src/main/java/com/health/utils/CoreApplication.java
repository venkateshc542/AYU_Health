package com.health.utils;

import android.app.Application;

import com.health.pojo.PatientData;

import java.util.ArrayList;

public class CoreApplication extends Application {
    String imageData;
    ArrayList<PatientData> patientList;

    public ArrayList<PatientData> getPatientList() {
        return patientList;
    }

    public void setPatientList(ArrayList<PatientData> patientList) {
        this.patientList = patientList;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }


}
