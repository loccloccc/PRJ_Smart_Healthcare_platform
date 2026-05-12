package com.example.smart_healthcare_platform.controller;

// Import toàn bộ model
import com.example.smart_healthcare_platform.model.*;

// Import toàn bộ service
import com.example.smart_healthcare_platform.service.*;

// Dùng quản lý session đăng nhập
import jakarta.servlet.http.HttpSession;

// Lombok tự tạo constructor cho các field final
import lombok.RequiredArgsConstructor;

// Đánh dấu đây là Controller
import org.springframework.stereotype.Controller;

// Quản lý transaction database
import org.springframework.transaction.annotation.Transactional;

// Dùng truyền dữ liệu sang View
import org.springframework.ui.Model;

// Annotation mapping request
import org.springframework.web.bind.annotation.*;

// Dùng xử lý ngày
import java.time.LocalDate;

// Dùng xử lý ngày giờ
import java.time.LocalDateTime;

// Danh sách động
import java.util.ArrayList;

// Interface List
import java.util.List;


// Đánh dấu class là Spring MVC Controller
@Controller

// Lombok tự tạo constructor cho các biến final
@RequiredArgsConstructor
public class DoctorController {

    // Service xử lý bác sĩ
    private final IDoctorService doctorService;

    // Service xử lý lịch khám
    private final IAppointmentService appointmentService;

    // Service xử lý thuốc
    private final IMedicineService medicineService;

    // Service xử lý hồ sơ bệnh án
    private final IMedicalRecordService medicalRecordService;

    // Service hồ sơ người dùng
    private final IUserProfilesService  userProfilesService;


    // ===================== TRANG CHỦ BÁC SĨ =====================

    @GetMapping("/doctor/home")
    public String doctorHome(HttpSession session, Model model) {

        // Lấy user đang đăng nhập
        User user = (User) session.getAttribute("userLogin");

        // Nếu chưa đăng nhập hoặc không phải bác sĩ
        if (user == null || !"DOCTOR".equals(user.getRole())) {

            // Chuyển về login
            return "redirect:/login";
        }

        // Tìm doctor theo user
        Doctor doctor = doctorService.findDoctorById(user);

        // Lấy toàn bộ lịch khám của bác sĩ
        List<Appointment> allAppointments =
                appointmentService.getAppointmentsByDoctor(doctor);

        // Lọc lịch khám hôm nay
        List<Appointment> todayAppointments =
                allAppointments.stream()

                        // Kiểm tra appointmentTime khác null
                        .filter(a ->
                                a.getAppointmentTime() != null &&

                                        // So sánh ngày với hôm nay
                                        a.getAppointmentTime()
                                                .toLocalDate()
                                                .equals(LocalDate.now())
                        )

                        // Chuyển stream thành list
                        .toList();

        // Đếm số lịch PENDING
        long pendingAppointments =
                todayAppointments.stream()

                        // Lọc trạng thái PENDING
                        .filter(a ->
                                "PENDING"
                                        .equalsIgnoreCase(a.getStatus())
                        )

                        // Đếm số lượng
                        .count();

        // Đếm số lịch COMPLETED
        long completedTasks =
                todayAppointments.stream()

                        // Lọc trạng thái COMPLETED
                        .filter(a ->
                                "COMPLETED"
                                        .equalsIgnoreCase(a.getStatus())
                        )

                        // Đếm số lượng
                        .count();

        // Gửi danh sách lịch chưa khám sang view
        model.addAttribute(
                "appointments",

                todayAppointments.stream()

                        // Chỉ lấy PENDING
                        .filter(a ->
                                "PENDING"
                                        .equalsIgnoreCase(a.getStatus())
                        )

                        .toList()
        );

        // Gửi số lịch pending
        model.addAttribute(
                "pendingAppointments",
                pendingAppointments
        );

        // Công việc khẩn = số lịch pending
        model.addAttribute(
                "urgentTasks",
                pendingAppointments
        );

        // Gửi số lịch đã hoàn thành
        model.addAttribute(
                "completedTasks",
                completedTasks
        );

        // Gửi ngày hôm nay
        model.addAttribute("today", LocalDate.now());

        // Gửi user login
        model.addAttribute("userLogin", user);

        // Trả về trang doctor_home.html
        return "doctor_home";
    }


    // ===================== DANH SÁCH BỆNH NHÂN =====================

