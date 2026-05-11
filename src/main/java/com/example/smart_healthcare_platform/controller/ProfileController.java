package com.example.smart_healthcare_platform.controller;

import com.example.smart_healthcare_platform.dto.UserProfileUpdateDTO;
import com.example.smart_healthcare_platform.model.Doctor;
import com.example.smart_healthcare_platform.model.User;
import com.example.smart_healthcare_platform.model.UserProfiles;

import com.example.smart_healthcare_platform.service.IDoctorService;
import com.example.smart_healthcare_platform.service.IUserProfilesService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ProfileController {
    private final IUserProfilesService userProfilesService;
    private final IDoctorService doctorService;
    @GetMapping("/admin/profile")
    public String adminProfile(HttpSession session,
                               Model model) {
        User user = (User) session.getAttribute("userLogin");
        if (user == null) {
            return "redirect:/login";
        }
        UserProfiles profile = userProfilesService.getUserProfilesByUser(user);
        UserProfileUpdateDTO dto = convertToDTO(profile);
        model.addAttribute("userLogin", user);
        model.addAttribute("userProfiles", dto);
        return "profile";
    }
    @GetMapping("/doctor/profile")
    public String doctorProfile(HttpSession session,
                                Model model) {
        User user = (User) session.getAttribute("userLogin");
        if (user == null || !"DOCTOR".equals(user.getRole())) {
            return "redirect:/login";
        }
        Doctor doctor = doctorService.findDoctorById(user);
        UserProfiles profile = userProfilesService.getUserProfilesByUser(user);
        UserProfileUpdateDTO dto = convertToDTO(profile);
        model.addAttribute("userLogin", user);
        model.addAttribute("doctor", doctor);
        model.addAttribute("userProfiles", dto);
        return "profile";
    }
    @GetMapping("/patient/profile")
    public String patientProfile(HttpSession session,
                                 Model model) {
        User user = (User) session.getAttribute("userLogin");
        if (user == null || !"PATIENT".equals(user.getRole())) {
            return "redirect:/login";
        }
        UserProfiles profile = userProfilesService.getUserProfilesByUser(user);
        UserProfileUpdateDTO dto = convertToDTO(profile);
        model.addAttribute("userLogin", user);
        model.addAttribute("userProfiles", dto);
        return "profile";
    }
    @PostMapping("/profile/update")
    public String updateProfile(
            @Valid
            @ModelAttribute("userProfiles")
            UserProfileUpdateDTO dto,
            BindingResult result,
            HttpSession session,
            Model model
    ) {
        User user = (User) session.getAttribute("userLogin");
        if (user == null) {
            return "redirect:/login";
        }
        if ("DOCTOR".equals(user.getRole())) {
            Doctor doctor = doctorService.findDoctorById(user);
            model.addAttribute("doctor", doctor);
        }
        model.addAttribute("userLogin", user);
        if (result.hasErrors()) {
            return "profile";
        }
        UserProfiles profile = userProfilesService.getUserProfilesByUser(user);
        if (profile == null) {
            profile = new UserProfiles();
            profile.setUser(user);
        }
        profile.setFullName(dto.getFullName());
        profile.setPhone(dto.getPhone());
        profile.setGender(dto.getGender());
        profile.setAddress(dto.getAddress());
        profile.setDateOfBirth(dto.getDateOfBirth());
        userProfilesService.saveUserProfiles(profile);
        // redirect theo role
        if ("ADMIN".equals(user.getRole())) {
            return "redirect:/admin/profile";
        }
        if ("DOCTOR".equals(user.getRole())) {
            return "redirect:/doctor/profile";
        }
        return "redirect:/patient/profile";
    }

    private UserProfileUpdateDTO convertToDTO(UserProfiles profile) {
        UserProfileUpdateDTO dto = new UserProfileUpdateDTO();
        if (profile != null) {
            dto.setFullName(profile.getFullName());
            dto.setPhone(profile.getPhone());
            dto.setGender(profile.getGender());
            dto.setAddress(profile.getAddress());
            dto.setDateOfBirth(profile.getDateOfBirth());
        }
        return dto;
    }

}