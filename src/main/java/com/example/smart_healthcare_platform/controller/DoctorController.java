package com.example.smart_healthcare_platform.controller;

import com.example.smart_healthcare_platform.model.*;
import com.example.smart_healthcare_platform.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DoctorController {

    private final IDoctorService doctorService;
    private final IAppointmentService appointmentService;
    private final IMedicineService medicineService;
    private final IMedicalRecordService medicalRecordService;

    // ================= HOME =================
    @GetMapping("/doctor/home")
    public String doctorHome(HttpSession session, Model model) {

        User user = (User) session.getAttribute("userLogin");

        if (user == null || !"DOCTOR".equals(user.getRole())) {
            return "redirect:/login";
        }

        Doctor doctor = doctorService.findDoctorById(user);

        List<Appointment> allAppointments =
                appointmentService.getAppointmentsByDoctor(doctor);

        List<Appointment> todayAppointments =
                allAppointments.stream()
                        .filter(a ->
                                a.getAppointmentTime() != null &&
                                        a.getAppointmentTime()
                                                .toLocalDate()
                                                .equals(LocalDate.now())
                        )
                        .toList();

        long pendingAppointments =
                todayAppointments.stream()
                        .filter(a ->
                                "PENDING".equalsIgnoreCase(a.getStatus())
                        )
                        .count();

        long completedTasks =
                todayAppointments.stream()
                        .filter(a ->
                                "COMPLETED".equalsIgnoreCase(a.getStatus())
                        )
                        .count();

        model.addAttribute(
                "appointments",
                todayAppointments.stream()
                        .filter(a ->
                                "PENDING".equalsIgnoreCase(a.getStatus())
                        )
                        .toList()
        );

        model.addAttribute(
                "pendingAppointments",
                pendingAppointments
        );

        model.addAttribute(
                "urgentTasks",
                pendingAppointments
        );

        model.addAttribute(
                "completedTasks",
                completedTasks
        );

        model.addAttribute(
                "today",
                LocalDate.now()
        );

        model.addAttribute(
                "userLogin",
                user
        );

        return "doctor_home";
    }
    @GetMapping("/doctor/profile")
    public String doctorProfile(
            HttpSession session,
            Model model
    ) {

        User user =
                (User) session.getAttribute("userLogin");

        if (user == null ||
                !"DOCTOR".equals(user.getRole())) {

            return "redirect:/login";
        }

        Doctor doctor =
                doctorService.findDoctorById(user);

        model.addAttribute("userLogin", user);

        model.addAttribute("doctor", doctor);

        return "profile";
    }
    // ================= PATIENT LIST =================
    @GetMapping("/doctor/patients")
    public String doctorPatients(
            HttpSession session,
            Model model
    ) {

        User user =
                (User) session.getAttribute("userLogin");

        if (user == null ||
                !"DOCTOR".equals(user.getRole())) {

            return "redirect:/login";
        }

        Doctor doctor =
                doctorService.findDoctorById(user);

        List<Appointment> appointments =
                appointmentService
                        .getAppointmentsByDoctor(doctor)
                        .stream()

                        // chỉ lấy pending
                        .filter(a ->
                                "PENDING"
                                        .equalsIgnoreCase(
                                                a.getStatus()
                                        )
                        )

                        // chỉ lấy từ hôm nay trở đi
                        .filter(a ->
                                a.getAppointmentTime() != null
                                        &&
                                        !a.getAppointmentTime()
                                                .toLocalDate()
                                                .isBefore(LocalDate.now())
                        )

                        .sorted((a1, a2) ->
                                a1.getAppointmentTime()
                                        .compareTo(
                                                a2.getAppointmentTime()
                                        )
                        )

                        .toList();

        model.addAttribute(
                "appointments",
                appointments
        );

        model.addAttribute(
                "today",
                LocalDate.now()
        );

        model.addAttribute(
                "userLogin",
                user
        );

        return "doctor_patients";
    }
    // ================= DIAGNOSIS =================
    @GetMapping("/doctor/diagnosis/{id}")
    public String doctorDiagnosis(
            @PathVariable Long id,
            HttpSession session,
            Model model
    ) {

        User user = (User) session.getAttribute("userLogin");

        if (user == null || !"DOCTOR".equals(user.getRole())) {
            return "redirect:/login";
        }

        Appointment appointment =
                appointmentService.getAppointmentById(id);

        if (appointment == null) {
            return "redirect:/doctor/patients";
        }

        // check doctor ownership
        if (!appointment.getDoctor()
                .getUser()
                .getId()
                .equals(user.getId())) {

            return "redirect:/doctor/patients";
        }

        model.addAttribute("appointment", appointment);

        model.addAttribute(
                "medicines",
                medicineService.getAllMedicines()
        );

        model.addAttribute("userLogin", user);

        return "doctor_diagnosis_receive";
    }

    // ================= SAVE DIAGNOSIS =================
    @PostMapping("/doctor/diagnosis/save")
    public String saveDiagnosis(
            @RequestParam Long appointmentId,
            @RequestParam String symptoms,
            @RequestParam String diagnosis,
            @RequestParam(required = false) String result,
            HttpSession session
    ) {

        User user = (User) session.getAttribute("userLogin");

        if (user == null || !"DOCTOR".equals(user.getRole())) {
            return "redirect:/login";
        }

        Appointment appointment =
                appointmentService.getAppointmentById(appointmentId);

        if (appointment == null) {
            return "redirect:/doctor/patients";
        }

        appointment.setStatus("COMPLETED");

        appointment.setNote(
                "Triệu chứng: " + symptoms +
                        "\nChẩn đoán: " + diagnosis +
                        "\nKết quả: " + result
        );

        appointmentService.save(appointment);

        MedicalRecord record = new MedicalRecord();

        record.setAppointment(appointment);

        record.setSymptoms(symptoms);

        record.setDiagnosis(diagnosis);

        record.setResult(result);

        record.setCreatedAt(LocalDateTime.now());

        medicalRecordService.save(record);

        return "redirect:/doctor/patients";
    }
}