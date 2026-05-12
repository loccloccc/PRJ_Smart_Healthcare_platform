package com.example.smart_healthcare_platform.controller;

// Import DTO dùng để nhận dữ liệu đặt lịch từ form
import com.example.smart_healthcare_platform.dto.AppointmentRequestDTO;

// Import toàn bộ model
import com.example.smart_healthcare_platform.model.*;

// Import toàn bộ service
import com.example.smart_healthcare_platform.service.*;

// Dùng để lưu session đăng nhập
import jakarta.servlet.http.HttpSession;

// Dùng validation dữ liệu form
import jakarta.validation.Valid;

// Lombok tự tạo constructor cho final field
import lombok.RequiredArgsConstructor;

// Đánh dấu đây là Controller của Spring MVC
import org.springframework.stereotype.Controller;

// Dùng truyền dữ liệu sang View
import org.springframework.ui.Model;

// Chứa lỗi validation
import org.springframework.validation.BindingResult;

// Các annotation mapping request
import org.springframework.web.bind.annotation.*;

// Dùng gửi dữ liệu tạm thời khi redirect
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Thời gian trong Java
import java.time.LocalDateTime;

// Danh sách
import java.util.List;

// Đánh dấu class là Controller
@Controller

// Tự động tạo constructor cho các field final
@RequiredArgsConstructor
public class PatientController {

    // Service xử lý bác sĩ
    private final IDoctorService doctorService;

    // Service hồ sơ người dùng
    private final IUserProfilesService userProfilesService;

    // Service user
    private final IUserService userService;

    // Service lịch khám
    private final IAppointmentService appointmentService;

    // Service bệnh án
    private final IMedicalRecordService medicalRecordService;


    // ===================== TRANG CHỦ BỆNH NHÂN =====================

    // Khi truy cập /patient/home bằng GET
    @GetMapping("/patient/home")
    public String patientHome(HttpSession session,
                              Model model) {

        // Lấy user đang đăng nhập từ session
        User user = (User) session.getAttribute("userLogin");

        // Nếu chưa login hoặc không phải PATIENT
        if (user == null || !"PATIENT".equals(user.getRole())) {

            // Chuyển về trang login
            return "redirect:/login";
        }

        // Gửi user sang view
        model.addAttribute("userLogin", user);

        // Trả về file patient_home.html
        return "patient_home";
    }


    // ===================== DANH SÁCH BÁC SĨ ĐỂ ĐẶT LỊCH =====================

    @GetMapping("/patient/booking")
    public String patientBook(HttpSession session,
                              Model model) {

        // Lấy user login
        User user = (User) session.getAttribute("userLogin");

        // Kiểm tra quyền
        if (user == null || !"PATIENT".equals(user.getRole())) {
            return "redirect:/login";
        }

        // Lấy toàn bộ bác sĩ
        List<Doctor> doctors = doctorService.getAllDoctors();

        // Gửi user sang view
        model.addAttribute("userLogin", user);

        // Gửi danh sách bác sĩ sang view
        model.addAttribute("doctors", doctors);

        // Mở giao diện đặt lịch
        return "patient_appointment_book";
    }


    // ===================== FORM THÊM LỊCH KHÁM =====================

    @GetMapping("/patient/book/add/{id}")
    public String patientAdd(

            // Lấy id bác sĩ từ URL
            @PathVariable Long id,

            HttpSession session,
            Model model) {

        // Lấy user đăng nhập
        User user = (User) session.getAttribute("userLogin");

        // Kiểm tra quyền
        if (user == null || !"PATIENT".equals(user.getRole())) {
            return "redirect:/login";
        }

        // Tìm bác sĩ theo id
        Doctor doctor = doctorService.getDoctorsById(id);

        // Nếu không có bác sĩ
        if (doctor == null) {

            // Quay lại trang booking
            return "redirect:/patient/booking";
        }

        // Tạo DTO rỗng
        AppointmentRequestDTO dto = new AppointmentRequestDTO();

        // Gán id bác sĩ
        dto.setDoctorId(doctor.getId());

        // Gán id bệnh nhân
        dto.setPatientId(user.getId());

        // Gửi doctor sang view
        model.addAttribute("doctor", doctor);

        // Gửi DTO sang view
        model.addAttribute("appointmentRequestDTO", dto);

        // isEdit = false vì đang thêm mới
        model.addAttribute("isEdit", false);

        // Mở form thêm lịch
        return "patient_appointment_book_add";
    }


