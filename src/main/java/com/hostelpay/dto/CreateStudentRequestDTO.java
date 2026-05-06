package com.hostelpay.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateStudentRequestDTO {
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    private LocalDate dob;
    private String aadharUrl;

    public void setDob(String dob) {
        if (dob == null || dob.trim().isEmpty()) {
            this.dob = null;
        } else {
            this.dob = LocalDate.parse(dob);
        }
    }

    public void setAadharUrl(String aadharUrl) {
        if (aadharUrl == null || aadharUrl.trim().isEmpty()) {
            this.aadharUrl = null;
        } else {
            this.aadharUrl = aadharUrl;
        }
    }
}
