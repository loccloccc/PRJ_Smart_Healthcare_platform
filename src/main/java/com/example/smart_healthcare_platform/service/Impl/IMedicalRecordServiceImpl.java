package com.example.smart_healthcare_platform.service.Impl;

import com.example.smart_healthcare_platform.model.MedicalRecord;
import com.example.smart_healthcare_platform.model.User;
import com.example.smart_healthcare_platform.repository.IMedicalRecordRepository;
import com.example.smart_healthcare_platform.service.IMedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IMedicalRecordServiceImpl implements IMedicalRecordService {
    private final IMedicalRecordRepository  medicalRecordRepository;
    @Override
    public List<MedicalRecord> getMedicalRecordsByPatient(User patient) {
        return medicalRecordRepository.findByAppointment_Patient(patient);
    }

    @Override
    public List<MedicalRecord> getAllMedicalRecords() {
        return medicalRecordRepository.findAll();
    }

    @Override
    public void save(MedicalRecord medicalRecord) {
        medicalRecordRepository.save(medicalRecord);
    }
}
