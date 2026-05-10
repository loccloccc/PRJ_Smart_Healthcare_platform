package com.example.smart_healthcare_platform.service;

import com.example.smart_healthcare_platform.model.Medicine;
import java.util.List;

public interface IMedicineService {
    List<Medicine> getAllMedicines();
    Medicine getMedicineById(Long id);
    Medicine saveMedicine(Medicine medicine);
    Medicine updateMedicine(Medicine medicine);
    void deleteMedicine(Long id);
}