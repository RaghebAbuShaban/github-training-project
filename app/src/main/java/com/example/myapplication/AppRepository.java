package com.example.myapplication;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class AppRepository {

    private UserDao userDao;
    private AppointmentDao appointmentDao;
    private MedicalRecordDao medicalRecordDao;

    public AppRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
        appointmentDao = db.appointmentDao();
        medicalRecordDao = db.medicalRecordDao();
    }

    public LiveData<User> loginUser(String username, String password) {
        return userDao.login(username, password);
    }

    public void insertUser(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.insert(user));
    }

    public void updateUser(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.update(user));
    }

    public LiveData<User> getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    public LiveData<List<User>> getUsersByType(String type) {
        return userDao.getUsersByType(type);
    }


    public void insertAppointment(Appointment appointment) {
        AppDatabase.databaseWriteExecutor.execute(() -> appointmentDao.insert(appointment));
    }

    public void updateAppointment(Appointment appointment) {
        AppDatabase.databaseWriteExecutor.execute(() -> appointmentDao.update(appointment));
    }

    public void deleteAppointment(Appointment appointment) {
        AppDatabase.databaseWriteExecutor.execute(() -> appointmentDao.delete(appointment));
    }

    public LiveData<List<Appointment>> getAppointmentsForDoctor(String doctorName){
        return appointmentDao.getAppointmentsForDoctor(doctorName);
    }

    public LiveData<List<Appointment>> getAvailableAppointmentsForDoctor(String doctorName) {
        return appointmentDao.getAvailableAppointmentsForDoctor(doctorName);
    }

    public LiveData<List<Appointment>> getAppointmentsForPatient(String patientName) {
        return appointmentDao.getAppointmentsForPatient(patientName);
    }

    public void updatePatientNameInAppointments(String oldName, String newName) {
        AppDatabase.databaseWriteExecutor.execute(() -> appointmentDao.updatePatientName(oldName, newName));
    }

    public void updateDoctorNameInAppointments(String oldName, String newName) {
        AppDatabase.databaseWriteExecutor.execute(() -> appointmentDao.updateDoctorName(oldName, newName));
    }


    public void insertMedicalRecord(MedicalRecord record) {
        AppDatabase.databaseWriteExecutor.execute(() -> medicalRecordDao.insert(record));
    }
    public LiveData<List<MedicalRecord>> getMedicalRecordForPatientAndDoctor(String patientName, String doctorName) {
        return medicalRecordDao.getMedicalRecordForPatientAndDoctor(patientName, doctorName);
    }
    public void updatePatientNameInMedicalRecords(String oldName, String newName) {
        AppDatabase.databaseWriteExecutor.execute(() -> medicalRecordDao.updatePatientName(oldName, newName));
    }

    public void updateDoctorNameInMedicalRecords(String oldName, String newName) {
        AppDatabase.databaseWriteExecutor.execute(() -> medicalRecordDao.updateDoctorName(oldName, newName));
    }

    public void updatePatientNameEverywhere(String oldName, String newName) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            updatePatientNameInAppointments(oldName, newName);
            updatePatientNameInMedicalRecords(oldName, newName);
        });
    }

    public void updateDoctorNameEverywhere(String oldName, String newName) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            updateDoctorNameInAppointments(oldName, newName);
            updateDoctorNameInMedicalRecords(oldName, newName);
        });
    }
}
