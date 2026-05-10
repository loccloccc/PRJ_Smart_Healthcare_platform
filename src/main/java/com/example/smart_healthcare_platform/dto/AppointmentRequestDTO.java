package com.example.smart_healthcare_platform.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class AppointmentRequestDTO {

    private Long id;

    @NotNull(message = "Doctor không được null")
    private Long doctorId;

    @NotNull(message = "Patient không được null")
    private Long patientId;

    @NotNull(message = "Vui lòng chọn ngày khám")
    @Future(message = "Ngày khám phải ở tương lai")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime appointmentTime;

    @Size(max = 500,
            message = "Ghi chú tối đa 500 ký tự")
    private String note;
}