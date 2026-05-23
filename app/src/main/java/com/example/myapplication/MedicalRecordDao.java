package com.example.myapplication;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MedicalRecordDao {

    @Insert
    void insert(MedicalRecord record);
    @Update
    void update(MedicalRecord record);
    @Query("SELECT * FROM medical_records WHERE PatientName = :patientName AND doctorName = :doctorName ORDER BY date DESC")
    LiveData<List<MedicalRecord>> getMedicalRecordForPatientAndDoctor(String patientName, String doctorName);
    @Query("UPDATE medical_records SET PatientName = :newName WHERE PatientName = :oldName")
    void updatePatientName(String oldName, String newName);
    @Query("UPDATE medical_records SET doctorName = :newName WHERE doctorName = :oldName")
    void updateDoctorName(String oldName, String newName);


}
