package com.example.smart_healthcare_platform.controller;

// Import DTO đăng ký user
import com.example.smart_healthcare_platform.dto.UserDTO;

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

// Service xử lý User
import com.example.smart_healthcare_platform.service.IUserService;

// Quản lý session
import jakarta.servlet.http.HttpSession;

// Validation form
import jakarta.validation.Valid;

// Lombok tự tạo constructor
import lombok.RequiredArgsConstructor;

// Mã hóa mật khẩu
import org.springframework.security.crypto.password.PasswordEncoder;

// Đánh dấu Controller
import org.springframework.stereotype.Controller;

// Truyền dữ liệu sang View
import org.springframework.ui.Model;

// Chứa lỗi validation
import org.springframework.validation.BindingResult;

// Annotation mapping request
import org.springframework.web.bind.annotation.*;


// Đánh dấu class là Spring MVC Controller
@Controller

// Lombok tự tạo constructor cho field final
@RequiredArgsConstructor
public class AuthController {

    // Service user
    private final IUserService userService;

    // Service hồ sơ người dùng
    private final IUserProfilesService userProfilesService;

    // Service doctor
    private final IDoctorService doctorService;

    // Dùng mã hóa password
    private final PasswordEncoder passwordEncoder;


    // ===================== FORM REGISTER =====================

    @GetMapping("/register")
    public String register(Model model) {

        // Tạo object UserDTO rỗng gửi sang form
        model.addAttribute("userDTO", new UserDTO());

        // Mở trang sign-in.html
        return "sign-in";
    }


    // ===================== XỬ LÝ ĐĂNG KÝ =====================

    @PostMapping("/handleRegister")
    public String handleRegister(

            // Validate dữ liệu form
            @Valid

            // Mapping form -> UserDTO
            @ModelAttribute("userDTO")
            UserDTO userDTO,

            // Chứa lỗi validation
            BindingResult result,

            Model model,

            HttpSession session
    ) {

        // Nếu validation lỗi
        if (result.hasErrors()) {

            // Quay lại form đăng ký
            return "sign-in";
        }


        // ===== KIỂM TRA XÁC NHẬN MẬT KHẨU =====

        // Nếu password và confirmPassword không khớp
        if (!userDTO.getPassword()
                .equals(userDTO.getConfirmPassword())) {

            // Gửi lỗi
            model.addAttribute(
                    "passwordError",
                    "Mật khẩu xác nhận không khớp"
            );

            // Quay lại form
            return "sign-in";
        }


        // ===== KIỂM TRA EMAIL TỒN TẠI =====

        // Tìm user theo email
        User checkEmail =
                userService.getUserByEmail(userDTO.getEmail());

        // Nếu email đã tồn tại
        if (checkEmail != null) {

            // Gửi lỗi
            model.addAttribute(
                    "emailExists",
                    "Email đã tồn tại"
            );

            // Quay lại form
            return "sign-in";
        }


        // ===================== TẠO USER =====================

        // Tạo user mới
        User user = new User();

        // Gán username
        user.setUsername(userDTO.getUsername());

        // Gán email
        user.setEmail(userDTO.getEmail());

        // Mã hóa password rồi lưu
        user.setPassword(
                passwordEncoder.encode(
                        userDTO.getPassword()
                )
        );

        // Role mặc định là PATIENT
        user.setRole("PATIENT");

        // Trạng thái hoạt động
        user.setStatus(true);

        // Lưu user vào database
        user = userService.saveUser(user);


        // ===================== TẠO USER PROFILE =====================

        // Tạo hồ sơ người dùng
        UserProfiles profile = new UserProfiles();

        // Gắn user
        profile.setUser(user);

        // FullName mặc định = username
        profile.setFullName(userDTO.getUsername());

        // Khởi tạo dữ liệu rỗng
        profile.setPhone("");
        profile.setAddress("");
        profile.setGender("");
        profile.setAvatar("");

        // Chưa có ngày sinh
        profile.setDateOfBirth(null);

        // Lưu profile
        userProfilesService.saveUserProfiles(profile);


        // ===================== LƯU SESSION =====================

        // Lưu user đăng nhập
        session.setAttribute("userLogin", user);

        // Lưu profile
        session.setAttribute("userProfiles", profile);


        // Chuyển đến trang chủ patient
        return "redirect:/patient/home";
    }


