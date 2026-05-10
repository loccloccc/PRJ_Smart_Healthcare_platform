package com.example.smart_healthcare_platform.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfiles {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "full_name")
    private String fullName;

    private String phone;

    private String address;

    @Column(name = "date_of_birth")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private String gender;

    private String avatar;

    // constructor dùng để seed data
    public UserProfiles(
            User user,
            String fullName,
            String phone,
            String address,
            LocalDate dateOfBirth,
            String gender,
            String avatar
    ) {

        this.user = user;

        this.userId = user.getId();

        this.fullName = fullName;

        this.phone = phone;

        this.address = address;

        this.dateOfBirth = dateOfBirth;

        this.gender = gender;

        this.avatar = avatar;
    }
}