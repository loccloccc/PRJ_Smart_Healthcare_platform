package com.example.smart_healthcare_platform.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "medical_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    private String diagnosis;

    private String symptoms;

    private String result;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToOne(
            mappedBy = "medicalRecord",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Prescription prescription;
}