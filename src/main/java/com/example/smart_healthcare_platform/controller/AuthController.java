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

    // ================= REGISTER =================

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

        // validate form
        if (result.hasErrors()) {

            return "sign-in";
        }

        // confirm password
        if (!userDTO.getPassword()
                .equals(userDTO.getConfirmPassword())) {

            model.addAttribute(
                    "passwordError",
                    "Mật khẩu xác nhận không khớp"
            );

            return "sign-in";
        }

        // check email exists
        User checkEmail =
                userService.getUserByEmail(userDTO.getEmail());

        if (checkEmail != null) {

            model.addAttribute(
                    "emailExists",
                    "Email đã tồn tại"
            );

            return "sign-in";
        }

        // ================= CREATE USER =================

        User user = new User();

        user.setUsername(userDTO.getUsername());

        user.setEmail(userDTO.getEmail());

        user.setPassword(
                passwordEncoder.encode(userDTO.getPassword())
        );

        user.setRole("PATIENT");

        user.setStatus(true);

        // save user
        user = userService.saveUser(user);

        // ================= CREATE PROFILE =================

        UserProfiles profile = new UserProfiles();

        // CHỈ set user
        // @MapsId sẽ tự lấy user_id
        profile.setUser(user);

        profile.setFullName(userDTO.getUsername());

        profile.setPhone("");

        profile.setAddress("");

        profile.setGender("");

        profile.setAvatar("");

        profile.setDateOfBirth(null);

        userProfilesService.saveUserProfiles(profile);

        // ================= SESSION =================

        session.setAttribute(
                "userLogin",
                user
        );

        session.setAttribute(
                "userProfiles",
                profile
        );

        // redirect vào home
        return "redirect:/patient/home";
    }

    // ================= LOGIN =================

    @GetMapping("/login")
    public String login() {

        return "log-in";
    }

    @PostMapping("/handleLogin")
    public String handleLogin(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session
    ) {

        User user =
                userService.getUserByEmail(email);

        if (user == null ||
                !passwordEncoder.matches(
                        password,
                        user.getPassword()
                )) {

            return "redirect:/login?error";
        }

        // save session
        session.setAttribute(
                "userLogin",
                user
        );

        UserProfiles profile =
                userProfilesService
                        .getUserProfilesByUser(user);

        if (profile != null) {

            session.setAttribute(
                    "userProfiles",
                    profile
            );
        }

        // ================= ADMIN =================

        if ("ADMIN".equals(user.getRole())) {

            return "redirect:/admin/home";
        }

        // ================= DOCTOR =================

        if ("DOCTOR".equals(user.getRole())) {

            Doctor doctor =
                    doctorService.findDoctorById(user);

            if (doctor != null &&
                    doctor.getSpecialty() != null) {

                session.setAttribute(
                        "specialtyName",
                        doctor.getSpecialty().getName()
                );
            }

            return "redirect:/doctor/home";
        }

        // ================= PATIENT =================

        if ("PATIENT".equals(user.getRole())) {

            return "redirect:/patient/home";
        }

        return "redirect:/login";
    }

    // ================= LOGOUT =================

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }
}