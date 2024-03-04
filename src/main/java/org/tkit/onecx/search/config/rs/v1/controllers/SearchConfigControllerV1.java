package org.tkit.onecx.search.config.rs.v1.controllers;

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
import org.tkit.onecx.search.config.rs.v1.mappers.ExceptionMapper;
import org.tkit.onecx.search.config.rs.v1.mappers.SearchConfigMapper;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;

import gen.org.tkit.onecx.search.config.v1.SearchConfigV1Api;
import gen.org.tkit.onecx.search.config.v1.model.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SearchConfigControllerV1 implements SearchConfigV1Api {

    @Inject
    SearchConfigMapper mapper;
    @Inject
    SearchConfigDAO dao;

    @Inject
    ExceptionMapper exceptionMapper;

    @Context
    UriInfo uriInfo;

    @Override
    public Response createSearchConfig(CreateSearchConfigRequestDTOV1 createSearchConfigRequestDTOV1) {
        var searchConfig = mapper.create(createSearchConfigRequestDTOV1);
        var responseSearchConfig = dao.create(searchConfig);
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path(responseSearchConfig.getConfigId()).build())
                .entity(mapper.mapCreate(responseSearchConfig))
                .build();
    }

    @Override
    public Response deleteSearchConfig(String configId) {
        var data = dao.findByConfigId(configId);
        if (data != null) {
            dao.deleteQueryById(configId);
        }
        return Response.noContent().build();
    }

    @Override
    public Response findSearchConfigsBySearchCriteria(SearchConfigSearchRequestDTOV1 searchConfigSearchRequestDTOV1) {
        var results = dao.findBySearchCriteria(mapper.map(searchConfigSearchRequestDTOV1));
        return Response.ok().entity(mapper.map(results)).build();
    }

    @Override
    public Response updateSearchConfig(String configId, UpdateSearchConfigRequestDTOV1 updateSearchConfigRequestDTOV1) {
        var searchConfig = dao.findById(configId);
        if (searchConfig == null) {
            return Response.status(NOT_FOUND).build();
        }

        mapper.update(searchConfig, updateSearchConfigRequestDTOV1);
        var updatedSearchConfig = dao.update(searchConfig);
        return Response.ok(mapper.mapUpdate(updatedSearchConfig)).build();
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTOV1> exception(ConstraintException ex) {
        return exceptionMapper.exception(ex);
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTOV1> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTOV1> daoException(OptimisticLockException ex) {
        return exceptionMapper.optimisticLock(ex);
    }

}
