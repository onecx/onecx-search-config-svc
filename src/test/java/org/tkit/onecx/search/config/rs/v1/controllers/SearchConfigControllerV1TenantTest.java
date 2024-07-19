package org.tkit.onecx.search.config.rs.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.resteasy.reactive.RestResponse.Status.*;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.search.config.test.AbstractTest;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.search.config.rs.internal.model.SearchConfigDTO;
import gen.org.tkit.onecx.search.config.v1.model.*;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(SearchConfigControllerV1.class)
@WithDBData(value = "search-config-data.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class SearchConfigControllerV1TenantTest extends AbstractTest {

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
        String configId = "c1";

        var dto = given()
                .contentType(APPLICATION_JSON)
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
                .header(APM_HEADER_PARAM, createToken("org2", null))
                .get(configId)
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldCreateSearchConfig() {
        String productName = "productName1";
        String application = "support-tool-ui";
        String name = "test-criteria-name";
        String page = "test-criteria-page";

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
                .header(APM_HEADER_PARAM, createToken("org2", null))
                .body(newSearchConfig)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(CreateSearchConfigResponseDTOV1.class);

        assertThat(searchConfigDTOV1).isNotNull();

        var requestBody = new SearchConfigSearchRequestDTOV1()
                .productName(newSearchConfig.getProductName())
                .appId(newSearchConfig.getAppId())
                .page(newSearchConfig.getPage());

        var responseDTO = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org2", null))
                .body(requestBody)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .as(SearchConfigSearchPageResultDTOV1.class);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getStream()).isNotNull().isNotEmpty().hasSize(1);

    }

    @Test
    void shouldUpdateModificationCount() {
        String searchConfigId = "c1";

        String name = "criteria-name";

        UpdateSearchConfigRequestDTOV1 updateRequestBody = new UpdateSearchConfigRequestDTOV1();
        updateRequestBody.setName(name);
        updateRequestBody.setModificationCount(0);

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org2", null))
                .body(updateRequestBody)
                .put(searchConfigId)
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        var searchConfigDTO = given()
                .when()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1", null))
                .body(updateRequestBody)
                .put(searchConfigId)
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .as(UpdateSearchConfigResponseDTOV1.class);

        assertThat(searchConfigDTO).isNotNull();
        assertThat(searchConfigDTO.getConfig()).isNotNull();
        assertThat(searchConfigDTO.getConfig().getName()).isEqualTo(name);
        assertThat(searchConfigDTO.getConfig().getName()).isEqualTo(updateRequestBody.getName());
        assertThat(searchConfigDTO.getConfig().getModificationCount()).isEqualTo(1);
    }

    @Test
    void shouldDeleteByConfigId() {
        String configId = "c8";

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1", null))
                .delete(configId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        String productName = "productName3";
        String application = "task-list-ui";
        String page = "task-page2";

        var requestBody = new SearchConfigSearchRequestDTOV1()
                .productName(productName)
                .appId(application)
                .page(page);

        var responseDTO = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org2", null))
                .body(requestBody)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .as(SearchConfigSearchPageResultDTOV1.class);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getStream()).isNotNull().isNotEmpty().hasSize(1);

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org2", null))
                .delete(configId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        responseDTO = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org2", null))
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
    void shouldFindByCriteria() {
        String application = "task-list-ui";
        var requestBody = new SearchConfigSearchRequestDTOV1()
                .appId(application).page("task-page2").productName("productName3");

        String[] expectedIds = { "name3" };

        var responseDTO = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org2", null))
                .body(requestBody)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .as(SearchConfigSearchPageResultDTOV1.class);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getStream()).isNotNull().isNotEmpty().hasSize(1);

        List<SearchConfigSearchResultDTOV1> configs = responseDTO.getStream();

        List<String> configIds = configs.stream()
                .map(SearchConfigSearchResultDTOV1::getName)
                .toList();
        assertThat(configIds).containsAll(Arrays.asList(expectedIds));
    }
}
