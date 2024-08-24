package org.tkit.onecx.search.config.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.resteasy.reactive.RestResponse.Status.*;
import static org.tkit.quarkus.security.test.SecurityTestUtils.getKeycloakClientToken;

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
class SearchConfigControllerInternalTenantTest extends AbstractTest {

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
                .header(APM_HEADER_PARAM, createToken("org1", null))
                .get(configId)
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body().as(SearchConfigDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getAppId()).isEqualTo("support-tool-ui");

        given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .header(APM_HEADER_PARAM, createToken("org2", null))
                .get(configId)
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldCreateSearchConfig() {
        String productName = "productTest1";
        String application = "test-tool-ui";
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
                .header(APM_HEADER_PARAM, createToken("org2", null))
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

        given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .header(APM_HEADER_PARAM, createToken("org1", null))
                .get(searchConfigDTO.getId())
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        var dto = given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .header(APM_HEADER_PARAM, createToken("org2", null))
                .get(searchConfigDTO.getId())
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body().as(SearchConfigDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getAppId()).isEqualTo(requestBody.getAppId());
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

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .header(APM_HEADER_PARAM, createToken("org2", null))
                .body(updateRequestBody)
                .put(searchConfigId)
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        var searchConfigDTO = given()
                .when()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .header(APM_HEADER_PARAM, createToken("org1", null))
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
    void shouldDeleteById() {
        String configId = "1";

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .header(APM_HEADER_PARAM, createToken("org2", null))
                .delete(configId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .header(APM_HEADER_PARAM, createToken("org1", null))
                .get(configId)
                .then()
                .statusCode(OK.getStatusCode());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .header(APM_HEADER_PARAM, createToken("org1", null))
                .delete(configId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .header(APM_HEADER_PARAM, createToken("org1", null))
                .get(configId)
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

    }

    @Test
    void shouldFindByCriteria() {
        String application = "task-list-ui";
        SearchConfigSearchRequestDTO requestBody = new SearchConfigSearchRequestDTO();
        requestBody.setAppId(application);

        String[] expectedIds = { "8" };

        var responseDTO = given()
                .when()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .header(APM_HEADER_PARAM, createToken("org2", null))
                .body(requestBody)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .as(SearchConfigPageResultDTO.class);

        assertThat(responseDTO).isNotNull();
        List<SearchConfigResultDTO> configs = responseDTO.getStream();
        assertThat(responseDTO.getTotalElements()).isEqualTo(1);

        List<String> searchConfigApplications = configs.stream()
                .map(SearchConfigResultDTO::getAppId)
                .toList();
        assertThat(searchConfigApplications).contains(application);

        List<String> configIds = configs.stream()
                .map(SearchConfigResultDTO::getId)
                .toList();
        assertThat(configIds).containsAll(Arrays.asList(expectedIds));
    }

}
