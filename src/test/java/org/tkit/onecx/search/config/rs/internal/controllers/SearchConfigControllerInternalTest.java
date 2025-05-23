package org.tkit.onecx.search.config.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.resteasy.reactive.RestResponse.Status.*;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.search.config.test.AbstractTest;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.search.config.rs.internal.model.*;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(SearchConfigControllerInternal.class)
@WithDBData(value = "search-config-data.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
@GenerateKeycloakClient(clientName = "testClient", scopes = "ocx-sc:all")
class SearchConfigControllerInternalTest extends AbstractTest {

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
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .get(configId)
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body().as(SearchConfigDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getAppId()).isEqualTo("support-tool-ui");
    }

    @Test
    void shouldNotGetSearchConfigsById() {
        String configId = "not-exists";

        given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .get(configId)
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldCreateSearchConfig() {
        String productName = "productName1";
        String application = "support-tool-ui";
        String name = "criteria-name";
        String page = "criteria-page";

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

        var searchConfigDTO = given()
                .when()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .body(requestBody)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body()
                .as(SearchConfigDTO.class);

        assertThat(searchConfigDTO.getId()).isNotNull();
        assertThat(requestBody.getAppId()).isEqualTo(searchConfigDTO.getAppId());
        assertThat(requestBody.getName()).isEqualTo(searchConfigDTO.getName());
        assertThat(requestBody.getPage()).isEqualTo(searchConfigDTO.getPage());
        assertThat(requestBody.getColumns()).isEqualTo(searchConfigDTO.getColumns());
        assertThat(requestBody.getValues()).isEqualTo(searchConfigDTO.getValues());
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
                .auth().oauth2(getKeycloakClientToken("testClient"))
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
    void shouldNotCreateSearchConfig() {
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    void shouldUpdateModificationCount() {
        String searchConfigId = "1";

        String application = "support-tool-ui";
        String name = "criteria-name";
        String page = "criteria-page";

        UpdateSearchConfigRequestDTO updateRequestBody = new UpdateSearchConfigRequestDTO();
        updateRequestBody.setAppId(application);
        updateRequestBody.setName(name);
        updateRequestBody.setPage(page);
        updateRequestBody.setModificationCount(0);

        var searchConfigDTO = given()
                .when()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .body(updateRequestBody)
                .put(searchConfigId)
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .as(SearchConfigDTO.class);

        assertThat(searchConfigDTO.getId()).isEqualTo(searchConfigId);
        assertThat(searchConfigDTO.getAppId()).isEqualTo(updateRequestBody.getAppId());
        assertThat(searchConfigDTO.getName()).isEqualTo(updateRequestBody.getName());
        assertThat(searchConfigDTO.getPage()).isEqualTo(updateRequestBody.getPage());
        assertThat(searchConfigDTO.getModificationCount()).isEqualTo(1);
    }

    @Test
    void shouldNotUpdateSearchConfigWhenBadRequest() {
        String configId = "1";

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
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

        UpdateSearchConfigRequestDTO updateRequestBody = new UpdateSearchConfigRequestDTO();
        updateRequestBody.setAppId(application);
        updateRequestBody.setName(name);
        updateRequestBody.setPage(page);
        updateRequestBody.setModificationCount(0);

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .body(updateRequestBody)
                .put(searchConfigId)
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldNotUpdateSearchConfigOptLock() {
        String searchConfigId = "1";

        String application = "support-tool-ui";
        String name = "criteria-name";
        String page = "criteria-page";

        UpdateSearchConfigRequestDTO updateRequestBody = new UpdateSearchConfigRequestDTO();
        updateRequestBody.setAppId(application);
        updateRequestBody.setName(name);
        updateRequestBody.setPage(page);
        updateRequestBody.setModificationCount(0);

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .body(updateRequestBody)
                .put(searchConfigId)
                .then()
                .statusCode(OK.getStatusCode());

        var error = given()
                .when()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .body(updateRequestBody)
                .put(searchConfigId)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(error.getErrorCode()).isEqualTo("OPTIMISTIC_LOCK");
    }

    @Test
    void shouldDeleteById() {
        String configId = "1";

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .delete(configId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

    }

    @Test
    void shouldFindByCriteria() {
        String application = "support-tool-ui";
        SearchConfigSearchRequestDTO requestBody = new SearchConfigSearchRequestDTO();
        requestBody.setAppId(application);

        String[] expectedIds = { "1", "2", "3", "4" };

        var responseDTO = given()
                .when()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .body(requestBody)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .as(SearchConfigPageResultDTO.class);

        List<SearchConfigResultDTO> configs = responseDTO.getStream();
        assertThat(responseDTO.getTotalElements()).isEqualTo(4);

        List<String> searchConfigApplications = configs.stream()
                .map(SearchConfigResultDTO::getAppId)
                .toList();
        assertThat(searchConfigApplications).contains(application);

        List<String> configIds = configs.stream()
                .map(SearchConfigResultDTO::getId)
                .toList();
        assertThat(configIds).containsAll(Arrays.asList(expectedIds));
    }

    @Test
    void shouldFindByCriteriaNoMatch() {
        String application = "no-match-app";
        SearchConfigSearchRequestDTO requestBody = new SearchConfigSearchRequestDTO();
        requestBody.setAppId(application);

        var responseDTO = given()
                .when()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .body(requestBody)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .as(SearchConfigPageResultDTO.class);

        assertThat(responseDTO.getTotalElements()).isZero();
    }

    @Test
    void shouldFindAllByCriteriaEmpty() {
        SearchConfigSearchRequestDTO searchConfigSearchCriteria = new SearchConfigSearchRequestDTO();

        var responseDTO = given()
                .when()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .body(searchConfigSearchCriteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .as(SearchConfigPageResultDTO.class);

        assertThat(responseDTO.getTotalElements()).isEqualTo(7);
    }

    @Test
    void shouldNotFindByCriteriaNullCriteria() {
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    void loadByProductAppAndProduct_shouldReturnSearchConfigLoadResultArray() {
        SearchConfigLoadRequestDTO requestBody = new SearchConfigLoadRequestDTO();
        requestBody.setAppId("support-tool-ui");
        requestBody.setProductName("productName1");
        requestBody.setPage("page1");

        var responseDTO = given()
                .when()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .body(requestBody)
                .post("/load")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .as(List.class);

        assertThat(responseDTO.get(0)).isNotNull();
    }
}
