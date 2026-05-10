package com.example.smart_healthcare_platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class UserProfileUpdateDTO {

    private String avatar;

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")

    @Pattern(
            regexp = "^0\\d{10}$",
            message = "Số điện thoại phải gồm 11 số và bắt đầu bằng số 0"
    )
    private String phone;

    @NotBlank(message = "Địa chỉ không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String address;

    @NotNull(message = "Không đươc để chống ngày sinh")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Vui lòng chọn giới tính")
    private String gender;

}