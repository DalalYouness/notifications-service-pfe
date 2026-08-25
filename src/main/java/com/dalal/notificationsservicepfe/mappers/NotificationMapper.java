package com.dalal.notificationsservicepfe.mappers;


import com.dalal.notificationsservicepfe.dtos.response.NotificationResponse;
import com.dalal.notificationsservicepfe.entities.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(source = "read", target = "isRead")
    NotificationResponse toResponseDTO(Notification notification);
}