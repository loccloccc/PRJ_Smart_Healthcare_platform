package com.example.smart_healthcare_platform.controller;

import com.example.smart_healthcare_platform.dto.MedicineDTO;
import com.example.smart_healthcare_platform.model.Medicine;
import com.example.smart_healthcare_platform.model.User;
import com.example.smart_healthcare_platform.service.IMedicineService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AdminController {
    private final IMedicineService medicineService;

    @GetMapping("/admin/home")
    public String adminHome(HttpSession session , Model model) {
        User user = (User) session.getAttribute("userLogin");
        if(user == null || !user.getRole().equals("ADMIN")){
            return "redirect:/login";
        }
        model.addAttribute("userLogin", user);
        return "admin_home";
    }

    @GetMapping("/admin/profile")
    public String profile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("userLogin");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("userLogin", user);
        return "profile";
    }




    @GetMapping("/admin/medicines")
    public String listMedicines(Model model, HttpSession session) {
        User user = (User) session.getAttribute("userLogin");
        if (user == null || !user.getRole().equals("ADMIN")) {
            return "redirect:/login";
        }
        model.addAttribute("userLogin", user);
        model.addAttribute("medicines", medicineService.getAllMedicines());
        return "admin_medicines";
    }

    @GetMapping("/admin/addMedicine")
    public String addMedicineForm(Model model) {
        model.addAttribute("medicine", new Medicine());
        model.addAttribute("formAction", "/admin/addMedicine");
        return "admin_add_medicine";
    }

    @PostMapping("/admin/addMedicine")
    public String saveMedicine(@Valid @ModelAttribute("medicine") MedicineDTO medicineDTO,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("formAction", "/admin/addMedicine");
            return "admin_add_medicine";
        }

        Medicine medicine = new Medicine();
        medicine.setId(medicineDTO.getId());
        medicine.setName(medicineDTO.getName());
        medicine.setStock(medicineDTO.getStock());
        medicine.setUnit(medicineDTO.getUnit());
        medicine.setPrice(medicineDTO.getPrice());
        medicine.setDescription(medicineDTO.getDescription());

        medicineService.saveMedicine(medicine);
        return "redirect:/admin/medicines";
    }

    @GetMapping("/admin/editMedicine/{id}")
    public String editMedicineForm(@PathVariable Long id, Model model) {
        Medicine med = medicineService.getMedicineById(id);
        model.addAttribute("medicine", med);
        model.addAttribute("formAction", "/admin/editMedicine");
        return "admin_add_medicine";
    }

    @PostMapping("/admin/editMedicine")
    public String updateMedicine(@Valid @ModelAttribute("medicine") MedicineDTO medicineDTO,
                                 BindingResult result,
                                 Model model) {
        if (result.hasErrors()) {
            model.addAttribute("formAction", "/admin/editMedicine");
            return "admin_add_medicine";
        }

        Medicine medicine = new Medicine();
        medicine.setId(medicineDTO.getId());
        medicine.setName(medicineDTO.getName());
        medicine.setStock(medicineDTO.getStock());
        medicine.setUnit(medicineDTO.getUnit());
        medicine.setPrice(medicineDTO.getPrice());
        medicine.setDescription(medicineDTO.getDescription());

        medicineService.updateMedicine(medicine);
        return "redirect:/admin/medicines";
    }


    @GetMapping("/admin/deleteMedicine/{id}")
    public String deleteMedicine(@PathVariable Long id) {
        medicineService.deleteMedicine(id);
        return "redirect:/admin/medicines";
    }

}

