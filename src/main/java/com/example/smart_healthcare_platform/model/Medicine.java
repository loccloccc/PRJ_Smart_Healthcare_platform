package com.example.smart_healthcare_platform.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "medicines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer stock;

    private String unit;

    private BigDecimal price;

    private String description;

    @OneToMany(mappedBy = "medicine")
    private List<PrescriptionDetail> prescriptionDetails;
}