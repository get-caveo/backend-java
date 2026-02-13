package com.caveo.backend.backend.dao;

import com.caveo.backend.backend.model.Notification;
import com.caveo.backend.backend.model.TypeNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationDao extends JpaRepository<Notification, Integer> {

    List<Notification> findByUtilisateurIdOrderByCreeLeDesc(Integer utilisateurId);

    List<Notification> findByUtilisateurIdAndLuFalseOrderByCreeLeDesc(Integer utilisateurId);

    List<Notification> findByTypeOrderByCreeLeDesc(TypeNotification type);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.utilisateur.id = :userId AND n.lu = false")
    Long countUnreadByUtilisateur(@Param("userId") Integer userId);
}
