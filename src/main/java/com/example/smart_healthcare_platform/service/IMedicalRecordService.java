package com.example.smart_healthcare_platform.service;

import com.example.smart_healthcare_platform.model.MedicalRecord;
import com.example.smart_healthcare_platform.model.User;

import java.util.List;

public interface IMedicalRecordService {
    List<MedicalRecord> getMedicalRecordsByPatient(User patient);
    List<MedicalRecord> getAllMedicalRecords();

    void save(MedicalRecord medicalRecord);
}
