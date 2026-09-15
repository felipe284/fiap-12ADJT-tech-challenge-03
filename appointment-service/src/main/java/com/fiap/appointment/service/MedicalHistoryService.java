package com.fiap.appointment.service;

import com.fiap.appointment.entity.MedicalHistory;
import com.fiap.appointment.entity.User;
import com.fiap.appointment.repository.MedicalHistoryRepository;
import com.fiap.appointment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalHistoryService {

    private final MedicalHistoryRepository medicalHistoryRepository;
    private final UserRepository userRepository;

    public MedicalHistory saveMedicalHistory(MedicalHistory medicalHistory) {
        return medicalHistoryRepository.save(medicalHistory);
    }

    public List<MedicalHistory> getPatientHistory(Long patientId) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        return medicalHistoryRepository.findByPatient(patient);
    }

    public MedicalHistory getMedicalHistoryById(Long id) {
        return medicalHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medical history not found"));
    }
}
