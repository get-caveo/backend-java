package com.caveo.backend.backend.controller;

import com.caveo.backend.backend.model.Notification;
import com.caveo.backend.backend.security.AppUserDetails;
import com.caveo.backend.backend.security.IsEmploye;
import com.caveo.backend.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@IsEmploye
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(
            @AuthenticationPrincipal AppUserDetails user) {
        return ResponseEntity.ok(notificationService.getNotifications(user.getUtilisateur().getId()));
    }

    @GetMapping("/non-lues")
    public ResponseEntity<List<Notification>> getNonLues(
            @AuthenticationPrincipal AppUserDetails user) {
        return ResponseEntity.ok(notificationService.getNotificationsNonLues(user.getUtilisateur().getId()));
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> countNonLues(
            @AuthenticationPrincipal AppUserDetails user) {
        Long count = notificationService.countNonLues(user.getUtilisateur().getId());
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PutMapping("/{id}/lire")
    public ResponseEntity<Notification> marquerCommeLue(@PathVariable Integer id) {
        return ResponseEntity.ok(notificationService.marquerCommeLue(id));
    }

    @PutMapping("/lire-tout")
    public ResponseEntity<Void> marquerToutesCommeLues(
            @AuthenticationPrincipal AppUserDetails user) {
        notificationService.marquerToutesCommeLues(user.getUtilisateur().getId());
        return ResponseEntity.noContent().build();
    }
}
