package com.fiap.appointment.controller;

import com.fiap.appointment.dto.AppointmentDTO;
import com.fiap.appointment.dto.CreateAppointmentDTO;
import com.fiap.appointment.dto.UpdateAppointmentDTO;
import com.fiap.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE')")
    public ResponseEntity<AppointmentDTO> createAppointment(
            @Valid @RequestBody CreateAppointmentDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(appointmentService.createAppointment(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE')")
    public ResponseEntity<AppointmentDTO> updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAppointmentDTO dto) {
        return ResponseEntity.ok(appointmentService.updateAppointment(id, dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE', 'PATIENT')")
    public ResponseEntity<AppointmentDTO> getAppointment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(appointmentService.getAppointment(id, principal));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE', 'PATIENT')")
    public ResponseEntity<List<AppointmentDTO>> getPatientAppointments(
            @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(appointmentService.getPatientAppointments(patientId, principal));
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE')")
    public ResponseEntity<List<AppointmentDTO>> getDoctorAppointments(
            @PathVariable Long doctorId) {
        return ResponseEntity.ok(appointmentService.getDoctorAppointments(doctorId));
    }

    @GetMapping("/patient/{patientId}/future")
    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE', 'PATIENT')")
    public ResponseEntity<List<AppointmentDTO>> getPatientFutureAppointments(
            @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(appointmentService.getPatientFutureAppointments(patientId, principal));
    }
}
