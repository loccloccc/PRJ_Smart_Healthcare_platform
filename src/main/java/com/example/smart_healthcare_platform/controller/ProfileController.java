package com.example.smart_healthcare_platform.controller;

// Import DTO cập nhật hồ sơ người dùng
import com.example.smart_healthcare_platform.dto.UserProfileUpdateDTO;

// Import model Doctor
import com.example.smart_healthcare_platform.model.Doctor;

// Import model User
import com.example.smart_healthcare_platform.model.User;

// Import model UserProfiles
import com.example.smart_healthcare_platform.model.UserProfiles;

// Service xử lý Doctor
import com.example.smart_healthcare_platform.service.IDoctorService;

// Service xử lý UserProfiles
import com.example.smart_healthcare_platform.service.IUserProfilesService;

// Quản lý session
import jakarta.servlet.http.HttpSession;

// Validation dữ liệu form
import jakarta.validation.Valid;

// Lombok tự tạo constructor cho field final
import lombok.RequiredArgsConstructor;

// Đánh dấu class là Controller
import org.springframework.stereotype.Controller;

// Dùng truyền dữ liệu sang View
import org.springframework.ui.Model;

// Chứa lỗi validation
import org.springframework.validation.BindingResult;

// Mapping GET
import org.springframework.web.bind.annotation.GetMapping;

// Mapping dữ liệu form vào object
import org.springframework.web.bind.annotation.ModelAttribute;

// Mapping POST
import org.springframework.web.bind.annotation.PostMapping;


// Đánh dấu đây là Spring MVC Controller
@Controller

// Tự tạo constructor cho các field final
@RequiredArgsConstructor
public class ProfileController {

    // Service hồ sơ người dùng
    private final IUserProfilesService userProfilesService;

    // Service bác sĩ
    private final IDoctorService doctorService;


    // ===================== PROFILE ADMIN =====================

    @GetMapping("/admin/profile")
    public String adminProfile(HttpSession session,
                               Model model) {

        // Lấy user đăng nhập từ session
        User user = (User) session.getAttribute("userLogin");

        // Nếu chưa login
        if (user == null) {

            // Chuyển về login
            return "redirect:/login";
        }

        // Lấy hồ sơ user
        UserProfiles profile =
                userProfilesService.getUserProfilesByUser(user);

        // Chuyển entity sang DTO
        UserProfileUpdateDTO dto = convertToDTO(profile);

        // Gửi user login sang view
        model.addAttribute("userLogin", user);

        // Gửi DTO hồ sơ sang view
        model.addAttribute("userProfiles", dto);

        // Mở trang profile.html
        return "profile";
    }


    // ===================== PROFILE DOCTOR =====================

    @GetMapping("/doctor/profile")
    public String doctorProfile(HttpSession session,
                                Model model) {

        // Lấy user đăng nhập
        User user = (User) session.getAttribute("userLogin");

        // Kiểm tra quyền bác sĩ
        if (user == null || !"DOCTOR".equals(user.getRole())) {

            return "redirect:/login";
        }

        // Tìm doctor theo user
        Doctor doctor = doctorService.findDoctorById(user);

        // Lấy hồ sơ user
        UserProfiles profile =
                userProfilesService.getUserProfilesByUser(user);

        // Convert entity -> DTO
        UserProfileUpdateDTO dto = convertToDTO(profile);

        // Gửi user login
        model.addAttribute("userLogin", user);

        // Gửi doctor
        model.addAttribute("doctor", doctor);

        // Gửi DTO hồ sơ
        model.addAttribute("userProfiles", dto);

        // Mở profile.html
        return "profile";
    }


    // ===================== PROFILE PATIENT =====================

    @GetMapping("/patient/profile")
    public String patientProfile(HttpSession session,
                                 Model model) {

        // Lấy user login
        User user = (User) session.getAttribute("userLogin");

        // Kiểm tra quyền patient
        if (user == null || !"PATIENT".equals(user.getRole())) {

            return "redirect:/login";
        }

        // Lấy hồ sơ user
        UserProfiles profile =
                userProfilesService.getUserProfilesByUser(user);

        // Convert entity -> DTO
        UserProfileUpdateDTO dto = convertToDTO(profile);

        // Gửi user login
        model.addAttribute("userLogin", user);

        // Gửi DTO hồ sơ
        model.addAttribute("userProfiles", dto);

        // Mở profile.html
        return "profile";
    }


    // ===================== CẬP NHẬT PROFILE =====================

    @PostMapping("/profile/update")
    public String updateProfile(

            // Validate dữ liệu form
            @Valid

            // Mapping form -> DTO
            @ModelAttribute("userProfiles")
            UserProfileUpdateDTO dto,

            // Chứa lỗi validation
            BindingResult result,

            HttpSession session,

            Model model
    ) {

        // Lấy user login
        User user = (User) session.getAttribute("userLogin");

        // Nếu chưa login
        if (user == null) {

            return "redirect:/login";
        }

        // Nếu là doctor
        if ("DOCTOR".equals(user.getRole())) {

            // Lấy doctor
            Doctor doctor =
                    doctorService.findDoctorById(user);

            // Gửi doctor sang view
            model.addAttribute("doctor", doctor);
        }

        // Gửi user login
        model.addAttribute("userLogin", user);


        // ===== VALIDATION ERROR =====

        // Nếu form có lỗi validation
        if (result.hasErrors()) {

            // Quay lại profile.html
            return "profile";
        }


        // ===== LẤY PROFILE =====

        // Tìm profile của user
        UserProfiles profile =
                userProfilesService.getUserProfilesByUser(user);

        // Nếu chưa có profile
        if (profile == null) {

            // Tạo mới profile
            profile = new UserProfiles();

            // Gắn user
            profile.setUser(user);
        }


        // ===== CẬP NHẬT DỮ LIỆU =====

        // Cập nhật họ tên
        profile.setFullName(dto.getFullName());

        // Cập nhật số điện thoại
        profile.setPhone(dto.getPhone());

        // Cập nhật giới tính
        profile.setGender(dto.getGender());

        // Cập nhật địa chỉ
        profile.setAddress(dto.getAddress());

        // Cập nhật ngày sinh
        profile.setDateOfBirth(dto.getDateOfBirth());


        // ===== LƯU DATABASE =====

        // Lưu profile
        userProfilesService.saveUserProfiles(profile);


        // ===== REDIRECT THEO ROLE =====

        // Nếu ADMIN
        if ("ADMIN".equals(user.getRole())) {

            return "redirect:/admin/profile";
        }

        // Nếu DOCTOR
        if ("DOCTOR".equals(user.getRole())) {

            return "redirect:/doctor/profile";
        }

        // Mặc định PATIENT
        return "redirect:/patient/profile";
    }


    // ===================== ENTITY -> DTO =====================

    // Hàm convert UserProfiles sang DTO
    private UserProfileUpdateDTO convertToDTO(
            UserProfiles profile
    ) {

        // Tạo DTO mới
        UserProfileUpdateDTO dto =
                new UserProfileUpdateDTO();

        // Nếu profile tồn tại
        if (profile != null) {

            // Gán fullName
            dto.setFullName(profile.getFullName());

            // Gán phone
            dto.setPhone(profile.getPhone());

            // Gán gender
            dto.setGender(profile.getGender());

            // Gán address
            dto.setAddress(profile.getAddress());

            // Gán dateOfBirth
            dto.setDateOfBirth(profile.getDateOfBirth());
        }

        // Trả về DTO
        return dto;
    }

}