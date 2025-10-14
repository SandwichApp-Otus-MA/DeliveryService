package com.sandwich.app.mapper;

import com.sandwich.app.domain.entity.CourierEntity;
import com.sandwich.app.models.model.delivery.courier.CourierDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface CourierMapper {

    CourierDto convert(CourierEntity entity);

    @Mapping(target = "id", ignore = true)
    CourierEntity convert(@MappingTarget CourierEntity entity, CourierDto dto);
}
