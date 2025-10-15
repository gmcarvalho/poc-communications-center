package com.example.communications.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class KycValidationRequest {

    @NotBlank
    String customerId;

    @NotBlank
    String customerName;

    @Email
    String email;
}