    // ===================== FORM SỬA LỊCH KHÁM =====================

    @GetMapping("/patient/appointment/edit/{id}")
    public String editAppointment(

            // Lấy id lịch khám
            @PathVariable Long id,

            HttpSession session,
            Model model
    ){

        // Lấy user đăng nhập
        User user = (User) session.getAttribute("userLogin");

        // Kiểm tra quyền
        if (user == null || !"PATIENT".equals(user.getRole())) {
            return "redirect:/login";
        }

        // Tìm lịch khám
        Appointment appointment = appointmentService.getAppointmentById(id);

        // Nếu không tồn tại
        if (appointment == null) {
            return "redirect:/patient/list";
        }

        // Kiểm tra lịch khám có phải của user này không
        if (!appointment.getPatient().getId().equals(user.getId())) {
            return "redirect:/patient/list";
        }

        // Tạo DTO
        AppointmentRequestDTO dto = new AppointmentRequestDTO();

        // Đổ dữ liệu cũ vào DTO
        dto.setId(appointment.getId());
        dto.setDoctorId(appointment.getDoctor().getId());
        dto.setPatientId(appointment.getPatient().getId());
        dto.setAppointmentTime(appointment.getAppointmentTime());
        dto.setNote(appointment.getNote());

        // Gửi DTO sang view
        model.addAttribute("appointmentRequestDTO", dto);

        // Gửi doctor sang view
        model.addAttribute("doctor", appointment.getDoctor());

        // isEdit = true vì đang sửa
        model.addAttribute("isEdit", true);

        // Mở form
        return "patient_appointment_book_add";
    }


    // ===================== XỬ LÝ THÊM / SỬA LỊCH KHÁM =====================

    @PostMapping("/patient/booking/book_add")
    public String bookAppointment(

            // Nhận dữ liệu form + validation
            @Valid
            @ModelAttribute("appointmentRequestDTO")
            AppointmentRequestDTO dto,

            // Chứa lỗi validation
            BindingResult result,

            Model model,

            // Flash message
            RedirectAttributes redirectAttributes,

            HttpSession session
    ) {

        // Lấy user login
        User user = (User) session.getAttribute("userLogin");

        // Kiểm tra quyền
        if (user == null || !"PATIENT".equals(user.getRole())) {
            return "redirect:/login";
        }

        // Lấy doctor theo id
        Doctor doctor = doctorService.getDoctorsById(dto.getDoctorId());

        // Lấy patient theo id
        User patient = userService.getUserById(dto.getPatientId());

        // Nếu validation lỗi
        if (result.hasErrors()) {

            // Gửi lại doctor
            model.addAttribute("doctor", doctor);

            // Kiểm tra đang sửa hay thêm
            model.addAttribute("isEdit", dto.getId() != null);

            // Quay lại form
            return "patient_appointment_book_add";
        }

        // Nếu chọn thời gian trong quá khứ
        if (dto.getAppointmentTime().isBefore(LocalDateTime.now())) {

            // Gửi doctor lại
            model.addAttribute("doctor", doctor);

            // Kiểm tra edit
            model.addAttribute("isEdit", dto.getId() != null);

            // Gửi lỗi
            model.addAttribute("error", "Không thể đặt lịch trong quá khứ");

            // Quay lại form
            return "patient_appointment_book_add";
        }

        // Tạo biến appointment
        Appointment appointment;

        // Nếu là sửa
        if (dto.getId() != null) {

            // Lấy appointment cũ
            appointment = appointmentService.getAppointmentById(dto.getId());

            // Nếu không tồn tại
            if (appointment == null) {
                return "redirect:/patient/list";
            }

            // Kiểm tra quyền
            if (!appointment.getPatient().getId().equals(user.getId())) {
                return "redirect:/patient/list";
            }
        }

        // Nếu là thêm mới
        else {

            // Tạo appointment mới
            appointment = new Appointment();

            // Trạng thái mặc định
            appointment.setStatus("PENDING");
        }

        // Kiểm tra bác sĩ đã có lịch chưa
        boolean exists =
                appointmentService.existsByDoctorAndAppointmentTime(
                        doctor,
                        dto.getAppointmentTime()
                );

        // Nếu sửa mà thời gian không đổi
        if (dto.getId() != null &&
                appointment.getAppointmentTime().equals(dto.getAppointmentTime())) {

            // Không tính là trùng
            exists = false;
        }

        // Nếu lịch đã tồn tại
        if (exists) {

            // Gửi doctor
            model.addAttribute("doctor", doctor);

            // Gửi trạng thái edit
            model.addAttribute("isEdit", dto.getId() != null);

            // Báo lỗi
            model.addAttribute("error", "Bác sĩ đã có lịch");

            // Quay lại form
            return "patient_appointment_book_add";
        }

        // Gán doctor
        appointment.setDoctor(doctor);

        // Gán patient
        appointment.setPatient(patient);

        // Gán thời gian khám
        appointment.setAppointmentTime(dto.getAppointmentTime());

        // Gán ghi chú
        appointment.setNote(dto.getNote());

        // Lưu vào database
        appointmentService.save(appointment);

        // Gửi thông báo thành công
        redirectAttributes.addFlashAttribute(
                "success",
                dto.getId() != null
                        ? "Cập nhật lịch khám thành công"
                        : "Đặt lịch thành công"
        );

        // Quay lại danh sách lịch khám
        return "redirect:/patient/list";
    }