    @GetMapping("/doctor/patients")
    public String doctorPatients(
            HttpSession session,
            Model model
    ) {

        // Lấy user đăng nhập
        User user = (User) session.getAttribute("userLogin");

        // Kiểm tra quyền bác sĩ
        if (user == null || !"DOCTOR".equals(user.getRole())) {
            return "redirect:/login";
        }

        // Tìm doctor theo user
        Doctor doctor = doctorService.findDoctorById(user);

        // Lấy danh sách lịch khám
        List<Appointment> appointments =
                appointmentService
                        .getAppointmentsByDoctor(doctor)

                        // Chuyển thành stream
                        .stream()

                        // Chỉ lấy lịch PENDING
                        .filter(a ->
                                "PENDING"
                                        .equalsIgnoreCase(a.getStatus())
                        )

                        // Chỉ lấy lịch từ hôm nay trở đi
                        .filter(a ->
                                a.getAppointmentTime() != null
                                        &&

                                        // Không lấy ngày trong quá khứ
                                        !a.getAppointmentTime()
                                                .toLocalDate()
                                                .isBefore(LocalDate.now())
                        )

                        // Sắp xếp theo thời gian tăng dần
                        .sorted((a1, a2) ->

                                a1.getAppointmentTime()
                                        .compareTo(
                                                a2.getAppointmentTime()
                                        )
                        )

                        // Chuyển về list
                        .toList();

        // Gửi appointments sang view
        model.addAttribute("appointments", appointments);

        // Gửi ngày hiện tại
        model.addAttribute("today", LocalDate.now());

        // Gửi user login
        model.addAttribute("userLogin", user);

        // Mở trang doctor_patients.html
        return "doctor_patients";
    }


    // ===================== FORM CHẨN ĐOÁN =====================

    @GetMapping("/doctor/diagnosis/{id}")
    public String doctorDiagnosis(

            // Lấy id appointment từ URL
            @PathVariable Long id,

            HttpSession session,
            Model model
    ) {

        // Lấy user login
        User user = (User) session.getAttribute("userLogin");

        // Kiểm tra quyền
        if (user == null || !"DOCTOR".equals(user.getRole())) {
            return "redirect:/login";
        }

        // Tìm appointment theo id
        Appointment appointment =
                appointmentService.getAppointmentById(id);

        // Nếu không tồn tại
        if (appointment == null) {
            return "redirect:/doctor/patients";
        }

        // Kiểm tra appointment có thuộc bác sĩ này không
        if (!appointment.getDoctor()
                .getUser()
                .getId()
                .equals(user.getId())) {

            return "redirect:/doctor/patients";
        }

        // Gửi appointment sang view
        model.addAttribute("appointment", appointment);

        // Gửi danh sách thuốc
        model.addAttribute(
                "medicines",
                medicineService.getAllMedicines()
        );

        // Gửi user login
        model.addAttribute("userLogin", user);

        // Mở form chẩn đoán
        return "doctor_diagnosis_receive";
    }


    // ===================== LƯU CHẨN ĐOÁN =====================

    // Transaction:
    // Nếu lỗi giữa chừng -> rollback database
    @Transactional

