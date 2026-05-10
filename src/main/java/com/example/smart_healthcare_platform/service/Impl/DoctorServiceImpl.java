package com.example.smart_healthcare_platform.service.Impl;

import com.example.smart_healthcare_platform.model.Doctor;
import com.example.smart_healthcare_platform.model.User;
import com.example.smart_healthcare_platform.repository.IDoctorRepository;
import com.example.smart_healthcare_platform.service.IDoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements IDoctorService {
    private final IDoctorRepository doctorRepository;

    @Override
    public Doctor findDoctorById(User u) {
        return doctorRepository.findByUser(u);
    }
    @Override
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    @Override
    public Doctor getDoctorsById(Long id) {
        return doctorRepository.findById(id).orElse(null);
    }
}
