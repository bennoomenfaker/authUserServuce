package tn.platformMedical.auth_user_service.dto.request;

import lombok.Data;

@Data
public class UpdateHospitalServiceRequest {
    private String hospitalId;
    private String serviceId;
}

