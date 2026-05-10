package com.example.smart_healthcare_platform.repository;

import com.example.smart_healthcare_platform.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMedicineRepository extends JpaRepository<Medicine, Long> {
}
