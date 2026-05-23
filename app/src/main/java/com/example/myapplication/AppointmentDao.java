package com.example.myapplication;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AppointmentDao {

    @Insert
    void insert(Appointment appointment);
    @Update
    void update(Appointment appointment);
    @Delete
    void delete(Appointment appointment);
    @Query("SELECT * FROM appointments WHERE doctorName = :doctorName")
    LiveData<List<Appointment>> getAppointmentsForDoctor(String doctorName);
    @Query("SELECT * FROM appointments WHERE doctorName = :doctorName AND patientName IS NULL")
    LiveData<List<Appointment>> getAvailableAppointmentsForDoctor(String doctorName);
    @Query("SELECT * FROM appointments WHERE patientName = :patientName")
    LiveData<List<Appointment>> getAppointmentsForPatient(String patientName);

    @Query("UPDATE appointments SET patientName = :newName WHERE patientName = :oldName")
    void updatePatientName(String oldName, String newName);
    @Query("UPDATE appointments SET doctorName = :newName WHERE doctorName = :oldName")
    void updateDoctorName(String oldName, String newName);

}

