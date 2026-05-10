package com.example.smart_healthcare_platform.repository;

import com.example.smart_healthcare_platform.model.MedicalRecord;
import com.example.smart_healthcare_platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByAppointmentPatient(User patient);
}

