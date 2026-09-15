package com.fiap.appointment.repository;

import com.fiap.appointment.entity.Appointment;
import com.fiap.appointment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatient(User patient);
    List<Appointment> findByDoctor(User doctor);
    List<Appointment> findByPatientAndAppointmentDateAfter(User patient, LocalDateTime date);
    List<Appointment> findByDoctorAndAppointmentDateAfter(User doctor, LocalDateTime date);
}
