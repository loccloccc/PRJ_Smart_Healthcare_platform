package com.example.smart_healthcare_platform.service;

import com.example.smart_healthcare_platform.model.Doctor;
import com.example.smart_healthcare_platform.model.User;

import java.util.List;

public interface IDoctorService {
    Doctor findDoctorById(User u);
    List<Doctor> getAllDoctors();
    Doctor getDoctorsById(Long id);

}
