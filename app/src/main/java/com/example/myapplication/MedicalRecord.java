package com.example.myapplication;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "medical_records")
public class MedicalRecord {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String PatientName;
    private String doctorName;
    private String diagnosis;
    private String treatment;
    private String notes;
    private String date;

    public MedicalRecord() { }


    public MedicalRecord(String patientName, String doctorName, String diagnosis, String treatment, String notes, String date) {
        PatientName = patientName;
        this.doctorName = doctorName;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.notes = notes;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPatientName() {
        return PatientName;
    }

    public void setPatientName(String patientName) {
        PatientName = patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
