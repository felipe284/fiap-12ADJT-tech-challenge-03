package com.fiap.appointment.dto;

import com.fiap.appointment.entity.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAppointmentDTO {
    private LocalDateTime appointmentDate;
    private AppointmentStatus status;
    private String notes;
}
