package com.fiap.appointment.repository;

import com.fiap.appointment.entity.MedicalHistory;
import com.fiap.appointment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {
    List<MedicalHistory> findByPatient(User patient);
}
