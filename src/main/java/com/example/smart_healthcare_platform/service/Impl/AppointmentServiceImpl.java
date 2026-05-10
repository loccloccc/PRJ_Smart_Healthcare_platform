package com.example.smart_healthcare_platform.service.Impl;

import com.example.smart_healthcare_platform.model.Appointment;
import com.example.smart_healthcare_platform.model.Doctor;
import com.example.smart_healthcare_platform.model.User;
import com.example.smart_healthcare_platform.repository.IAppointmentRepository;
import com.example.smart_healthcare_platform.service.IAppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements IAppointmentService {

    private final IAppointmentRepository appointmentRepository;

    @Override
    public Appointment save(Appointment appointment) {

        return appointmentRepository.save(appointment);

    }

    @Override
    public List<Appointment> getAppointmentsByDoctor(
            Doctor doctor) {

        return appointmentRepository
                .findAllByDoctor(doctor);

    }

    @Override
    public List<Appointment> getAppointmentsByPatient(
            User patient) {

        return appointmentRepository
                .findByPatient(patient);

    }

    @Override
    public boolean existsByDoctorAndAppointmentTime(
            Doctor doctor,
            LocalDateTime appointmentTime) {

        return appointmentRepository
                .existsByDoctorAndAppointmentTime(
                        doctor,
                        appointmentTime
                );

    }

    @Override
    public Appointment getAppointmentById(Long id) {

        return appointmentRepository
                .findById(id)
                .orElse(null);

    }

    @Override
    public void deleteAppointment(Long id) {

        appointmentRepository.deleteById(id);

    }
}