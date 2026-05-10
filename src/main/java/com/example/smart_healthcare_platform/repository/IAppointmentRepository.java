package com.example.smart_healthcare_platform.repository;

import com.example.smart_healthcare_platform.model.Appointment;
import com.example.smart_healthcare_platform.model.Doctor;
import com.example.smart_healthcare_platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IAppointmentRepository extends JpaRepository<Appointment, Long> {
    boolean existsByDoctorAndAppointmentTime(Doctor doctor, LocalDateTime appointmentTime);

    List<Appointment> findAllByDoctor(Doctor doctor);

    List<Appointment> findByPatient(User patient);
}

