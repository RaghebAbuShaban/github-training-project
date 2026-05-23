package com.example.myapplication;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class AppViewModel extends AndroidViewModel {

    private AppRepository repository;
    private MutableLiveData<User> currentUser = new MutableLiveData<>();

    public AppViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
    }
    public LiveData<User> loginUser(String username, String password) {
        return repository.loginUser(username, password);
    }
    public void registerUser(User user){
        repository.insertUser(user);
    }

    public void updateUser(User user){
        repository.updateUser(user);
        currentUser.postValue(user);
    }
    public void setCurrentUser(User user){
        currentUser.setValue(user);
    }

    public LiveData<User> getCurrentUser(){
        return currentUser;
    }
    public LiveData<User> getUserByUsername(String username){
        LiveData<User> userLiveData = repository.getUserByUsername(username);
        userLiveData.observeForever(user -> {
            if(user != null){
                currentUser.postValue(user);
            }
        });
        return userLiveData;
    }
    public LiveData<List<User>> getUsersByType(String type){
        return repository.getUsersByType(type);
    }


    public void insertAppointment(Appointment appointment){
        repository.insertAppointment(appointment);
    }

    public void updateAppointment(Appointment appointment){
        repository.updateAppointment(appointment);
    }

    public void deleteAppointment(Appointment appointment){
        repository.deleteAppointment(appointment);
    }

    public LiveData<List<Appointment>> getAppointmentsForDoctor(String doctorName){
        return repository.getAppointmentsForDoctor(doctorName);
    }

    public LiveData<List<Appointment>> getAvailableAppointmentsForDoctor(String doctorName){
        return repository.getAvailableAppointmentsForDoctor(doctorName);
    }

    public LiveData<List<Appointment>> getAppointmentsForPatient(String patientName){
        return repository.getAppointmentsForPatient(patientName);
    }



    public void insertMedicalRecord(MedicalRecord record){
        repository.insertMedicalRecord(record);
    }
    public LiveData<List<MedicalRecord>> getMedicalRecordForPatientAndDoctor(String patientName, String doctorName){
        return repository.getMedicalRecordForPatientAndDoctor(patientName, doctorName);
    }
    public void updatePatientNameEverywhere(String oldName, String newName){
        repository.updatePatientNameEverywhere(oldName, newName);
    }
    public void updateDoctorNameEverywhere(String oldName, String newName){
        repository.updateDoctorNameEverywhere(oldName, newName);
    }
}
