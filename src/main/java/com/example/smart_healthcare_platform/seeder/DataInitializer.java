package com.example.smart_healthcare_platform.seeder;

import com.example.smart_healthcare_platform.model.*;
import com.example.smart_healthcare_platform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final IUserRepository userRepository;
    private final IUserProfileRepository profileRepository;
    private final ISpecialtyRepository specialtyRepository;
    private final IDoctorRepository doctorRepository;
    private final IAppointmentRepository appointmentRepository;
    private final IMedicalRecordRepository medicalRecordRepository;
    private final IMedicineRepository medicineRepository;
    private final IPrescriptionRepository prescriptionRepository;
    private final IPrescriptionDetailRepository detailRepository;

    @Override
    public void run(String... args) {

        if (userRepository.count() > 0) {
            return;
        }

        // ================= USERS =================

        User admin = userRepository.save(
                new User(
                        null,
                        "admin",
                        "123456",
                        "admin@gmail.com",
                        "ADMIN",
                        true,
                        LocalDateTime.now()
                )
        );

        User d1User = userRepository.save(
                new User(
                        null,
                        "doctor1",
                        "123456",
                        "doctor1@gmail.com",
                        "DOCTOR",
                        true,
                        LocalDateTime.now()
                )
        );

        User d2User = userRepository.save(
                new User(
                        null,
                        "doctor2",
                        "123456",
                        "doctor2@gmail.com",
                        "DOCTOR",
                        true,
                        LocalDateTime.now()
                )
        );

        User p1User = userRepository.save(
                new User(
                        null,
                        "patient1",
                        "123456",
                        "patient1@gmail.com",
                        "PATIENT",
                        true,
                        LocalDateTime.now()
                )
        );

        User p2User = userRepository.save(
                new User(
                        null,
                        "patient2",
                        "123456",
                        "patient2@gmail.com",
                        "PATIENT",
                        true,
                        LocalDateTime.now()
                )
        );

        User p3User = userRepository.save(
                new User(
                        null,
                        "patient3",
                        "123456",
                        "patient3@gmail.com",
                        "PATIENT",
                        true,
                        LocalDateTime.now()
                )
        );

        // ================= USER PROFILES =================

        profileRepository.save(
                new UserProfiles(
                        admin,
                        "Admin",
                        "0901",
                        "Ha Noi",
                        LocalDate.of(1990, 1, 1),
                        "male",
                        ""
                )
        );

        profileRepository.save(
                new UserProfiles(
                        d1User,
                        "Doctor A",
                        "0902",
                        "Ha Noi",
                        LocalDate.of(1985, 1, 1),
                        "male",
                        ""
                )
        );

        profileRepository.save(
                new UserProfiles(
                        d2User,
                        "Doctor B",
                        "0903",
                        "Ho Chi Minh",
                        LocalDate.of(1987, 1, 1),
                        "female",
                        ""
                )
        );

        profileRepository.save(
                new UserProfiles(
                        p1User,
                        "Patient 1",
                        "0904",
                        "Da Nang",
                        LocalDate.of(2000, 1, 1),
                        "male",
                        ""
                )
        );

        profileRepository.save(
                new UserProfiles(
                        p2User,
                        "Patient 2",
                        "0905",
                        "Hai Phong",
                        LocalDate.of(2001, 1, 1),
                        "female",
                        ""
                )
        );

        profileRepository.save(
                new UserProfiles(
                        p3User,
                        "Patient 3",
                        "0906",
                        "Hue",
                        LocalDate.of(2002, 1, 1),
                        "male",
                        ""
                )
        );

        // ================= SPECIALTIES =================

        Specialty s1 = specialtyRepository.save(
                new Specialty(
                        null,
                        "Tim mạch",
                        "Chuyên khoa tim mạch",
                        null
                )
        );

        Specialty s2 = specialtyRepository.save(
                new Specialty(
                        null,
                        "Da liễu",
                        "Chuyên khoa da liễu",
                        null
                )
        );

        Specialty s3 = specialtyRepository.save(
                new Specialty(
                        null,
                        "Thần kinh",
                        "Chuyên khoa thần kinh",
                        null
                )
        );

        // ================= DOCTORS =================

        Doctor doctor1 = doctorRepository.save(
                new Doctor(
                        null,
                        d1User,
                        s1,
                        10,
                        "Bác sĩ tim mạch nhiều kinh nghiệm",
                        null
                )
        );

        Doctor doctor2 = doctorRepository.save(
                new Doctor(
                        null,
                        d2User,
                        s2,
                        7,
                        "Chuyên điều trị bệnh da liễu",
                        null
                )
        );

        // ================= APPOINTMENTS =================

        Appointment appointment1 = appointmentRepository.save(
                new Appointment(
                        null,
                        doctor1,
                        p1User,
                        LocalDateTime.now().plusDays(1),
                        "CONFIRMED",
                        "Khám tim",
                        null
                )
        );

        Appointment appointment2 = appointmentRepository.save(
                new Appointment(
                        null,
                        doctor1,
                        p2User,
                        LocalDateTime.now().plusDays(2),
                        "PENDING",
                        "Tái khám",
                        null
                )
        );

        Appointment appointment3 = appointmentRepository.save(
                new Appointment(
                        null,
                        doctor2,
                        p3User,
                        LocalDateTime.now().plusDays(3),
                        "COMPLETED",
                        "Khám da",
                        null
                )
        );

        // ================= MEDICAL RECORDS =================

        MedicalRecord record1 = medicalRecordRepository.save(
                new MedicalRecord(
                        null,
                        appointment1,
                        "Đau tim nhẹ",
                        "Khó thở",
                        "Ổn định",
                        LocalDateTime.now(),
                        null
                )
        );

        MedicalRecord record2 = medicalRecordRepository.save(
                new MedicalRecord(
                        null,
                        appointment2,
                        "Dị ứng da",
                        "Ngứa",
                        "Đang điều trị",
                        LocalDateTime.now(),
                        null
                )
        );

        MedicalRecord record3 = medicalRecordRepository.save(
                new MedicalRecord(
                        null,
                        appointment3,
                        "Viêm da",
                        "Nổi mẩn đỏ",
                        "Đã kê thuốc",
                        LocalDateTime.now(),
                        null
                )
        );

        // ================= MEDICINES =================

        Medicine med1 = medicineRepository.save(
                new Medicine(
                        null,
                        "Paracetamol",
                        100,
                        "Viên",
                        new BigDecimal("5000"),
                        "Giảm đau hạ sốt",
                        null
                )
        );

        Medicine med2 = medicineRepository.save(
                new Medicine(
                        null,
                        "Vitamin C",
                        200,
                        "Viên",
                        new BigDecimal("3000"),
                        "Tăng sức đề kháng",
                        null
                )
        );

        Medicine med3 = medicineRepository.save(
                new Medicine(
                        null,
                        "Thuốc bôi da",
                        50,
                        "Tuýp",
                        new BigDecimal("45000"),
                        "Điều trị viêm da",
                        null
                )
        );

        // ================= PRESCRIPTIONS =================

        Prescription prescription1 = prescriptionRepository.save(
                new Prescription(
                        null,
                        record1,
                        "DISPENSED",
                        LocalDateTime.now(),
                        null
                )
        );

        Prescription prescription2 = prescriptionRepository.save(
                new Prescription(
                        null,
                        record2,
                        "PENDING",
                        LocalDateTime.now(),
                        null
                )
        );

        // ================= PRESCRIPTION DETAILS =================

        detailRepository.save(
                new PrescriptionDetail(
                        null,
                        prescription1,
                        med1,
                        10,
                        "Ngày 2 viên"
                )
        );

        detailRepository.save(
                new PrescriptionDetail(
                        null,
                        prescription1,
                        med2,
                        5,
                        "Ngày 1 viên"
                )
        );

        detailRepository.save(
                new PrescriptionDetail(
                        null,
                        prescription2,
                        med3,
                        1,
                        "Bôi ngày 2 lần"
                )
        );

        System.out.println("SEED DATA SUCCESS");
    }
}