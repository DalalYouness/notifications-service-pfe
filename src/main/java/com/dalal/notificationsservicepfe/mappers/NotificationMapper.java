package com.dalal.notificationsservicepfe.mappers;

import com.dalal.notificationsservicepfe.dtos.event.NotificationEvent;
import com.dalal.notificationsservicepfe.dtos.response.NotificationResponse;
import com.dalal.notificationsservicepfe.entities.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "notificationType", source = "type")
    Notification toEntity(NotificationEvent event);

    NotificationResponse toResponseDTO(Notification notification);
}