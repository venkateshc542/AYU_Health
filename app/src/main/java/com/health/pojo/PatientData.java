package com.health.pojo;

public class PatientData {
    String patientID;
    String name;
    String age;
    String gender;
    String location;
    String apmtDate;
    String description;
    String status;
    String email;
    String consultType;
    String mobilenumber;


    public PatientData(String patientID, String name, String age, String gender, String location, String apmtDate, String description, String status,
                       String email, String mobilenumber, String consultType) {
        this.patientID = patientID;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.location = location;
        this.apmtDate = apmtDate;
        this.description = description;
        this.status = status;
        this.email = email;
        this.mobilenumber = mobilenumber;
        this.consultType = consultType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobilenumber() {
        return mobilenumber;
    }

    public void setMobilenumber(String mobilenumber) {
        this.mobilenumber = mobilenumber;
    }


    public String getConsultType() {
        return consultType;
    }

    public void setConsultType(String consultType) {
        this.consultType = consultType;
    }

    public String getApmtDate() {
        return apmtDate;
    }

    public void setApmtDate(String apmtDate) {
        this.apmtDate = apmtDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
