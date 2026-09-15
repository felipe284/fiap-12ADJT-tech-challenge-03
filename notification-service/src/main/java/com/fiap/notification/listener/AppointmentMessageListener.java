package com.fiap.notification.listener;

import com.fiap.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;

@Component
@RequiredArgsConstructor
public class AppointmentMessageListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = "appointment-queue")
    public void handleAppointmentMessage(@Payload String message) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            JsonNode json = mapper.readTree(message);

            Long patientId = json.get("patientId").asLong();
            Long appointmentId = json.get("id").asLong();
            String appointmentDate = json.get("appointmentDate").asText();

            String notificationMessage = String.format(
                "Lembrete: Você tem uma consulta marcada para %s",
                appointmentDate
            );

            notificationService.createNotification(patientId, appointmentId, notificationMessage);
            System.out.println("[NOTIFICATION] ✓ Notificação criada com sucesso");
            System.out.println("[NOTIFICATION] Paciente ID: " + patientId);
            System.out.println("[NOTIFICATION] Appointment ID: " + appointmentId);
            System.out.println("[NOTIFICATION] Data: " + appointmentDate);
            System.out.println("[NOTIFICATION] Mensagem: " + notificationMessage);

        } catch (Exception e) {
            System.err.println("[NOTIFICATION] ✗ Erro ao processar mensagem: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
