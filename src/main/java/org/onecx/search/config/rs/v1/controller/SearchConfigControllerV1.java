package org.onecx.search.config.rs.v1.controller;

import static org.jboss.resteasy.reactive.RestResponse.StatusCode.BAD_REQUEST;
import static org.jboss.resteasy.reactive.RestResponse.StatusCode.NOT_FOUND;

import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.onecx.search.config.domain.dao.SearchConfigDAO;
import org.onecx.search.config.domain.models.SearchConfig;
import org.onecx.search.config.rs.v1.mapper.SearchConfigMapper;
import org.tkit.quarkus.jpa.exceptions.DAOException;

import gen.io.github.onecx.search.config.v1.SearchConfigV1Api;
import gen.io.github.onecx.search.config.v1.model.CreateSearchConfigRequestDTOV1;
import gen.io.github.onecx.search.config.v1.model.SearchConfigPageResultDTOV1;
import gen.io.github.onecx.search.config.v1.model.SearchConfigSearchRequestDTOV1;
import gen.io.github.onecx.search.config.v1.model.UpdateSearchConfigRequestDTOV1;
import lombok.extern.slf4j.Slf4j;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
@Tag(name = "AnnouncementV1")
public class SearchConfigControllerV1 implements SearchConfigV1Api {

    @Inject
    SearchConfigMapper searchConfigMapper;
    @Inject
    SearchConfigDAO searchConfigDAO;

    @Context
    UriInfo uriInfo;

    @Override
    public Response createSearchConfig(@NotNull CreateSearchConfigRequestDTOV1 createSearchConfigRequestDTOV1) {
        SearchConfig searchConfig = searchConfigMapper.create(createSearchConfigRequestDTOV1.getConfig());
        SearchConfig responseSearchConfig = searchConfigDAO.create(searchConfig);
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path(responseSearchConfig.getId()).build())
                .entity(searchConfigMapper.map(responseSearchConfig))
                .build();
    }

    @Override
    public Response deleteSearchConfig(String configId) {
        try {
            SearchConfig searchConfig = searchConfigDAO.findById(configId);
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
    public Response findBySearchCriteria(SearchConfigSearchRequestDTOV1 searchConfigSearchRequestDTOV1) {
        var results = searchConfigDAO.findBySearchCriteria(searchConfigSearchRequestDTOV1);
        if (results == null) {
            return Response.status(NOT_FOUND).build();
        }
        SearchConfigPageResultDTOV1 announcementPageResultDTOV1 = searchConfigMapper.mapToPageResult(results);
        return Response.ok().entity(announcementPageResultDTOV1).build();
    }

    @Override
    public Response getSearchConfigs(String page) {
        var results = searchConfigDAO.findByPage(page);
        if (results == null) {
            return Response.status(NOT_FOUND).build();
        }
        SearchConfigPageResultDTOV1 announcementPageResultDTOV1 = searchConfigMapper.mapToPageResult(results);
        return Response.ok().entity(announcementPageResultDTOV1).build();
    }

    @Override
    public Response updateSearchConfig(String configId,
            @NotNull UpdateSearchConfigRequestDTOV1 updateSearchConfigRequestDTOV1) {
        SearchConfig searchConfig = searchConfigDAO.findById(configId);
        if (searchConfig != null) {
            SearchConfig updatedSearchConfig = searchConfigDAO
                    .update(searchConfigMapper.update(searchConfig, updateSearchConfigRequestDTOV1.getConfig()));
            return Response.ok(searchConfigMapper.map(updatedSearchConfig)).build();
        }
        return Response.status(NOT_FOUND).build();
    }

}