    // ===================== LỊCH SỬ KHÁM =====================

    @GetMapping("/patient/history")
    public String patientHistory(HttpSession session, Model model) {

        // Lấy user login
        User user = (User) session.getAttribute("userLogin");

        // Kiểm tra quyền
        if (user == null || !"PATIENT".equals(user.getRole())) {
            return "redirect:/login";
        }

        // Lấy bệnh án của bệnh nhân
        List<MedicalRecord> records =
                medicalRecordService.getMedicalRecordsByPatient(user);

        // Gửi dữ liệu sang view
        model.addAttribute("medicalRecords", records);

        // Gửi user
        model.addAttribute("userLogin", user);

        // Mở trang history
        return "medical_history";
    }


    // ===================== DANH SÁCH LỊCH KHÁM =====================

    @GetMapping("/patient/list")
    public String patientList(HttpSession session,
                              Model model) {

        // Lấy user login
        User user = (User) session.getAttribute("userLogin");

        // Kiểm tra quyền
        if (user == null || !"PATIENT".equals(user.getRole())) {
            return "redirect:/login";
        }

        // Lấy danh sách appointment của bệnh nhân
        List<Appointment> appointments =
                appointmentService.getAppointmentsByPatient(user)

                        // Loại bỏ lịch COMPLETED
                        .stream()
                        .filter(a -> !"COMPLETED".equals(a.getStatus()))

                        // Chuyển stream về list
                        .toList();

        // Gửi appointments sang view
        model.addAttribute("appointments", appointments);

        // Gửi user
        model.addAttribute("userLogin", user);

        // Mở trang quản lý lịch khám
        return "patient_appointment_manage";
    }


    // ===================== HỦY LỊCH KHÁM =====================

    @GetMapping("/patient/appointment/delete/{id}")
    public String deleteAppointment(

            // id appointment
            @PathVariable Long id,

            HttpSession session,

            RedirectAttributes redirectAttributes
    ){

        // Lấy user login
        User user = (User) session.getAttribute("userLogin");

        // Kiểm tra quyền
        if (user == null || !"PATIENT".equals(user.getRole())) {
            return "redirect:/login";
        }

        // Tìm appointment
        Appointment appointment =
                appointmentService.getAppointmentById(id);

        // Nếu không tồn tại
        if (appointment == null) {
            return "redirect:/patient/list";
        }

        // Kiểm tra quyền
        if (!appointment.getPatient().getId().equals(user.getId())) {
            return "redirect:/patient/list";
        }

        // Xóa appointment
        appointmentService.deleteAppointment(id);

        // Gửi thông báo thành công
        redirectAttributes.addFlashAttribute(
                "success",
                "Hủy lịch khám thành công"
        );

        // Quay lại danh sách
        return "redirect:/patient/list";
    }



    // ===================== HỖ TRỢ =====================

    @GetMapping("/patient/support")
    public String patientSupport() {

        // Mở trang hỗ trợ
        return "support_consult";
    }
}