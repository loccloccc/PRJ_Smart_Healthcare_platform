package com.example.smart_healthcare_platform.repository;

import com.example.smart_healthcare_platform.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPrescriptionRepository extends JpaRepository<Prescription, Long> {
}
