package com.fiap.notification.service;

import com.fiap.notification.entity.Notification;
import com.fiap.notification.entity.NotificationStatus;
import com.fiap.notification.entity.User;
import com.fiap.notification.entity.UserRole;
import com.fiap.notification.repository.NotificationRepository;
import com.fiap.notification.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private NotificationService notificationService;

    @Test
    void shouldCreateNotificationWithPendingStatus() {
        User patient = user(3L, "patient1");
        when(userRepository.findById(3L)).thenReturn(Optional.of(patient));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            notification.setId(1L);
            return notification;
        });

        Notification notification = notificationService.createNotification(3L, 10L, "Lembrete");

        assertEquals(1L, notification.getId());
        assertEquals(NotificationStatus.PENDING, notification.getStatus());
        assertEquals(10L, notification.getAppointmentId());
        assertEquals(patient, notification.getPatient());
    }

    @Test
    void shouldSendNotificationAndMarkAsSent() {
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setPatient(user(3L, "patient1"));
        notification.setMessage("Lembrete");
        notification.setStatus(NotificationStatus.PENDING);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Notification result = notificationService.sendNotification(1L);

        assertEquals(NotificationStatus.SENT, result.getStatus());
        assertNotNull(result.getSentAt());
    }

    @Test
    void shouldMarkAsFailedWhenSaveThrowsDuringSend() {
        Notification notification = new Notification();
        notification.setId(7L);
        notification.setPatient(user(3L, "patient1"));
        notification.setMessage("Lembrete");
        notification.setStatus(NotificationStatus.PENDING);
        when(notificationRepository.findById(7L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class)))
                .thenThrow(new RuntimeException("db error"))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Notification result = notificationService.sendNotification(7L);

        assertEquals(NotificationStatus.FAILED, result.getStatus());
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    @Test
    void shouldThrowWhenNotificationNotFound() {
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> notificationService.sendNotification(99L));
    }

    private User user(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setFullName("Paciente");
        user.setEmail("patient@hospital.com");
        user.setRole(UserRole.PATIENT);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }
}
