package com.sandwich.app.mapper;

import com.sandwich.app.domain.entity.DeliveryEntity;
import com.sandwich.app.models.model.delivery.DeliveryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface DeliveryMapper {

    DeliveryDto convert(DeliveryEntity entity);

    @Mapping(target = "id", ignore = true)
    DeliveryEntity convert(@MappingTarget DeliveryEntity entity, DeliveryDto dto);
}
