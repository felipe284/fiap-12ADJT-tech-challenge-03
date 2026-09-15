package com.fiap.notification.controller;

import com.fiap.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/{id}/send")
    public ResponseEntity<?> sendNotification(@PathVariable Long id) {
        var notification = notificationService.sendNotification(id);
        return ResponseEntity.ok(notification);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getPatientNotifications(@PathVariable Long patientId) {
        return ResponseEntity.ok(notificationService.getPatientNotifications(patientId));
    }
}
