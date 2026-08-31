package com.dalal.notificationsservicepfe.feign.client;


import com.dalal.notificationsservicepfe.feign.dto.ProfilSummaryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "IDENTITY-SERVICE-PFE")
public interface IdentityClient {
    @GetMapping("/api/v1/auth/profil/{id}")
    ProfilSummaryDto getProfilDetail(@PathVariable Long id);
}
