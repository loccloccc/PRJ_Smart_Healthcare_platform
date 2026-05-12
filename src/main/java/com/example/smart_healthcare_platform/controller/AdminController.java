package com.example.smart_healthcare_platform.controller;

// Import DTO thuốc
import com.example.smart_healthcare_platform.dto.MedicineDTO;

// Import model Doctor
import com.example.smart_healthcare_platform.model.Doctor;

// Import model Medicine
import com.example.smart_healthcare_platform.model.Medicine;

// Import model User
import com.example.smart_healthcare_platform.model.User;

// Import model UserProfiles
import com.example.smart_healthcare_platform.model.UserProfiles;

// Service doctor
import com.example.smart_healthcare_platform.service.IDoctorService;

// Service medicine
import com.example.smart_healthcare_platform.service.IMedicineService;

// Service user profiles
import com.example.smart_healthcare_platform.service.IUserProfilesService;

// Service user
import com.example.smart_healthcare_platform.service.IUserService;

// Quản lý session
import jakarta.servlet.http.HttpSession;

// Validation dữ liệu form
import jakarta.validation.Valid;

// Lombok tự tạo constructor
import lombok.RequiredArgsConstructor;

// Đánh dấu Controller
import org.springframework.stereotype.Controller;

// Truyền dữ liệu sang View
import org.springframework.ui.Model;

// Chứa lỗi validation
import org.springframework.validation.BindingResult;

// Mapping request
import org.springframework.web.bind.annotation.*;


// Đánh dấu đây là Spring MVC Controller
@Controller

// Tự động tạo constructor cho các field final
@RequiredArgsConstructor
public class AdminController {

    // Service thuốc
    private final IMedicineService medicineService;

    // Service hồ sơ người dùng
    private final IUserProfilesService  userProfilesService;

    // Service user
    private final IUserService userService;

    // Service doctor
    private final IDoctorService doctorService;


    // ===================== TRANG CHỦ ADMIN =====================

    @GetMapping("/admin/home")
    public String adminHome(HttpSession session , Model model) {

        // Lấy user đăng nhập
        User user = (User) session.getAttribute("userLogin");

        // Nếu chưa login hoặc không phải ADMIN
        if(user == null || !user.getRole().equals("ADMIN")){

            // Chuyển về login
            return "redirect:/login";
        }

        // Gửi user login sang view
        model.addAttribute("userLogin", user);

        // Mở admin_home.html
        return "admin_home";
    }


    // ===================== DANH SÁCH THUỐC =====================

    @GetMapping("/admin/medicines")
    public String listMedicines(Model model,
                                HttpSession session) {

        // Lấy user login
        User user = (User) session.getAttribute("userLogin");

        // Kiểm tra quyền ADMIN
        if (user == null || !user.getRole().equals("ADMIN")) {

            return "redirect:/login";
        }

        // Gửi user login
        model.addAttribute("userLogin", user);

        // Gửi danh sách thuốc
        model.addAttribute(
                "medicines",
                medicineService.getAllMedicines()
        );

        // Mở admin_medicines.html
        return "admin_medicines";
    }


    // ===================== FORM THÊM THUỐC =====================

    @GetMapping("/admin/addMedicine")
    public String addMedicineForm(Model model) {

        // Tạo object Medicine rỗng
        model.addAttribute("medicine", new Medicine());

        // Gửi action form
        model.addAttribute(
                "formAction",
                "/admin/addMedicine"
        );

        // Mở form thêm thuốc
        return "admin_add_medicine";
    }


    // ===================== LƯU THUỐC =====================

    @PostMapping("/admin/addMedicine")
    public String saveMedicine(

            // Validate dữ liệu form
            @Valid

            // Mapping form -> DTO
            @ModelAttribute("medicine")
            MedicineDTO medicineDTO,

            // Chứa lỗi validation
            BindingResult result,

            Model model
    ) {

        // Nếu validation lỗi
        if (result.hasErrors()) {

            // Gửi lại action form
            model.addAttribute(
                    "formAction",
                    "/admin/addMedicine"
            );

            // Quay lại form
            return "admin_add_medicine";
        }


        // ===================== TẠO MEDICINE =====================

        // Tạo object Medicine
        Medicine medicine = new Medicine();

        // Gán id
        medicine.setId(medicineDTO.getId());

        // Gán tên thuốc
        medicine.setName(medicineDTO.getName());

        // Gán số lượng tồn kho
        medicine.setStock(medicineDTO.getStock());

        // Gán đơn vị
        medicine.setUnit(medicineDTO.getUnit());

        // Gán giá
        medicine.setPrice(medicineDTO.getPrice());

        // Gán mô tả
        medicine.setDescription(
                medicineDTO.getDescription()
        );

        // Lưu vào database
        medicineService.saveMedicine(medicine);

        // Quay lại danh sách thuốc
        return "redirect:/admin/medicines";
    }


