package org.tkit.onecx.search.config.rs.v1.controller;

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
import org.tkit.onecx.search.config.domain.criteria.SearchConfigCriteria;
import org.tkit.onecx.search.config.domain.daos.SearchConfigDAO;
import org.tkit.onecx.search.config.domain.models.SearchConfig;
import org.tkit.onecx.search.config.rs.v1.mapper.ExceptionMapper;
import org.tkit.onecx.search.config.rs.v1.mapper.SearchConfigMapper;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;

import gen.org.tkit.onecx.search.config.v1.SearchConfigV1Api;
import gen.org.tkit.onecx.search.config.v1.model.*;
import lombok.extern.slf4j.Slf4j;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class SearchConfigControllerV1 implements SearchConfigV1Api {

    @Inject
    SearchConfigMapper searchConfigMapper;
    @Inject
    SearchConfigDAO searchConfigDAO;

    @Inject
    ExceptionMapper exceptionMapper;

    @Context
    UriInfo uriInfo;

    @Override
    public Response createSearchConfig(CreateSearchConfigRequestDTOV1 createSearchConfigRequestDTOV1) {
        SearchConfig searchConfig = searchConfigMapper.create(createSearchConfigRequestDTOV1);
        SearchConfig responseSearchConfig = searchConfigDAO.create(searchConfig);
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path(responseSearchConfig.getId()).build())
                .entity(searchConfigMapper.map(responseSearchConfig))
                .build();
    }

    @Override
    public Response deleteSearchConfig(String configId) {
        SearchConfig searchConfig = searchConfigDAO.findById(configId);
        if (searchConfig != null) {
            searchConfigDAO.deleteQueryById(configId);
            return Response.noContent().build();
        }
        return Response.status(NOT_FOUND).build();
    }

    @Override
    public Response findBySearchCriteria(SearchConfigSearchRequestDTOV1 searchConfigSearchRequestDTOV1) {
        var results = searchConfigDAO.findBySearchCriteria(searchConfigMapper.map(searchConfigSearchRequestDTOV1));
        return Response.ok().entity(searchConfigMapper.map(results)).build();
    }

    @Override
    public Response getSearchConfigs(String productName, String appId, String page) {
        SearchConfigCriteria scc = new SearchConfigCriteria();
        scc.setPage(page);
        scc.setAppId(appId);
        scc.setProductName(productName);
        scc.setPageNumber(0);
        scc.setPageSize(100);
        var results = searchConfigDAO.findBySearchCriteria(scc);
        return Response.ok().entity(searchConfigMapper.map(results)).build();
    }

    @Override
    public Response updateSearchConfig(String configId,
            UpdateSearchConfigRequestDTOV1 updateSearchConfigRequestDTOV1) {
        SearchConfig searchConfig = searchConfigDAO.findById(configId);
        if (searchConfig != null) {
            searchConfigMapper.update(searchConfig, updateSearchConfigRequestDTOV1);
            SearchConfig updatedSearchConfig = searchConfigDAO
                    .update(searchConfig);
            return Response.ok(searchConfigMapper.map(updatedSearchConfig)).build();
        }
        return Response.status(NOT_FOUND).build();
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
