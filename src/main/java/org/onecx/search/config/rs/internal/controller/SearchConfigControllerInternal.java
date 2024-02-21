package org.onecx.search.config.rs.internal.controller;

import static org.jboss.resteasy.reactive.RestResponse.StatusCode.BAD_REQUEST;
import static org.jboss.resteasy.reactive.RestResponse.StatusCode.NOT_FOUND;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.onecx.search.config.domain.dao.SearchConfigDAO;
import org.onecx.search.config.domain.models.SearchConfig;
import org.onecx.search.config.rs.internal.mapper.SearchConfigMapper;
import org.onecx.search.config.rs.v1.controller.ErrorKeys;
import org.tkit.quarkus.jpa.exceptions.DAOException;

import gen.io.github.onecx.search.config.rs.internal.SearchConfigInternalApi;
import gen.io.github.onecx.search.config.rs.internal.model.CreateSearchConfigRequestDTO;
import gen.io.github.onecx.search.config.rs.internal.model.GetSearchConfigResponseDTO;
import gen.io.github.onecx.search.config.rs.internal.model.SearchConfigSearchRequestDTO;
import gen.io.github.onecx.search.config.rs.internal.model.UpdateSearchConfigRequestDTO;
import lombok.extern.slf4j.Slf4j;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
@Tag(name = "SearchConfigInternal")
public class SearchConfigControllerInternal implements SearchConfigInternalApi {

    @Inject
    SearchConfigMapper searchConfigMapper;
    @Inject
    SearchConfigDAO searchConfigDAO;

    @Context
    UriInfo uriInfo;

    @Override
    public Response createSearchConfig(CreateSearchConfigRequestDTO createSearchConfigRequestDTO) {
        var searchConfig = searchConfigMapper.create(createSearchConfigRequestDTO.getConfig());
        var responseSearchConfig = searchConfigDAO.create(searchConfig);
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path(responseSearchConfig.getId()).build())
                .entity(searchConfigMapper.map(responseSearchConfig))
                .build();
    }

    @Override
    public Response deleteSearchConfig(String configId) {
        try {
            var searchConfig = searchConfigDAO.findById(configId);
            if (searchConfig != null) {
                searchConfigDAO.deleteQueryById(configId);
                return Response.noContent().build();
            }
        } catch (DAOException e) {
            e.printStackTrace();
            return Response.status(BAD_REQUEST)
                    .entity(searchConfigMapper.exception(ErrorKeys.ERROR_DELETE_SEARCH_CONFIG.name(),
                            "Failed to delete SEARCH CONFIG. Error: " + e.getMessage()))
                    .build();
        }
        return Response.status(NOT_FOUND).build();
    }

    @Override
    public Response findBySearchCriteria(SearchConfigSearchRequestDTO searchConfigSearchRequestDTO) {
        var results = searchConfigDAO.findBySearchCriteria(searchConfigMapper.mapToV1(searchConfigSearchRequestDTO));
        if (results == null) {
            return Response.status(NOT_FOUND).build();
        }
        GetSearchConfigResponseDTO getSearchConfigResponse = new GetSearchConfigResponseDTO();
        getSearchConfigResponse.setConfigs(searchConfigMapper.mapList(results));
        return Response.ok().entity(getSearchConfigResponse).build();
    }

    @Override
    public Response getSearchConfigById(String configId) {
        var searchConfig = searchConfigDAO.findById(configId);
        if (searchConfig != null) {
            return Response.ok().entity(searchConfigMapper.map(searchConfig)).build();
        }
        return Response.status(NOT_FOUND).build();
    }

    @Override
    public Response updateSearchConfig(String configId,
            UpdateSearchConfigRequestDTO updateSearchConfigRequestDTO) {
        var searchConfig = searchConfigDAO.findById(configId);
        if (searchConfig != null) {
            SearchConfig updatedSearchConfig = searchConfigDAO
                    .update(searchConfigMapper.update(searchConfig, updateSearchConfigRequestDTO.getConfig()));
            return Response.ok(searchConfigMapper.map(updatedSearchConfig)).build();
        }
        return Response.status(NOT_FOUND).build();
    }

}
