package com.caveo.backend.backend.dto;

import com.caveo.backend.backend.model.TypeNotification;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationEvent {

    private final TypeNotification type;
    private final String titre;
    private final String message;
    private final String typeReference;
    private final Integer referenceId;
    private final Integer utilisateurId;
}
