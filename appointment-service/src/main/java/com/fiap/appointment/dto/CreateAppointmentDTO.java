package com.fiap.appointment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAppointmentDTO {
    private Long doctorId;
    private Long patientId;
    private LocalDateTime appointmentDate;
    private String notes;
}
