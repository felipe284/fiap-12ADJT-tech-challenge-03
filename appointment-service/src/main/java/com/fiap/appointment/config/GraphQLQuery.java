package com.fiap.appointment.config;

import com.fiap.appointment.entity.MedicalHistory;
import com.fiap.appointment.service.MedicalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class GraphQLQuery {

    private final MedicalHistoryService medicalHistoryService;

    @QueryMapping
    public List<MedicalHistory> getPatientMedicalHistory(@Argument String patientId) {
        return medicalHistoryService.getPatientHistory(Long.parseLong(patientId));
    }

    @QueryMapping
    public MedicalHistory getMedicalHistory(@Argument String id) {
        return medicalHistoryService.getMedicalHistoryById(Long.parseLong(id));
    }
}