    // ===================== FORM SỬA THUỐC =====================

    @GetMapping("/admin/editMedicine/{id}")
    public String editMedicineForm(

            // Lấy id thuốc từ URL
            @PathVariable Long id,

            Model model
    ) {

        // Tìm thuốc theo id
        Medicine med =
                medicineService.getMedicineById(id);

        // Gửi thuốc sang view
        model.addAttribute("medicine", med);

        // Action form sửa
        model.addAttribute(
                "formAction",
                "/admin/editMedicine"
        );

        // Mở form
        return "admin_add_medicine";
    }


    // ===================== CẬP NHẬT THUỐC =====================

    @PostMapping("/admin/editMedicine")
    public String updateMedicine(

            // Validate dữ liệu
            @Valid

            // Mapping form -> DTO
            @ModelAttribute("medicine")
            MedicineDTO medicineDTO,

            // Chứa lỗi validation
            BindingResult result,

            Model model
    ) {

        // Nếu validation lỗi
        if (result.hasErrors()) {

            // Gửi lại action form
            model.addAttribute(
                    "formAction",
                    "/admin/editMedicine"
            );

            // Quay lại form
            return "admin_add_medicine";
        }


        // ===================== TẠO OBJECT MEDICINE =====================

        // Tạo object Medicine
        Medicine medicine = new Medicine();

        // Gán id
        medicine.setId(medicineDTO.getId());

        // Gán tên thuốc
        medicine.setName(medicineDTO.getName());

        // Gán tồn kho
        medicine.setStock(medicineDTO.getStock());

        // Gán đơn vị
        medicine.setUnit(medicineDTO.getUnit());

        // Gán giá
        medicine.setPrice(medicineDTO.getPrice());

        // Gán mô tả
        medicine.setDescription(
                medicineDTO.getDescription()
        );

        // Cập nhật database
        medicineService.updateMedicine(medicine);

        // Quay lại danh sách thuốc
        return "redirect:/admin/medicines";
    }


    // ===================== XÓA THUỐC =====================

    @GetMapping("/admin/deleteMedicine/{id}")
    public String deleteMedicine(

            // Lấy id thuốc
            @PathVariable Long id
    ) {

        // Xóa thuốc
        medicineService.deleteMedicine(id);

        // Quay lại danh sách
        return "redirect:/admin/medicines";
    }


    // ===================== DANH SÁCH BÁC SĨ =====================

    @GetMapping("/admin/doctors")
    public String doctors(Model model,
                          HttpSession session) {

        // Gửi danh sách user
        model.addAttribute(
                "user",
                userService.getUsers()
        );

        // Gửi danh sách hồ sơ doctor
        model.addAttribute(
                "userProfiles",

                userProfilesService
                        .getUserProfiles()

                        .stream()

                        // Chỉ lấy role DOCTOR
                        .filter(
                                u -> u.getUser()
                                        .getRole()
                                        .equals("DOCTOR")
                        )
        );

        // Lấy user login
        User user =
                (User) session.getAttribute("userLogin");

        // Tìm doctor theo user
        Doctor doctor =
                doctorService.findDoctorById(user);

        // Gửi doctor sang view
        model.addAttribute("doctor",doctor);

        // Mở admin_doctor_manage.html
        return "admin_doctor_manage";
    }


    // ===================== DANH SÁCH BỆNH NHÂN =====================

    @GetMapping("/admin/patients")
    public String patients(Model model) {

        // Gửi danh sách user
        model.addAttribute(
                "user",
                userService.getUsers()
        );

        // Gửi danh sách hồ sơ patient
        model.addAttribute(
                "userProfiles",

                userProfilesService
                        .getUserProfiles()

                        .stream()

                        // Chỉ lấy role PATIENT
                        .filter(
                                u -> u.getUser()
                                        .getRole()
                                        .equals("PATIENT")
                        )
        );

        // Mở admin_patients_manage.html
        return "admin_patients_manage";
    }

}