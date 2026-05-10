package com.example.smart_healthcare_platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UserDTO {
    private Long id;
    @NotBlank(message = "Không được để trống")
    private String username;

    @NotBlank(message = "Không được để trống")
    @Length(min = 8 , message = "Mật khẩu phải lớn hơn 8 ký tự")
    private String password;

    @NotBlank(message = "Không được để trống")
    private String confirmPassword;

    @NotBlank(message = "Không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;
}