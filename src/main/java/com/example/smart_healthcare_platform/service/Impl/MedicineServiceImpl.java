package com.example.smart_healthcare_platform.service.Impl;

import com.example.smart_healthcare_platform.model.Medicine;

import com.example.smart_healthcare_platform.repository.IMedicineRepository;
import com.example.smart_healthcare_platform.service.IMedicineService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicineServiceImpl implements IMedicineService {

    @Autowired
    private IMedicineRepository medicineRepository;

    @Override
    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    @Override
    public Medicine getMedicineById(Long id) {
        return medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thuốc với ID: " + id));
    }

    @Override
    public Medicine saveMedicine(Medicine medicine) {
        return medicineRepository.save(medicine);
    }

    @Override
    public Medicine updateMedicine(Medicine medicine) {
        if (medicine.getId() == null) {
            throw new RuntimeException("ID thuốc không được null khi cập nhật");
        }
        return medicineRepository.save(medicine);
    }

    @Override
    public void deleteMedicine(Long id) {
        medicineRepository.deleteById(id);
    }
}