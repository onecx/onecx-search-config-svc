package org.onecx.search.config.rs.internal.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.onecx.search.config.domain.models.SearchConfig;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gen.io.github.onecx.search.config.rs.internal.model.ProblemDetailResponseDTO;
import gen.io.github.onecx.search.config.rs.internal.model.SearchConfigDTO;
import gen.io.github.onecx.search.config.rs.internal.model.SearchConfigPageResultDTO;
import gen.io.github.onecx.search.config.rs.internal.model.SearchConfigSearchRequestDTO;

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
    @Mapping(target = "readOnly", source = "isReadOnly")
    @Mapping(target = "advanced", source = "isAdvanced")
    public abstract SearchConfig create(SearchConfigDTO dto);

    public String map(Map<String, String> values) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(values);

    }

    @Mapping(target = "isReadOnly", source = "readOnly")
    @Mapping(target = "isAdvanced", source = "advanced")
    @Mapping(target = "removeColumnsItem", source = "advanced")
    @Mapping(target = "removeValuesItem", source = "advanced")
    public abstract SearchConfigDTO map(SearchConfig searchConfig);

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
    @Mapping(target = "readOnly", source = "isReadOnly")
    @Mapping(target = "advanced", source = "isAdvanced")
    public abstract SearchConfig update(@MappingTarget SearchConfig searchConfig, SearchConfigDTO dto);

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

    @Mapping(target = "params", ignore = true)
    @Mapping(target = "removeParamsItem", ignore = true)
    @Mapping(target = "invalidParams", ignore = true)
    @Mapping(target = "removeInvalidParamsItem", ignore = true)
    public abstract ProblemDetailResponseDTO exception(String errorCode, String detail);

    @Mapping(target = "removeStreamItem", ignore = true)
    public abstract SearchConfigPageResultDTO mapToPageResult(PageResult<SearchConfig> results);

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
    @Mapping(target = "readOnly", ignore = true)
    @Mapping(target = "advanced", ignore = true)
    @Mapping(target = "fieldListVersion", ignore = true)
    @Mapping(target = "values", ignore = true)
    @Mapping(target = "columns", ignore = true)
    public abstract SearchConfig map(SearchConfigSearchRequestDTO configSearchRequestDTO);
}
