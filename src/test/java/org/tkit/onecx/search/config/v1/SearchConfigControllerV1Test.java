package org.tkit.onecx.search.config.v1;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.resteasy.reactive.RestResponse.Status.*;

import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.search.config.rs.v1.controller.SearchConfigControllerV1;
import org.tkit.onecx.search.config.test.AbstractTest;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.search.config.rs.internal.model.CreateSearchConfigRequestDTO;
import gen.org.tkit.onecx.search.config.rs.internal.model.ProblemDetailResponseDTO;
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
    void shouldGetSearchConfigsByPage() {

        String productName = "productName1";
        String application = "support-tool-ui";
        String page = "page1";

        var responseDTO = given()
                .contentType(APPLICATION_JSON)
                .get("/" + productName + "/" + application + "/" + page)
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body().as(SearchPageResultDTOV1.class);

        assertThat(responseDTO.getTotalElements()).isEqualTo(2);
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
                .body().as(SearchConfigDTOV1.class);

        assertThat(searchConfigDTOV1.getId()).isNotNull();
        assertThat(newSearchConfig.getAppId()).isEqualTo(searchConfigDTOV1.getAppId());
        assertThat(newSearchConfig.getName()).isEqualTo(searchConfigDTOV1.getName());
        assertThat(newSearchConfig.getPage()).isEqualTo(searchConfigDTOV1.getPage());
        assertThat(newSearchConfig.getColumns()).isEqualTo(searchConfigDTOV1.getColumns());
        assertThat(newSearchConfig.getValues()).isEqualTo(searchConfigDTOV1.getValues());
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
                .as(SearchConfigDTOV1.class);

        assertThat(searchConfigDTO.getId()).isEqualTo(searchConfigId);
        assertThat(searchConfigDTO.getAppId()).isEqualTo(updateRequestBody.getAppId());
        assertThat(searchConfigDTO.getName()).isEqualTo(updateRequestBody.getName());
        assertThat(searchConfigDTO.getPage()).isEqualTo(updateRequestBody.getPage());
        assertThat(searchConfigDTO.getModificationCount()).isEqualTo(1);

    }

    @Test
    void shouldUpdateModificationCountOptLock() {
        String searchConfigId = "1";

        String application = "support-tool-ui";
        String name = "criteria-name";
        String page = "criteria-page";

        UpdateSearchConfigRequestDTOV1 updateRequestBody = new UpdateSearchConfigRequestDTOV1();
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

        var response = given()
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
        SearchConfigSearchRequestDTOV1 requestBody = new SearchConfigSearchRequestDTOV1();
        requestBody.setAppId(application);

        String[] expectedIds = { "1", "2", "3", "4" };

        var responseDTO = given()
                .contentType(APPLICATION_JSON)
                .body(requestBody)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .as(SearchPageResultDTOV1.class);

        List<SearchConfigDTOV1> configs = responseDTO.getStream();

        assertThat(configs.size()).isSameAs(4);

        List<String> searchConfigApplications = configs.stream()
                .map(SearchConfigDTOV1::getAppId)
                .collect(Collectors.toList());
        assertThat(searchConfigApplications).contains(application);

        List<String> configIds = configs.stream()
                .map(SearchConfigDTOV1::getId)
                .collect(Collectors.toList());
        assertThat(configIds).containsAll(Arrays.asList(expectedIds));
    }

    @Test
    void shouldFindByCriteriaPage() {
        String page = "page1";
        SearchConfigSearchRequestDTOV1 requestBody = new SearchConfigSearchRequestDTOV1();
        requestBody.setPage(page);

        String[] expectedIds = { "1", "2" };

        var responseDTO = given()
                .contentType(APPLICATION_JSON)
                .body(requestBody)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .as(SearchPageResultDTOV1.class);

        List<SearchConfigDTOV1> configs = responseDTO.getStream();

        assertThat(configs.size()).isSameAs(2);

        List<String> searchConfigApplications = configs.stream()
                .map(SearchConfigDTOV1::getPage)
                .collect(Collectors.toList());
        assertThat(searchConfigApplications).contains(page);

        List<String> configIds = configs.stream()
                .map(SearchConfigDTOV1::getId)
                .collect(Collectors.toList());
        assertThat(configIds).containsAll(Arrays.asList(expectedIds));
    }

    @Test
    void shouldFindOneResultByCriteria() {
        String application = "support-tool-ui";
        String page = "page1";
        String name = "name1";
        SearchConfigSearchRequestDTOV1 requestBody = new SearchConfigSearchRequestDTOV1();
        requestBody.setAppId(application);
        requestBody.setPage(page);
        requestBody.setName(name);

        var responseDTO = given()
                .contentType(APPLICATION_JSON)
                .body(requestBody)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .as(SearchPageResultDTOV1.class);

        List<SearchConfigDTOV1> configs = responseDTO.getStream();

        assertThat(configs.size()).isSameAs(1);

        SearchConfigDTOV1 searchConfigDTOV1 = configs.get(0);
        assertThat(searchConfigDTOV1.getId()).isEqualTo("2");
        assertThat(searchConfigDTOV1.getAppId()).isEqualTo(application);
        assertThat(searchConfigDTOV1.getName()).isEqualTo(name);
        assertThat(searchConfigDTOV1.getPage()).isEqualTo(page);
    }

    @Test
    void shouldFindByCriteriaNoMatch() {
        String application = "no-match-app";
        SearchConfigSearchRequestDTOV1 requestBody = new SearchConfigSearchRequestDTOV1();
        requestBody.setAppId(application);

        var responseDTO = given()
                .contentType(APPLICATION_JSON)
                .body(requestBody)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .as(SearchPageResultDTOV1.class);

        assertThat(responseDTO.getStream()).isEmpty();
    }

    @Test
    void shouldFindAllByCriteriaEmpty() {
        SearchConfigSearchRequestDTOV1 searchConfigSearchCriteria = new SearchConfigSearchRequestDTOV1();

        var responseDTO = given()
                .contentType(APPLICATION_JSON)
                .body(searchConfigSearchCriteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .as(SearchPageResultDTOV1.class);

        assertThat(responseDTO.getStream()).hasSize(7);
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