    // ===================== FORM LOGIN =====================

    @GetMapping("/login")
    public String login() {
        // Mở trang log-in.html
        return "log-in";
    }


    // ===================== XỬ LÝ LOGIN =====================

    @PostMapping("/handleLogin")
    public String handleLogin(

            // Nhận email từ form
            @RequestParam(required = false)
            String email,

            // Nhận password từ form
            @RequestParam(required = false)
            String password,

            HttpSession session,

            Model model
    ) {


        // ===== KIỂM TRA EMAIL =====

        // Nếu email rỗng
        if (email == null || email.trim().isEmpty()) {

            // Báo lỗi
            model.addAttribute(
                    "emailError",
                    "Vui lòng nhập email"
            );

            return "log-in";
        }


        // ===== KIỂM TRA PASSWORD =====

        // Nếu password rỗng
        if (password == null || password.trim().isEmpty()) {

            // Báo lỗi
            model.addAttribute(
                    "passwordError",
                    "Vui lòng nhập mật khẩu"
            );

            // Giữ lại email cũ
            model.addAttribute("email", email);

            return "log-in";
        }


        // ===== KIỂM TRA USER TỒN TẠI =====

        // Tìm user theo email
        User user = userService.getUserByEmail(email);

        // Nếu không tồn tại
        if (user == null) {

            // Báo lỗi
            model.addAttribute(
                    "emailError",
                    "Tài khoản không tồn tại"
            );

            // Giữ lại email
            model.addAttribute("email", email);

            return "log-in";
        }


        // ===== KIỂM TRA PASSWORD =====

        // So sánh password nhập vào với password mã hóa
        if (!passwordEncoder.matches(
                password,
                user.getPassword()
        )) {

            // Báo lỗi
            model.addAttribute(
                    "passwordError",
                    "Sai mật khẩu"
            );

            // Giữ email
            model.addAttribute("email", email);

            return "log-in";
        }


        // ===================== LOGIN THÀNH CÔNG =====================

        // Lưu user vào session
        session.setAttribute("userLogin", user);


        // ===== LẤY PROFILE =====

        // Tìm hồ sơ user
        UserProfiles profile =
                userProfilesService.getUserProfilesByUser(user);

        // Nếu tồn tại profile
        if (profile != null) {

            // Lưu profile vào session
            session.setAttribute("userProfiles", profile);
        }


        // ===================== REDIRECT THEO ROLE =====================

        // Nếu là ADMIN
        if ("ADMIN".equals(user.getRole())) {

            return "redirect:/admin/home";
        }


        // Nếu là DOCTOR
        if ("DOCTOR".equals(user.getRole())) {

            // Tìm doctor
            Doctor doctor =
                    doctorService.findDoctorById(user);

            // Nếu có chuyên khoa
            if (doctor != null &&
                    doctor.getSpecialty() != null) {

                // Lưu tên chuyên khoa vào session
                session.setAttribute(
                        "specialtyName",
                        doctor.getSpecialty().getName()
                );
            }

            // Chuyển đến doctor/home
            return "redirect:/doctor/home";
        }


        // Nếu là PATIENT
        if ("PATIENT".equals(user.getRole())) {

            // Chuyển đến patient/home
            return "redirect:/patient/home";
        }


        // Nếu role không hợp lệ
        return "redirect:/login";
    }


    // ===================== LOGOUT =====================

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        // Xóa toàn bộ session
        session.invalidate();

        // Quay về login
        return "redirect:/login";
    }
}