    @PostMapping("/doctor/diagnosis/save")
    public String saveDiagnosis(

            // id appointment
            @RequestParam Long appointmentId,

            // Triệu chứng
            @RequestParam String symptoms,

            // Chẩn đoán
            @RequestParam String diagnosis,

            // Kết quả điều trị
            @RequestParam(required = false)
            String result,

            // Danh sách id thuốc
            @RequestParam List<Long> medicineIds,

            // Danh sách số lượng thuốc
            @RequestParam List<Integer> quantities,

            // Danh sách liều dùng
            @RequestParam List<String> dosages,

            HttpSession session,
            Model model
    ) {

        // Lấy user login
        User user = (User) session.getAttribute("userLogin");

        // Kiểm tra quyền
        if (user == null || !"DOCTOR".equals(user.getRole())) {
            return "redirect:/login";
        }

        // Tìm appointment
        Appointment appointment =
                appointmentService.getAppointmentById(appointmentId);

        // Nếu không tồn tại
        if (appointment == null) {
            return "redirect:/doctor/patients";
        }

        // Gửi dữ liệu cũ sang view
        model.addAttribute("appointment", appointment);

        model.addAttribute(
                "medicines",
                medicineService.getAllMedicines()
        );

        model.addAttribute("symptoms", symptoms);
        model.addAttribute("diagnosis", diagnosis);
        model.addAttribute("resultValue", result);

        // Biến kiểm tra lỗi
        boolean hasError = false;


        // ===== KIỂM TRA TRIỆU CHỨNG =====

        if (symptoms == null || symptoms.trim().isEmpty()) {

            // Báo lỗi
            model.addAttribute(
                    "symptomsError",
                    "Vui lòng nhập triệu chứng"
            );

            hasError = true;
        }


        // ===== KIỂM TRA CHẨN ĐOÁN =====

        if (diagnosis == null || diagnosis.trim().isEmpty()) {

            model.addAttribute(
                    "diagnosisError",
                    "Vui lòng nhập chẩn đoán"
            );

            hasError = true;
        }


        // ===== KIỂM TRA THUỐC =====

        boolean hasMedicine = false;

        // Duyệt số lượng thuốc
        for (Integer qty : quantities) {

            // Nếu có ít nhất 1 thuốc
            if (qty != null && qty > 0) {

                hasMedicine = true;

                break;
            }
        }

        // Nếu chưa kê thuốc nào
        if (!hasMedicine) {

            model.addAttribute(
                    "medicineError",
                    "Phải kê ít nhất 1 loại thuốc"
            );

            hasError = true;
        }


        // Nếu có lỗi validation
        if (hasError) {

            // Quay lại form
            return "doctor_diagnosis_receive";
        }


        // ===== KIỂM TRA TỒN KHO THUỐC =====

        for (int i = 0; i < medicineIds.size(); i++) {

            // Lấy số lượng thuốc
            Integer qty = quantities.get(i);

            // Nếu <= 0 thì bỏ qua
            if (qty == null || qty <= 0) {
                continue;
            }

            // Tìm thuốc
            Medicine medicine =
                    medicineService.getMedicineById(
                            medicineIds.get(i)
                    );

            // Nếu tồn kho không đủ
            if (medicine.getStock() < qty) {

                // Báo lỗi
                model.addAttribute(
                        "error",
                        "Không đủ thuốc: " + medicine.getName()
                );

                return "doctor_diagnosis_receive";
            }
        }


        // ===================== TẠO BỆNH ÁN =====================

        // Tạo MedicalRecord mới
        MedicalRecord record = new MedicalRecord();

        // Gắn appointment
        record.setAppointment(appointment);

        // Gắn triệu chứng
        record.setSymptoms(symptoms);

        // Gắn chẩn đoán
        record.setDiagnosis(diagnosis);

        // Gắn kết quả
        record.setResult(result);

        // Thời gian tạo
        record.setCreatedAt(LocalDateTime.now());

        // Lưu bệnh án
        medicalRecordService.save(record);


        // ===================== TẠO ĐƠN THUỐC =====================

        // Tạo prescription
        Prescription prescription = new Prescription();

        // Gắn bệnh án
        prescription.setMedicalRecord(record);

        // Trạng thái cấp thuốc
        prescription.setStatus("DISPENSED");

        // Thời gian tạo
        prescription.setCreatedAt(LocalDateTime.now());

        // Danh sách chi tiết đơn thuốc
        List<PrescriptionDetail> details =
                new ArrayList<>();


        // ===== TẠO CHI TIẾT ĐƠN THUỐC =====

        for (int i = 0; i < medicineIds.size(); i++) {

            // Lấy số lượng thuốc
            Integer qty = quantities.get(i);

            // Nếu hợp lệ
            if (qty != null && qty > 0) {

                // Tìm thuốc
                Medicine medicine =
                        medicineService.getMedicineById(
                                medicineIds.get(i)
                        );

                // Trừ tồn kho
                medicine.setStock(
                        medicine.getStock() - qty
                );

                // Lưu thuốc
                medicineService.saveMedicine(medicine);

                // Tạo PrescriptionDetail
                PrescriptionDetail detail =
                        new PrescriptionDetail();

                // Gắn prescription
                detail.setPrescription(prescription);

                // Gắn thuốc
                detail.setMedicine(medicine);

                // Gắn số lượng
                detail.setQuantity(qty);

                // Gắn liều dùng
                detail.setDosage(dosages.get(i));

                // Thêm vào danh sách
                details.add(detail);
            }
        }

        // Gắn details vào prescription
        prescription.setDetails(details);

        // Gắn prescription vào medical record
        record.setPrescription(prescription);


        // ===================== CẬP NHẬT APPOINTMENT =====================

        // Đánh dấu đã khám
        appointment.setStatus("COMPLETED");

        // Ghi chú
        appointment.setNote("Đã khám và cấp thuốc");

        // Lưu appointment
        appointmentService.save(appointment);


        // Quay lại danh sách bệnh nhân
        return "redirect:/doctor/patients";
    }
}