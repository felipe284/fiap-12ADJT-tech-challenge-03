package com.fiap.appointment.service;

import com.fiap.appointment.dto.CreateAppointmentDTO;
import com.fiap.appointment.entity.Appointment;
import com.fiap.appointment.entity.AppointmentStatus;
import com.fiap.appointment.entity.User;
import com.fiap.appointment.entity.UserRole;
import com.fiap.appointment.exception.BadRequestException;
import com.fiap.appointment.repository.AppointmentRepository;
import com.fiap.appointment.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void shouldCreateAppointmentAndPublishEvent() {
        User doctor = user(1L, "doctor1", UserRole.DOCTOR);
        User patient = user(3L, "patient1", UserRole.PATIENT);
        LocalDateTime date = LocalDateTime.now().plusDays(2);
        CreateAppointmentDTO dto = CreateAppointmentDTO.builder()
                .doctorId(1L)
                .patientId(3L)
                .appointmentDate(date)
                .notes("Consulta")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(userRepository.findById(3L)).thenReturn(Optional.of(patient));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment appointment = invocation.getArgument(0);
            appointment.setId(10L);
            return appointment;
        });

        var result = appointmentService.createAppointment(dto);

        assertEquals(10L, result.getId());
        assertEquals(doctor.getId(), result.getDoctorId());
        assertEquals(patient.getId(), result.getPatientId());
        assertEquals(AppointmentStatus.SCHEDULED, result.getStatus());
        verify(rabbitTemplate, times(1))
                .convertAndSend(eq("appointment-exchange"), eq("appointment.created"), any(com.fiap.appointment.dto.AppointmentDTO.class));
    }

    @Test
    void shouldRejectCreateAppointmentInPast() {
        User doctor = user(1L, "doctor1", UserRole.DOCTOR);
        User patient = user(3L, "patient1", UserRole.PATIENT);
        CreateAppointmentDTO dto = CreateAppointmentDTO.builder()
                .doctorId(1L)
                .patientId(3L)
                .appointmentDate(LocalDateTime.now().minusDays(1))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(userRepository.findById(3L)).thenReturn(Optional.of(patient));

        assertThrows(BadRequestException.class, () -> appointmentService.createAppointment(dto));
    }

    @Test
    void shouldBlockPatientFromAccessingAnotherPatientAppointment() {
        User otherPatient = user(4L, "patient2", UserRole.PATIENT);
        Appointment appointment = new Appointment();
        appointment.setId(99L);
        appointment.setDoctor(user(1L, "doctor1", UserRole.DOCTOR));
        appointment.setPatient(otherPatient);
        appointment.setAppointmentDate(LocalDateTime.now().plusDays(1));
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findById(99L)).thenReturn(Optional.of(appointment));
        when(userRepository.findByUsername("patient1")).thenReturn(Optional.of(user(3L, "patient1", UserRole.PATIENT)));

        UserDetails principal = org.springframework.security.core.userdetails.User
                .withUsername("patient1")
                .password("x")
                .authorities("ROLE_PATIENT")
                .build();

        assertThrows(AccessDeniedException.class, () -> appointmentService.getAppointment(99L, principal));
    }

    @Test
    void shouldAllowDoctorToFetchPatientAppointments() {
        User patient = user(3L, "patient1", UserRole.PATIENT);
        User doctor = user(1L, "doctor1", UserRole.DOCTOR);
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentDate(LocalDateTime.now().plusDays(1));
        appointment.setStatus(AppointmentStatus.CONFIRMED);

        when(userRepository.findById(3L)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findByPatient(patient)).thenReturn(List.of(appointment));

        UserDetails principal = org.springframework.security.core.userdetails.User
                .withUsername("doctor1")
                .password("x")
                .authorities("ROLE_DOCTOR")
                .build();

        var result = appointmentService.getPatientAppointments(3L, principal);

        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getId());
    }

    private User user(Long id, String username, UserRole role) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setRole(role);
        user.setFullName(username);
        user.setPassword("encoded");
        return user;
    }
}
