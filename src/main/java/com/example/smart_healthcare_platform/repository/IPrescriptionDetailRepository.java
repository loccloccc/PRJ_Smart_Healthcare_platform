package com.example.smart_healthcare_platform.repository;

import com.example.smart_healthcare_platform.model.PrescriptionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPrescriptionDetailRepository extends JpaRepository<PrescriptionDetail, Long> {
}
