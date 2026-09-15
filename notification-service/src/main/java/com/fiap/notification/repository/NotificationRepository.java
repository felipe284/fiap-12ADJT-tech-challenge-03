package com.fiap.notification.repository;

import com.fiap.notification.entity.Notification;
import com.fiap.notification.entity.NotificationStatus;
import com.fiap.notification.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByPatient(User patient);
    List<Notification> findByPatientAndStatus(User patient, NotificationStatus status);
    List<Notification> findByAppointmentId(Long appointmentId);
}
