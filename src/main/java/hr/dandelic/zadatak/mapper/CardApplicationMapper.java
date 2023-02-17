package hr.dandelic.zadatak.mapper;

import hr.dandelic.zadatak.api.dto.request.CardApplicationCreateRequest;
import hr.dandelic.zadatak.api.dto.request.CardApplicationUpdateRequest;
import hr.dandelic.zadatak.api.dto.response.CardApplicationResponse;
import hr.dandelic.zadatak.persistence.model.CardApplication;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CardApplicationMapper {

    List<CardApplicationResponse> map(List<CardApplication> cardApplications);

    CardApplicationResponse map(CardApplication cardApplication);

    @Mapping(target = "status", ignore = true)
    CardApplication map(CardApplicationCreateRequest createRequest);

    @Mapping(target = "oib", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    void update(@MappingTarget CardApplication persisted, CardApplicationUpdateRequest updateRequest);
}
