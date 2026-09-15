package com.fiap.appointment.config;

import com.fiap.appointment.entity.MedicalHistory;
import com.fiap.appointment.entity.User;
import com.fiap.appointment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MedicalHistoryResolver {

    private final UserRepository userRepository;

    @SchemaMapping(typeName = "MedicalHistory", field = "patient")
    public User patient(MedicalHistory medicalHistory) {
        return userRepository.findById(medicalHistory.getPatient().getId())
                .orElse(null);
    }
}
