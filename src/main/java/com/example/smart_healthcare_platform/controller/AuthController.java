package com.example.smart_healthcare_platform.controller;

import com.example.smart_healthcare_platform.dto.UserDTO;
import com.example.smart_healthcare_platform.model.Doctor;
import com.example.smart_healthcare_platform.model.User;
import com.example.smart_healthcare_platform.model.UserProfiles;
import com.example.smart_healthcare_platform.service.IDoctorService;
import com.example.smart_healthcare_platform.service.IUserProfilesService;
import com.example.smart_healthcare_platform.service.IUserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final IUserService userService;
    private final IUserProfilesService userProfilesService;
    private final IDoctorService doctorService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "sign-in";
    }
    @PostMapping("/handleRegister")
    public String handleRegister(
            @Valid @ModelAttribute("userDTO") UserDTO userDTO,
            BindingResult result,
            Model model,
            HttpSession session
    ) {
        if (result.hasErrors()) {
            return "sign-in";
        }
        if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
            model.addAttribute("passwordError", "Mật khẩu xác nhận không khớp");
            return "sign-in";
        }
        User checkEmail = userService.getUserByEmail(userDTO.getEmail());
        if (checkEmail != null) {
            model.addAttribute("emailExists", "Email đã tồn tại");
            return "sign-in";
        }
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole("PATIENT");
        user.setStatus(true);
        user = userService.saveUser(user);
        UserProfiles profile = new UserProfiles();
        profile.setUser(user);
        profile.setFullName(userDTO.getUsername());
        profile.setPhone("");
        profile.setAddress("");
        profile.setGender("");
        profile.setAvatar("");
        profile.setDateOfBirth(null);
        userProfilesService.saveUserProfiles(profile);
        session.setAttribute("userLogin", user);
        session.setAttribute("userProfiles", profile);
        return "redirect:/patient/home";
    }
    @GetMapping("/login")
    public String login() {
        return "log-in";
    }
    @PostMapping("/handleLogin")
    public String handleLogin(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String password,
            HttpSession session,
            Model model
    ) {
        if (email == null || email.trim().isEmpty()) {
            model.addAttribute("emailError", "Vui lòng nhập email");
            return "log-in";
        }
        if (password == null || password.trim().isEmpty()) {
            model.addAttribute("passwordError", "Vui lòng nhập mật khẩu");
            model.addAttribute("email", email);
            return "log-in";
        }
        User user = userService.getUserByEmail(email);
        if (user == null) {
            model.addAttribute("emailError", "Tài khoản không tồn tại");
            model.addAttribute("email", email);
            return "log-in";
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            model.addAttribute("passwordError", "Sai mật khẩu");
            model.addAttribute("email", email);
            return "log-in";
        }
        session.setAttribute("userLogin", user);
        UserProfiles profile = userProfilesService.getUserProfilesByUser(user);
        if (profile != null) {
            session.setAttribute("userProfiles", profile);
        }
        if ("ADMIN".equals(user.getRole())) {
            return "redirect:/admin/home";
        }
        if ("DOCTOR".equals(user.getRole())) {
            Doctor doctor = doctorService.findDoctorById(user);
            if (doctor != null && doctor.getSpecialty() != null) {
                session.setAttribute("specialtyName", doctor.getSpecialty().getName());
            }
            return "redirect:/doctor/home";
        }
        if ("PATIENT".equals(user.getRole())) {
            return "redirect:/patient/home";
        }
        return "redirect:/login";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}