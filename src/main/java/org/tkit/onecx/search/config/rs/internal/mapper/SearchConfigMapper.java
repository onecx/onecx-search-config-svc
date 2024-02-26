package org.tkit.onecx.search.config.rs.internal.mapper;

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
public abstract class SearchConfigMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "apiVersion", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "readOnly", source = "isReadOnly")
    @Mapping(target = "advanced", source = "isAdvanced")
    public abstract SearchConfig create(CreateSearchConfigRequestDTO dto);

    public String map(Map<String, String> values) throws JsonProcessingException {
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
    @Mapping(target = "apiVersion", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "readOnly", source = "isReadOnly")
    @Mapping(target = "advanced", source = "isAdvanced")
    public abstract SearchConfig update(@MappingTarget SearchConfig searchConfig, UpdateSearchConfigRequestDTO dto);

    public String map(List<String> value) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(value);
    }

    public List<String> map(String value) throws JsonProcessingException {
        List<String> columns;
        ObjectMapper mapper = new ObjectMapper();
        columns = mapper.readValue(value, List.class);
        return columns;
    }

    public Map<String, String> mapValues(String value) throws JsonProcessingException {
        Map<String, String> values;

        ObjectMapper mapper = new ObjectMapper();

        values = mapper.readValue(value, HashMap.class);
        return values;
    }

    public abstract SearchConfigCriteria map(SearchConfigSearchRequestDTO configSearchRequestDTO);

    @Mapping(target = "removeStreamItem", ignore = true)
    public abstract SearchPageResultDTO map(PageResult<SearchConfig> page);

    @Mapping(target = "isReadOnly", source = "readOnly")
    @Mapping(target = "isAdvanced", source = "advanced")
    @Mapping(target = "removeColumnsItem", source = "advanced")
    @Mapping(target = "removeValuesItem", source = "advanced")
    public abstract SearchConfigDTO map(SearchConfig searchConfig);

}
