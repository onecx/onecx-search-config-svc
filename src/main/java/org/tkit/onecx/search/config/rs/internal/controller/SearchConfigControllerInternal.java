package org.tkit.onecx.search.config.rs.internal.controller;

import static org.jboss.resteasy.reactive.RestResponse.StatusCode.NOT_FOUND;

import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.search.config.domain.dao.SearchConfigDAO;
import org.tkit.onecx.search.config.domain.models.SearchConfig;
import org.tkit.onecx.search.config.rs.internal.mapper.ExceptionMapper;
import org.tkit.onecx.search.config.rs.internal.mapper.SearchConfigMapper;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;

import gen.org.tkit.onecx.search.config.rs.internal.SearchConfigInternalApi;
import gen.org.tkit.onecx.search.config.rs.internal.model.*;
import lombok.extern.slf4j.Slf4j;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class SearchConfigControllerInternal implements SearchConfigInternalApi {

    @Inject
    SearchConfigMapper searchConfigMapper;

    @Inject
    ExceptionMapper exceptionMapper;

    @Inject
    SearchConfigDAO searchConfigDAO;

    @Context
    UriInfo uriInfo;

    @Override
    public Response createSearchConfig(CreateSearchConfigRequestDTO createSearchConfigRequestDTO) {
        var searchConfig = searchConfigMapper.create(createSearchConfigRequestDTO);
        var responseSearchConfig = searchConfigDAO.create(searchConfig);
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path(responseSearchConfig.getId()).build())
                .entity(searchConfigMapper.map(responseSearchConfig))
                .build();
    }

    @Override
    public Response deleteSearchConfig(String configId) {
        var searchConfig = searchConfigDAO.findById(configId);
        if (searchConfig != null) {
            searchConfigDAO.deleteQueryById(configId);
            return Response.noContent().build();
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
                    .update(searchConfigMapper.update(searchConfig, updateSearchConfigRequestDTO));
            return Response.ok(searchConfigMapper.map(updatedSearchConfig)).build();
        }
        return Response.status(NOT_FOUND).build();
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> exception(ConstraintException ex) {
        return exceptionMapper.exception(ex);
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> daoException(OptimisticLockException ex) {
        return exceptionMapper.optimisticLock(ex);
    }

}
