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
    void shouldGetSearchConfigsByPage() {

        String productName = "productName1";
        String application = "support-tool-ui";
        String page = "page1";

        var responseDTO = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org2", null))
                .get("/" + productName + "/" + application + "/" + page)
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body().as(SearchPageResultDTOV1.class);

        assertThat(responseDTO.getTotalElements()).isZero();

        responseDTO = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1", null))
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
                .body().as(SearchConfigDTOV1.class);

        var dto = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1", null))
                .get("/" + productName + "/" + application + "/" + page)
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body().as(SearchPageResultDTOV1.class);

        assertThat(dto.getTotalElements()).isZero();

        dto = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org2", null))
                .get("/" + productName + "/" + application + "/" + page)
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body().as(SearchPageResultDTOV1.class);

        assertThat(dto.getTotalElements()).isEqualTo(1);

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
                .as(SearchConfigDTOV1.class);

        assertThat(searchConfigDTO.getId()).isEqualTo(searchConfigId);
        assertThat(searchConfigDTO.getAppId()).isEqualTo(updateRequestBody.getAppId());
        assertThat(searchConfigDTO.getName()).isEqualTo(updateRequestBody.getName());
        assertThat(searchConfigDTO.getPage()).isEqualTo(updateRequestBody.getPage());
        assertThat(searchConfigDTO.getModificationCount()).isEqualTo(1);
    }

    @Test
    void shouldDeleteById() {
        String configId = "8";

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

        var responseDTO = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org2", null))
                .get("/" + productName + "/" + application + "/" + page)
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body().as(SearchPageResultDTOV1.class);

        assertThat(responseDTO.getTotalElements()).isEqualTo(1);

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
                .get("/" + productName + "/" + application + "/" + page)
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body().as(SearchPageResultDTOV1.class);

        assertThat(responseDTO.getTotalElements()).isZero();

    }

    @Test
    void shouldFindByCriteria() {
        String application = "task-list-ui";
        SearchConfigSearchRequestDTOV1 requestBody = new SearchConfigSearchRequestDTOV1();
        requestBody.setAppId(application);

        String[] expectedIds = { "8" };

        var responseDTO = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org2", null))
                .body(requestBody)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .as(SearchPageResultDTOV1.class);

        List<SearchConfigDTOV1> configs = responseDTO.getStream();

        assertThat(configs.size()).isSameAs(1);

        List<String> searchConfigApplications = configs.stream()
                .map(SearchConfigDTOV1::getAppId)
                .collect(Collectors.toList());
        assertThat(searchConfigApplications).contains(application);

        List<String> configIds = configs.stream()
                .map(SearchConfigDTOV1::getId)
                .collect(Collectors.toList());
        assertThat(configIds).containsAll(Arrays.asList(expectedIds));
    }
}
