package com.fiap.notification.listener;

import com.fiap.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AppointmentMessageListenerTest {

    @Mock
    private NotificationService notificationService;

    @Test
    void shouldCreateNotificationWhenMessageIsValid() {
        AppointmentMessageListener listener = new AppointmentMessageListener(notificationService);
        String message = """
                {
                  "id": 12,
                  "patientId": 3,
                  "appointmentDate": "2027-03-15T10:00:00"
                }
                """;

        listener.handleAppointmentMessage(message);

        verify(notificationService).createNotification(
                3L,
                12L,
                "Lembrete: Você tem uma consulta marcada para 2027-03-15T10:00:00"
        );
    }

    @Test
    void shouldIgnoreInvalidPayloadWithoutThrowing() {
        AppointmentMessageListener listener = new AppointmentMessageListener(notificationService);

        assertDoesNotThrow(() -> listener.handleAppointmentMessage("{invalid json"));
        verify(notificationService, never()).createNotification(org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyString());
    }
}
