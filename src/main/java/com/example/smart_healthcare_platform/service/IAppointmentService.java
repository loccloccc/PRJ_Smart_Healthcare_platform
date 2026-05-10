package com.example.smart_healthcare_platform.service;

import com.example.smart_healthcare_platform.model.Appointment;
import com.example.smart_healthcare_platform.model.Doctor;
import com.example.smart_healthcare_platform.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface IAppointmentService {

    Appointment save(Appointment appointment);

    List<Appointment> getAppointmentsByDoctor(Doctor doctor);

    boolean existsByDoctorAndAppointmentTime(
            Doctor doctor,
            LocalDateTime appointmentTime
    );

    List<Appointment> getAppointmentsByPatient(User patient);

    Appointment getAppointmentById(Long id);

    void deleteAppointment(Long id);
}