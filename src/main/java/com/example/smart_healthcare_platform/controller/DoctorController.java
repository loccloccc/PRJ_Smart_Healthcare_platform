package com.example.smart_healthcare_platform.controller;

import com.example.smart_healthcare_platform.model.*;
import com.example.smart_healthcare_platform.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DoctorController {

    private final IDoctorService doctorService;
    private final IAppointmentService appointmentService;
    private final IMedicineService medicineService;
    private final IMedicalRecordService medicalRecordService;
    private final IUserProfilesService  userProfilesService;

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

    // ================= SAVE + CORE-08 =================
    @Transactional
    @PostMapping("/doctor/diagnosis/save")
    public String saveDiagnosis(

            @RequestParam Long appointmentId,

            @RequestParam String symptoms,

            @RequestParam String diagnosis,

            @RequestParam(required = false)
            String result,

            @RequestParam List<Long> medicineIds,

            @RequestParam List<Integer> quantities,

            @RequestParam List<String> dosages,

            HttpSession session,

            Model model
    ) {

        User user =
                (User) session.getAttribute("userLogin");

        if (user == null ||
                !"DOCTOR".equals(user.getRole())) {

            return "redirect:/login";
        }

        Appointment appointment =
                appointmentService.getAppointmentById(
                        appointmentId
                );

        if (appointment == null) {

            return "redirect:/doctor/patients";
        }

        model.addAttribute(
                "appointment",
                appointment
        );

        model.addAttribute(
                "medicines",
                medicineService.getAllMedicines()
        );

        // giữ lại dữ liệu cũ
        model.addAttribute("symptoms", symptoms);
        model.addAttribute("diagnosis", diagnosis);
        model.addAttribute("resultValue", result);

        // ================= VALIDATE =================

        boolean hasError = false;

        // triệu chứng
        if (symptoms == null ||
                symptoms.trim().isEmpty()) {

            model.addAttribute(
                    "symptomsError",
                    "Vui lòng nhập triệu chứng"
            );

            hasError = true;
        }

        // chẩn đoán
        if (diagnosis == null ||
                diagnosis.trim().isEmpty()) {

            model.addAttribute(
                    "diagnosisError",
                    "Vui lòng nhập chẩn đoán"
            );

            hasError = true;
        }

        // phải có ít nhất 1 thuốc
        boolean hasMedicine = false;

        for (Integer qty : quantities) {

            if (qty != null && qty > 0) {

                hasMedicine = true;

                break;
            }
        }

        if (!hasMedicine) {

            model.addAttribute(
                    "medicineError",
                    "Phải kê ít nhất 1 loại thuốc"
            );

            hasError = true;
        }

        if (hasError) {

            return "doctor_diagnosis_receive";
        }

        // ================= CHECK STOCK =================

        for (int i = 0; i < medicineIds.size(); i++) {

            Integer qty =
                    quantities.get(i);

            if (qty == null ||
                    qty <= 0) {

                continue;
            }

            Medicine medicine =
                    medicineService.getMedicineById(
                            medicineIds.get(i)
                    );

            if (medicine.getStock() < qty) {

                model.addAttribute(
                        "error",
                        "Không đủ thuốc: "
                                + medicine.getName()
                );

                return "doctor_diagnosis_receive";
            }
        }

        // ================= MEDICAL RECORD =================

        MedicalRecord record =
                new MedicalRecord();

        record.setAppointment(appointment);

        record.setSymptoms(symptoms);

        record.setDiagnosis(diagnosis);

        record.setResult(result);

        record.setCreatedAt(
                LocalDateTime.now()
        );

        medicalRecordService.save(record);

        // ================= PRESCRIPTION =================

        Prescription prescription =
                new Prescription();

        prescription.setMedicalRecord(record);

        prescription.setStatus("DISPENSED");

        prescription.setCreatedAt(
                LocalDateTime.now()
        );

        List<PrescriptionDetail> details =
                new ArrayList<>();

        // ================= SAVE MEDICINES =================

        for (int i = 0; i < medicineIds.size(); i++) {

            Integer qty =
                    quantities.get(i);

            if (qty != null &&
                    qty > 0) {

                Medicine medicine =
                        medicineService.getMedicineById(
                                medicineIds.get(i)
                        );

                // trừ kho
                medicine.setStock(
                        medicine.getStock() - qty
                );

                medicineService.saveMedicine(medicine);

                PrescriptionDetail detail =
                        new PrescriptionDetail();

                detail.setPrescription(prescription);

                detail.setMedicine(medicine);

                detail.setQuantity(qty);

                detail.setDosage(
                        dosages.get(i)
                );

                details.add(detail);
            }
        }

        prescription.setDetails(details);

        record.setPrescription(prescription);

        // ================= COMPLETE =================

        appointment.setStatus("COMPLETED");

        appointment.setNote(
                "Đã khám và cấp thuốc"
        );

        appointmentService.save(appointment);

        return "redirect:/doctor/patients";
    }
}