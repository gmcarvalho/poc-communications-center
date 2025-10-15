package com.example.communications.application.dto;

import jakarta.validation.constraints.NotNull;

public record CardStatusUpdateRequest(@NotNull Boolean cardIssued) {}
