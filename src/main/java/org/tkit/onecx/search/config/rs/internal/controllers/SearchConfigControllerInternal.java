package org.tkit.onecx.search.config.rs.internal.controllers;

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
import org.tkit.onecx.search.config.domain.daos.SearchConfigDAO;
import org.tkit.onecx.search.config.rs.internal.mappers.ExceptionMapper;
import org.tkit.onecx.search.config.rs.internal.mappers.SearchConfigMapper;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;

import gen.org.tkit.onecx.search.config.rs.internal.SearchConfigInternalApi;
import gen.org.tkit.onecx.search.config.rs.internal.model.*;
import lombok.extern.slf4j.Slf4j;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class SearchConfigControllerInternal implements SearchConfigInternalApi {

    @Inject
    SearchConfigMapper mapper;

    @Inject
    ExceptionMapper exceptionMapper;

    @Inject
    SearchConfigDAO dao;

    @Context
    UriInfo uriInfo;

    @Override
    public Response createConfig(CreateSearchConfigRequestDTO createSearchConfigRequestDTO) {
        var searchConfig = mapper.create(createSearchConfigRequestDTO);
        var responseSearchConfig = dao.create(searchConfig);
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path(responseSearchConfig.getId()).build())
                .entity(mapper.map(responseSearchConfig))
                .build();
    }

    @Override
    public Response deleteConfig(String id) {
        dao.deleteQueryById(id);
        return Response.noContent().build();
    }

    @Override
    public Response findBySearchCriteria(SearchConfigSearchRequestDTO searchConfigSearchRequestDTO) {
        var results = dao.findBySearchCriteria(mapper.map(searchConfigSearchRequestDTO));
        return Response.ok().entity(mapper.map(results)).build();
    }

    @Override
    public Response loadByProductAppAndPage(SearchConfigLoadRequestDTO searchConfigLoadRequestDTO) {
        var results = dao.findByProductAppAndPage(mapper.map(searchConfigLoadRequestDTO));
        return Response.ok().entity(mapper.mapToSearchConfigLoadResultList(results)).build();
    }

    @Override
    public Response getConfigById(String id) {
        var searchConfig = dao.findById(id);
        if (searchConfig == null) {
            return Response.status(NOT_FOUND).build();
        }
        return Response.ok().entity(mapper.map(searchConfig)).build();
    }

    @Override
    public Response updateConfig(String configId,
            UpdateSearchConfigRequestDTO updateSearchConfigRequestDTO) {
        var searchConfig = dao.findById(configId);
        if (searchConfig == null) {
            return Response.status(NOT_FOUND).build();
        }

        mapper.update(searchConfig, updateSearchConfigRequestDTO);
        var updatedSearchConfig = dao.update(searchConfig);
        return Response.ok(mapper.map(updatedSearchConfig)).build();
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
