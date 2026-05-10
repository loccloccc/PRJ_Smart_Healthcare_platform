package com.example.smart_healthcare_platform.controller;

import com.example.smart_healthcare_platform.dto.AppointmentRequestDTO;
import com.example.smart_healthcare_platform.model.*;
import com.example.smart_healthcare_platform.service.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PatientController {

    private final IDoctorService doctorService;
    private final IUserProfilesService userProfilesService;
    private final IUserService userService;
    private final IAppointmentService appointmentService;
    private final IMedicalRecordService medicalRecordService;

    @GetMapping("/patient/home")
    public String patientHome(HttpSession session,
                              Model model) {

        User user =
                (User) session.getAttribute("userLogin");

        if (user == null ||
                !"PATIENT".equals(user.getRole())) {

            return "redirect:/login";
        }

        model.addAttribute("userLogin", user);

        return "patient_home";
    }


    @GetMapping("/patient/booking")
    public String patientBook(HttpSession session,
                              Model model) {

        User user =
                (User) session.getAttribute("userLogin");

        if (user == null ||
                !"PATIENT".equals(user.getRole())) {

            return "redirect:/login";
        }

        List<Doctor> doctors =
                doctorService.getAllDoctors();

        model.addAttribute("userLogin", user);

        model.addAttribute("doctors", doctors);

        return "patient_appointment_book";
    }

    @GetMapping("/patient/book/add/{id}")
    public String patientAdd(
            @PathVariable Long id,
            HttpSession session,
            Model model) {

        User user =
                (User) session.getAttribute("userLogin");

        if (user == null ||
                !"PATIENT".equals(user.getRole())) {

            return "redirect:/login";
        }

        Doctor doctor =
                doctorService.getDoctorsById(id);

        if (doctor == null) {

            return "redirect:/patient/booking";
        }

        AppointmentRequestDTO dto =
                new AppointmentRequestDTO();

        dto.setDoctorId(doctor.getId());

        dto.setPatientId(user.getId());

        model.addAttribute("doctor", doctor);

        model.addAttribute(
                "appointmentRequestDTO",
                dto
        );

        model.addAttribute(
                "isEdit",
                false
        );

        return "patient_appointment_book_add";
    }

    @GetMapping("/patient/appointment/edit/{id}")
    public String editAppointment(
            @PathVariable Long id,
            HttpSession session,
            Model model
    ){

        User user =
                (User) session.getAttribute("userLogin");

        if (user == null ||
                !"PATIENT".equals(user.getRole())) {

            return "redirect:/login";
        }

        Appointment appointment =
                appointmentService
                        .getAppointmentById(id);

        if (appointment == null) {

            return "redirect:/patient/list";
        }
        if (!appointment.getPatient()
                .getId()
                .equals(user.getId())) {

            return "redirect:/patient/list";
        }

        AppointmentRequestDTO dto =
                new AppointmentRequestDTO();

        dto.setId(
                appointment.getId()
        );

        dto.setDoctorId(
                appointment.getDoctor().getId()
        );

        dto.setPatientId(
                appointment.getPatient().getId()
        );

        dto.setAppointmentTime(
                appointment.getAppointmentTime()
        );

        dto.setNote(
                appointment.getNote()
        );

        model.addAttribute(
                "appointmentRequestDTO",
                dto
        );

        model.addAttribute(
                "doctor",
                appointment.getDoctor()
        );

        model.addAttribute(
                "isEdit",
                true
        );

        return "patient_appointment_book_add";
    }

    @PostMapping("/patient/booking/book_add")
    public String bookAppointment(

            @Valid
            @ModelAttribute("appointmentRequestDTO")
            AppointmentRequestDTO dto,

            BindingResult result,

            Model model,

            RedirectAttributes redirectAttributes,

            HttpSession session
    ) {

        User user =
                (User) session.getAttribute("userLogin");

        if (user == null ||
                !"PATIENT".equals(user.getRole())) {

            return "redirect:/login";
        }

        Doctor doctor =
                doctorService.getDoctorsById(
                        dto.getDoctorId()
                );

        User patient =
                userService.getUserById(
                        dto.getPatientId()
                );

        if (result.hasErrors()) {

            model.addAttribute("doctor", doctor);

            model.addAttribute(
                    "isEdit",
                    dto.getId() != null
            );

            return "patient_appointment_book_add";
        }
        if (dto.getAppointmentTime()
                .isBefore(LocalDateTime.now())) {

            model.addAttribute(
                    "doctor",
                    doctor
            );

            model.addAttribute(
                    "isEdit",
                    dto.getId() != null
            );

            model.addAttribute(
                    "error",
                    "Không thể đặt lịch trong quá khứ"
            );

            return "patient_appointment_book_add";
        }

        Appointment appointment;
        if (dto.getId() != null) {

            appointment =
                    appointmentService
                            .getAppointmentById(
                                    dto.getId()
                            );

            // không tồn tại
            if (appointment == null) {

                return "redirect:/patient/list";
            }

            // chỉ cho sửa lịch của chính mình
            if (!appointment.getPatient()
                    .getId()
                    .equals(user.getId())) {

                return "redirect:/patient/list";
            }

        }
        else {

            appointment =
                    new Appointment();

            appointment.setStatus("PENDING");
        }

        boolean exists =
                appointmentService
                        .existsByDoctorAndAppointmentTime(
                                doctor,
                                dto.getAppointmentTime()
                        );
        if (dto.getId() != null &&
                appointment.getAppointmentTime()
                        .equals(dto.getAppointmentTime())) {

            exists = false;
        }

        if (exists) {

            model.addAttribute(
                    "doctor",
                    doctor
            );

            model.addAttribute(
                    "isEdit",
                    dto.getId() != null
            );

            model.addAttribute(
                    "error",
                    "Bác sĩ đã có lịch"
            );

            return "patient_appointment_book_add";
        }

        appointment.setDoctor(doctor);

        appointment.setPatient(patient);

        appointment.setAppointmentTime(
                dto.getAppointmentTime()
        );

        appointment.setNote(
                dto.getNote()
        );

        appointmentService.save(appointment);

        redirectAttributes.addFlashAttribute(
                "success",
                dto.getId() != null
                        ? "Cập nhật lịch khám thành công"
                        : "Đặt lịch thành công"
        );

        return "redirect:/patient/list";
    }

    // ================= HISTORY (CHỈ COMPLETED) =================
    @GetMapping("/patient/history")
    public String patientHistory(HttpSession session, Model model) {

        User user = (User) session.getAttribute("userLogin");

        if (user == null || !"PATIENT".equals(user.getRole())) {
            return "redirect:/login";
        }

        List<MedicalRecord> records =
                medicalRecordService.getMedicalRecordsByPatient(user);

        model.addAttribute("medicalRecords", records);
        model.addAttribute("userLogin", user);

        return "medical_history";
    }

    @GetMapping("/patient/list")
    public String patientList(HttpSession session,
                              Model model) {

        User user =
                (User) session.getAttribute("userLogin");

        if (user == null ||
                !"PATIENT".equals(user.getRole())) {

            return "redirect:/login";
        }

        List<Appointment> appointments =
                appointmentService
                        .getAppointmentsByPatient(user)
                        .stream()
                        .filter(a ->
                                !"COMPLETED".equals(a.getStatus())
                        )
                        .toList();

        model.addAttribute(
                "appointments",
                appointments
        );

        model.addAttribute(
                "userLogin",
                user
        );

        return "patient_appointment_manage";
    }

    @GetMapping("/patient/appointment/delete/{id}")
    public String deleteAppointment(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ){

        User user =
                (User) session.getAttribute("userLogin");

        if (user == null ||
                !"PATIENT".equals(user.getRole())) {

            return "redirect:/login";
        }

        Appointment appointment =
                appointmentService
                        .getAppointmentById(id);

        if (appointment == null) {

            return "redirect:/patient/list";
        }

        if (!appointment.getPatient()
                .getId()
                .equals(user.getId())) {

            return "redirect:/patient/list";
        }

        appointmentService.deleteAppointment(id);

        redirectAttributes.addFlashAttribute(
                "success",
                "Hủy lịch khám thành công"
        );

        return "redirect:/patient/list";
    }




    @GetMapping("/patient/support")
    public String patientSupport() {
        return "support_consult";
    }
}