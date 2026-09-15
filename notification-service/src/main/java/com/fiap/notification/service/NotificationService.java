package com.fiap.notification.service;

import com.fiap.notification.entity.Notification;
import com.fiap.notification.entity.NotificationStatus;
import com.fiap.notification.entity.User;
import com.fiap.notification.repository.NotificationRepository;
import com.fiap.notification.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public Notification createNotification(Long patientId, Long appointmentId, String message) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Notification notification = new Notification();
        notification.setPatient(patient);
        notification.setAppointmentId(appointmentId);
        notification.setMessage(message);
        notification.setStatus(NotificationStatus.PENDING);

        return notificationRepository.save(notification);
    }

    public Notification sendNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        try {
            System.out.println("[NOTIFICATION SEND] Enviando notificação ID: " + notificationId);
            System.out.println("[NOTIFICATION SEND] Para o paciente: " + notification.getPatient().getFullName());
            System.out.println("[NOTIFICATION SEND] Email: " + notification.getPatient().getEmail());
            System.out.println("[NOTIFICATION SEND] Mensagem: " + notification.getMessage());
            
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            
            System.out.println("[NOTIFICATION SEND] ✓ Notificação enviada/registrada com sucesso às " + notification.getSentAt());
            return notificationRepository.save(notification);
        } catch (Exception e) {
            System.err.println("[NOTIFICATION SEND] ✗ Erro ao enviar notificação: " + e.getMessage());
            notification.setStatus(NotificationStatus.FAILED);
            return notificationRepository.save(notification);
        }
    }

    public List<Notification> getPatientNotifications(Long patientId) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        return notificationRepository.findByPatient(patient);
    }

    public List<Notification> getPatientPendingNotifications(Long patientId) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        return notificationRepository.findByPatientAndStatus(patient, NotificationStatus.PENDING);
    }

    public List<Notification> getAppointmentNotifications(Long appointmentId) {
        return notificationRepository.findByAppointmentId(appointmentId);
    }
}
