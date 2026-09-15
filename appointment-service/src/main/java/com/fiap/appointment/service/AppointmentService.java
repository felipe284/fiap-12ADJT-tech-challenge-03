package com.fiap.appointment.service;

import com.fiap.appointment.dto.AppointmentDTO;
import com.fiap.appointment.dto.CreateAppointmentDTO;
import com.fiap.appointment.dto.UpdateAppointmentDTO;
import com.fiap.appointment.entity.Appointment;
import com.fiap.appointment.entity.AppointmentStatus;
import com.fiap.appointment.entity.User;
import com.fiap.appointment.exception.ResourceNotFoundException;
import com.fiap.appointment.exception.BadRequestException;
import com.fiap.appointment.repository.AppointmentRepository;
import com.fiap.appointment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;

    public AppointmentDTO createAppointment(CreateAppointmentDTO dto) {
        if (dto.getAppointmentDate() == null) {
            throw new BadRequestException("Appointment date is required");
        }
        if (dto.getDoctorId() == null || dto.getPatientId() == null) {
            throw new BadRequestException("Doctor and Patient IDs are required");
        }
        
        User doctor = userRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + dto.getDoctorId()));
        User patient = userRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + dto.getPatientId()));

        if (dto.getAppointmentDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Appointment date must be in the future");
        }

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setNotes(dto.getNotes());
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        Appointment saved = appointmentRepository.save(appointment);

        rabbitTemplate.convertAndSend("appointment-exchange", "appointment.created",
                mapToDTO(saved));

        return mapToDTO(saved);
    }

    public AppointmentDTO updateAppointment(Long appointmentId, UpdateAppointmentDTO dto) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        if (dto.getAppointmentDate() != null) {
            if (dto.getAppointmentDate().isBefore(LocalDateTime.now())) {
                throw new BadRequestException("Appointment date must be in the future");
            }
            appointment.setAppointmentDate(dto.getAppointmentDate());
        }
        if (dto.getStatus() != null) {
            appointment.setStatus(dto.getStatus());
        }
        if (dto.getNotes() != null) {
            appointment.setNotes(dto.getNotes());
        }

        Appointment updated = appointmentRepository.save(appointment);

        rabbitTemplate.convertAndSend("appointment-exchange", "appointment.updated",
                mapToDTO(updated));

        return mapToDTO(updated);
    }

    public AppointmentDTO getAppointment(Long appointmentId, UserDetails principal) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));
        ensureAuthenticatedPatientOwns(principal, appointment.getPatient().getId());
        return mapToDTO(appointment);
    }

    public List<AppointmentDTO> getPatientAppointments(Long patientId, UserDetails principal) {
        ensureAuthenticatedPatientOwns(principal, patientId);
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));
        return appointmentRepository.findByPatient(patient)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getDoctorAppointments(Long doctorId) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + doctorId));
        return appointmentRepository.findByDoctor(doctor)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getPatientFutureAppointments(Long patientId, UserDetails principal) {
        ensureAuthenticatedPatientOwns(principal, patientId);
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));
        return appointmentRepository.findByPatient(patient)
                .stream()
                .filter(a -> a.getAppointmentDate().isAfter(LocalDateTime.now()))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

   private AppointmentDTO mapToDTO(Appointment appointment) {
        return AppointmentDTO.builder()
                .id(appointment.getId())
                .doctorId(appointment.getDoctor().getId())
                .doctorName(appointment.getDoctor().getFullName())
                .patientId(appointment.getPatient().getId())
                .patientName(appointment.getPatient().getFullName())
                .appointmentDate(appointment.getAppointmentDate())
                .status(appointment.getStatus())
                .notes(appointment.getNotes())
                .build();
    }

    private void ensureAuthenticatedPatientOwns(UserDetails principal, Long patientId) {
        if (principal == null) {
            throw new AccessDeniedException("Authentication required");
        }
        boolean isPatient = principal.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_PATIENT".equals(authority.getAuthority()));
        if (!isPatient) {
            return;
        }
        User authenticatedUser = userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
        if (!authenticatedUser.getId().equals(patientId)) {
            throw new AccessDeniedException("Patients can only access their own appointments");
        }
    }

}
