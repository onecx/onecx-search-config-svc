package org.tkit.onecx.search.config.rs.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.resteasy.reactive.RestResponse.Status.*;

import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.search.config.test.AbstractTest;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.search.config.rs.internal.model.CreateSearchConfigRequestDTO;
import gen.org.tkit.onecx.search.config.rs.internal.model.ProblemDetailResponseDTO;
import gen.org.tkit.onecx.search.config.rs.internal.model.SearchConfigDTO;
import gen.org.tkit.onecx.search.config.v1.model.*;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(SearchConfigControllerV1.class)
@WithDBData(value = "search-config-data.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class SearchConfigControllerV1Test extends AbstractTest {

    private Map<String, String> setupValues() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "name");
        values.put("surname", "surname");
        values.put("address", "address");
        return values;
    }

    private List<String> setupColumns() {
        List<String> columns = new ArrayList<>();
        columns.add("name");
        columns.add("surname");
        columns.add("address");
        return columns;
    }

    @Test
    void shouldGetSearchConfigsById() {
        String configId = "1";

        var dto = given()
                .contentType(APPLICATION_JSON)
                .get(configId)
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body().as(SearchConfigDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getAppId()).isEqualTo("support-tool-ui");
    }

    @Test
    void shouldCreateSearchConfig() {
        String productName = "productName1";
        String application = "support-tool-ui";
        String name = "criteria-name";
        String page = "criteria-page";

        CreateSearchConfigRequestDTOV1 newSearchConfig = new CreateSearchConfigRequestDTOV1();
        newSearchConfig.setProductName(productName);
        newSearchConfig.setAppId(application);
        newSearchConfig.setName(name);
        newSearchConfig.setPage(page);
        newSearchConfig.values(setupValues());
        newSearchConfig.setColumns(setupColumns());
        newSearchConfig.setFieldListVersion(1);
        newSearchConfig.setReadOnly(true);
        newSearchConfig.setAdvanced(false);

        var searchConfigDTOV1 = given()
                .contentType(APPLICATION_JSON)
                .body(newSearchConfig)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(CreateSearchConfigResponseDTOV1.class);

        assertThat(searchConfigDTOV1).isNotNull();
        assertThat(searchConfigDTOV1.getConfig()).isNotNull();
        assertThat(searchConfigDTOV1.getConfig().getConfigId()).isNotNull();
        assertThat(searchConfigDTOV1.getConfig().getAppId()).isEqualTo(newSearchConfig.getAppId());
        assertThat(searchConfigDTOV1.getConfig().getName()).isEqualTo(newSearchConfig.getName());
        assertThat(searchConfigDTOV1.getConfig().getPage()).isEqualTo(newSearchConfig.getPage());
        assertThat(searchConfigDTOV1.getConfig().getColumns()).isEqualTo(newSearchConfig.getColumns());
        assertThat(searchConfigDTOV1.getConfig().getValues()).isEqualTo(newSearchConfig.getValues());
    }

    @Test
    void shouldNotCreateSearchConfig() {
        given()
                .contentType(APPLICATION_JSON)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    void shouldNotCreateSearchConfigDuplicate() {
        String productName = "productName1";
        String application = "support-tool-ui";
        String name = "test";
        String page = "page1";

        CreateSearchConfigRequestDTO requestBody = new CreateSearchConfigRequestDTO();
        requestBody.setAppId(application);
        requestBody.setName(name);
        requestBody.setPage(page);
        requestBody.setProductName(productName);
        requestBody.values(setupValues());
        requestBody.setColumns(setupColumns());
        requestBody.setFieldListVersion(1);
        requestBody.setReadOnly(true);
        requestBody.setAdvanced(false);

        var error = given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(requestBody)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract()
                .body()
                .as(ProblemDetailResponseDTO.class);

        assertThat(error.getErrorCode()).isEqualTo("PERSIST_ENTITY_FAILED");
    }

    @Test
    void shouldUpdateModificationCount() {
        String searchConfigId = "1";

        String application = "support-tool-ui";
        String name = "criteria-name";
        String page = "criteria-page";

        UpdateSearchConfigRequestDTOV1 updateRequestBody = new UpdateSearchConfigRequestDTOV1();
        updateRequestBody.setProductName("productName1");
        updateRequestBody.setAppId(application);
        updateRequestBody.setName(name);
        updateRequestBody.setPage(page);
        updateRequestBody.setModificationCount(0);

        var searchConfigDTO = given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(updateRequestBody)
                .put(searchConfigId)
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .as(UpdateSearchConfigResponseDTOV1.class);

        assertThat(searchConfigDTO).isNotNull();
        assertThat(searchConfigDTO.getConfig()).isNotNull();
        assertThat(searchConfigDTO.getConfig().getConfigId()).isEqualTo(searchConfigId);
        assertThat(searchConfigDTO.getConfig().getAppId()).isEqualTo(updateRequestBody.getAppId());
        assertThat(searchConfigDTO.getConfig().getName()).isEqualTo(updateRequestBody.getName());
        assertThat(searchConfigDTO.getConfig().getPage()).isEqualTo(updateRequestBody.getPage());
        assertThat(searchConfigDTO.getConfig().getModificationCount()).isEqualTo(1);

    }

    @Test
    void shouldUpdateModificationCountOptLock() {
        String searchConfigId = "1";

        String application = "support-tool-ui";
        String name = "criteria-name";
        String page = "criteria-page";

        UpdateSearchConfigRequestDTOV1 updateRequestBody = new UpdateSearchConfigRequestDTOV1();
        updateRequestBody.setProductName("productName1");
        updateRequestBody.setAppId(application);
        updateRequestBody.setName(name);
        updateRequestBody.setPage(page);
        updateRequestBody.setModificationCount(0);

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(updateRequestBody)
                .put(searchConfigId)
                .then()
                .statusCode(OK.getStatusCode());

        var error = given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(updateRequestBody)
                .put(searchConfigId)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(error.getErrorCode()).isEqualTo("OPTIMISTIC_LOCK");

    }

    @Test
    void shouldNotUpdateSearchConfigWhenBadRequest() {
        String configId = "1";

        given()
                .contentType(APPLICATION_JSON)
                .put(configId)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    void shouldNotUpdateSearchConfigNotExists() {
        String searchConfigId = "NotExists";

        String application = "support-tool-ui";
        String name = "criteria-name";
        String page = "criteria-page";

        UpdateSearchConfigRequestDTOV1 updateRequestBody = new UpdateSearchConfigRequestDTOV1();
        updateRequestBody.setProductName("productName1");
        updateRequestBody.setAppId(application);
        updateRequestBody.setName(name);
        updateRequestBody.setPage(page);
        updateRequestBody.setModificationCount(1);

        given()
                .contentType(APPLICATION_JSON)
                .body(updateRequestBody)
                .put(searchConfigId)
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

    }

    @Test
    void shouldDeleteById() {
        String configId = "1";

        given()
                .contentType(APPLICATION_JSON)
                .delete(configId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void shouldNotDeleteWhenNotExists() {
        String configId = "NotExists";

        given()
                .contentType(APPLICATION_JSON)
                .delete(configId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void shouldFindByCriteria() {
        String application = "support-tool-ui";

        var requestBody = new SearchConfigSearchRequestDTOV1()
                .productName("productName1").appId(application).page("page2");

        String[] expectedIds = { "3", "4" };

        var responseDTO = given()
                .contentType(APPLICATION_JSON)
                .body(requestBody)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .as(SearchConfigSearchPageResultDTOV1.class);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getStream()).isNotNull().isNotEmpty().hasSize(2);
        List<SearchConfigSearchResultDTOV1> configs = responseDTO.getStream();

        List<String> configIds = configs.stream()
                .map(SearchConfigSearchResultDTOV1::getConfigId)
                .collect(Collectors.toList());
        assertThat(configIds).containsAll(Arrays.asList(expectedIds));
    }

    @Test
    void shouldFindByCriteriaPage() {
        String page = "page1";
        var requestBody = new SearchConfigSearchRequestDTOV1()
                .productName("productName1").appId("support-tool-ui").page(page);

        String[] expectedIds = { "test", "name1" };

        var responseDTO = given()
                .contentType(APPLICATION_JSON)
                .body(requestBody)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .as(SearchConfigSearchPageResultDTOV1.class);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getStream()).isNotNull().isNotEmpty().hasSize(2);
        List<SearchConfigSearchResultDTOV1> configs = responseDTO.getStream();

        List<String> configIds = configs.stream()
                .map(SearchConfigSearchResultDTOV1::getName)
                .collect(Collectors.toList());
        assertThat(configIds).containsAll(Arrays.asList(expectedIds));
    }

    @Test
    void shouldFindOneResultByCriteria() {
        String application = "support-tool-ui";
        String page = "page1";
        String name = "name1";
        SearchConfigSearchRequestDTOV1 requestBody = new SearchConfigSearchRequestDTOV1();
        requestBody.setProductName("productName1");
        requestBody.setAppId(application);
        requestBody.setPage(page);

        var responseDTO = given()
                .contentType(APPLICATION_JSON)
                .body(requestBody)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .as(SearchConfigSearchPageResultDTOV1.class);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getStream()).isNotNull().isNotEmpty().hasSize(2);
        List<SearchConfigSearchResultDTOV1> configs = responseDTO.getStream();

        assertThat(configs.stream().map(SearchConfigSearchResultDTOV1::getConfigId).toList()).contains("2", "1");
    }

    @Test
    void shouldFindByCriteriaNoMatch() {
        String application = "no-match-app";
        var requestBody = new SearchConfigSearchRequestDTOV1()
                .productName("productName1").appId(application).page("page1");

        var responseDTO = given()
                .contentType(APPLICATION_JSON)
                .body(requestBody)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .as(SearchConfigSearchPageResultDTOV1.class);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getStream()).isNotNull().isEmpty();
    }

    @Test
    void shouldFindAllByCriteriaEmpty() {
        SearchConfigSearchRequestDTOV1 searchConfigSearchCriteria = new SearchConfigSearchRequestDTOV1();

        given()
                .contentType(APPLICATION_JSON)
                .body(searchConfigSearchCriteria)
                .post("/search")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    void shouldNotFindByCriteriaNullCriteria() {
        var response = given()
                .contentType(APPLICATION_JSON)
                .post("/search")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }
}
