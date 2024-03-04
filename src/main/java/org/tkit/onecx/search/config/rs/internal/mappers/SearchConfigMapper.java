package org.tkit.onecx.search.config.rs.internal.mappers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.tkit.onecx.search.config.domain.criteria.SearchConfigCriteria;
import org.tkit.onecx.search.config.domain.models.SearchConfig;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gen.org.tkit.onecx.search.config.rs.internal.model.*;

@Mapper(uses = OffsetDateTimeMapper.class)
public interface SearchConfigMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "configId", ignore = true)
    SearchConfig create(CreateSearchConfigRequestDTO dto);

    default String map(Map<String, String> values) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(values);

    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "configId", ignore = true)
    void update(@MappingTarget SearchConfig searchConfig, UpdateSearchConfigRequestDTO dto);

    default String map(List<String> value) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(value);
    }

    default List<String> map(String value) throws JsonProcessingException {
        List<String> columns;
        ObjectMapper mapper = new ObjectMapper();
        columns = mapper.readValue(value, List.class);
        return columns;
    }

    default Map<String, String> mapValues(String value) throws JsonProcessingException {
        Map<String, String> values;

        ObjectMapper mapper = new ObjectMapper();

        values = mapper.readValue(value, HashMap.class);
        return values;
    }

    SearchConfigCriteria map(SearchConfigSearchRequestDTO configSearchRequestDTO);

    @Mapping(target = "removeStreamItem", ignore = true)
    SearchConfigPageResultDTO map(PageResult<SearchConfig> page);

    @Mapping(target = "removeColumnsItem", ignore = true)
    @Mapping(target = "removeValuesItem", ignore = true)
    SearchConfigDTO map(SearchConfig searchConfig);

}
