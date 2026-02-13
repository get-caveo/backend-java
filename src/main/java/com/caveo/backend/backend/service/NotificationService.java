package com.caveo.backend.backend.service;

import com.caveo.backend.backend.dao.NotificationDao;
import com.caveo.backend.backend.dao.UtilisateurDao;
import com.caveo.backend.backend.dto.NotificationEvent;
import com.caveo.backend.backend.exception.GestionException;
import com.caveo.backend.backend.model.Notification;
import com.caveo.backend.backend.model.Utilisateur;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationDao notificationDao;
    private final UtilisateurDao utilisateurDao;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Listener qui capte les NotificationEvent publiés via ApplicationEventPublisher.
     * Persiste la notification en base et la pousse via WebSocket au backoffice.
     */
    @EventListener
    @Transactional
    public void onNotificationEvent(NotificationEvent event) {
        Notification notification = new Notification();
        notification.setType(event.getType());
        notification.setTitre(event.getTitre());
        notification.setMessage(event.getMessage());
        notification.setTypeReference(event.getTypeReference());
        notification.setReferenceId(event.getReferenceId());
        notification.setLu(false);

        if (event.getUtilisateurId() != null) {
            Utilisateur utilisateur = utilisateurDao.findById(event.getUtilisateurId()).orElse(null);
            notification.setUtilisateur(utilisateur);
        }

        Notification saved = notificationDao.save(notification);

        // Push via WebSocket vers le backoffice
        messagingTemplate.convertAndSend("/topic/notifications", saved);

        log.info("Notification créée et envoyée: [{}] {}", event.getType(), event.getTitre());
    }

    public List<Notification> getNotifications(Integer utilisateurId) {
        return notificationDao.findByUtilisateurIdOrderByCreeLeDesc(utilisateurId);
    }

    public List<Notification> getNotificationsNonLues(Integer utilisateurId) {
        return notificationDao.findByUtilisateurIdAndLuFalseOrderByCreeLeDesc(utilisateurId);
    }

    public Long countNonLues(Integer utilisateurId) {
        return notificationDao.countUnreadByUtilisateur(utilisateurId);
    }

    @Transactional
    public Notification marquerCommeLue(Integer notificationId) {
        Notification notification = notificationDao.findById(notificationId)
                .orElseThrow(() -> GestionException.notFound("Notification", notificationId));
        notification.setLu(true);
        return notificationDao.save(notification);
    }

    @Transactional
    public void marquerToutesCommeLues(Integer utilisateurId) {
        List<Notification> nonLues = notificationDao
                .findByUtilisateurIdAndLuFalseOrderByCreeLeDesc(utilisateurId);
        nonLues.forEach(n -> n.setLu(true));
        notificationDao.saveAll(nonLues);
    }
}
