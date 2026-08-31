package com.dalal.notificationsservicepfe.feign.dto;

import lombok.Builder;

@Builder
public record ProfilSummaryDto(
        Long id,
        String firstName,
        String lastName,
        String photoUrl,
        String phoneNumber
) {}
