package org.onecx.search.config.rs.external.v1;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;
import java.util.stream.Collectors;

import jakarta.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;
import org.onecx.search.config.rs.test.AbstractTest;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.search.config.v1.model.*;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class SearchConfigControllerV1Test extends AbstractTest {

    String SEARCH_CONFIG_CONTROLLER_V1_ENDPOINT = "/v1/searchConfig";
    String SEARCH_CONFIG_CONTROLLER_INTERNAL_ENDPOINT = "/internal/searchConfig";

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
    @WithDBData(value = { "search-config-data.xml" }, deleteBeforeInsert = true, deleteAfterTest = true)
    void shouldGetSearchConfigsByPage() {

        // given
        String application = "support-tool-ui";
        String page = "page1";

        var response = given()
                .when()
                .contentType(MediaType.APPLICATION_JSON)
                .get(SEARCH_CONFIG_CONTROLLER_V1_ENDPOINT + "/" + application + "/" + page)
                .prettyPeek();

        GetSearchConfigResponseDTOV1 getSearchConfigResponseDTO = response.as(GetSearchConfigResponseDTOV1.class);
        // then
        response.then().statusCode(200);
    }

    @Test
    @WithDBData(value = { "search-config-data.xml" }, deleteBeforeInsert = true, deleteAfterTest = true)
    void shouldCreateSearchConfig() {
        // given
        String application = "support-tool-ui";
        String name = "criteria-name";
        String page = "criteria-page";

        CreateSearchConfigRequestDTOV1 newSearchConfig = new CreateSearchConfigRequestDTOV1();
        newSearchConfig.setApplication(application);
        newSearchConfig.setName(name);
        newSearchConfig.setPage(page);
        newSearchConfig.values(setupValues());
        newSearchConfig.setColumns(setupColumns());
        newSearchConfig.setFieldListVersion(1);
        newSearchConfig.setIsReadOnly(true);
        newSearchConfig.setIsAdvanced(false);

        // when
        var response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .body(newSearchConfig)
                .post(SEARCH_CONFIG_CONTROLLER_V1_ENDPOINT);

        // then
        response.then().statusCode(201);

        SearchConfigDTOV1 searchConfigDTOV1 = response.as(SearchConfigDTOV1.class);

        assertThat(searchConfigDTOV1.getId()).isNotNull();
        assertThat(newSearchConfig.getApplication()).isEqualTo(searchConfigDTOV1.getApplication());
        assertThat(newSearchConfig.getName()).isEqualTo(searchConfigDTOV1.getName());
        assertThat(newSearchConfig.getPage()).isEqualTo(searchConfigDTOV1.getPage());
        assertThat(newSearchConfig.getColumns()).isEqualTo(searchConfigDTOV1.getColumns());
        assertThat(newSearchConfig.getValues()).isEqualTo(searchConfigDTOV1.getValues());
    }

    @Test
    @WithDBData(value = { "search-config-data.xml" }, deleteBeforeInsert = true, deleteAfterTest = true)
    void shouldNotCreateSearchConfig() {
        // given

        // when
        var response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post(SEARCH_CONFIG_CONTROLLER_V1_ENDPOINT);

        // then
        response.then().statusCode(400);
    }

    @Test
    @WithDBData(value = { "search-config-data.xml" }, deleteBeforeInsert = true, deleteAfterTest = true)
    void shouldUpdateModificationCount() {
        // given
        String searchConfigId = "1";

        String application = "support-tool-ui";
        String name = "criteria-name";
        String page = "criteria-page";

        UpdateSearchConfigRequestDTOV1 updateRequestBody = new UpdateSearchConfigRequestDTOV1();
        updateRequestBody.setApplication(application);
        updateRequestBody.setName(name);
        updateRequestBody.setPage(page);
        updateRequestBody.setModificationCount(1);

        // when
        var response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .body(updateRequestBody)
                .put(SEARCH_CONFIG_CONTROLLER_V1_ENDPOINT + "/" + searchConfigId);

        // then
        response.then().statusCode(200);

        SearchConfigDTOV1 searchConfigDTOV1 = response.as(SearchConfigDTOV1.class);
        assertThat(searchConfigDTOV1.getId()).isEqualTo(searchConfigId);
        assertThat(searchConfigDTOV1.getApplication()).isEqualTo(updateRequestBody.getApplication());
        assertThat(searchConfigDTOV1.getName()).isEqualTo(updateRequestBody.getName());
        assertThat(searchConfigDTOV1.getPage()).isEqualTo(updateRequestBody.getPage());
        assertThat(searchConfigDTOV1.getModificationCount()).isSameAs(1);
    }

    @Test
    @WithDBData(value = { "search-config-data.xml" }, deleteBeforeInsert = true, deleteAfterTest = true)
    void shouldNotUpdateSearchConfigWhenBadRequest() {
        // given
        String configId = "1";

        // when
        var response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .put(SEARCH_CONFIG_CONTROLLER_V1_ENDPOINT + "/" + configId).prettyPeek();

        // then
        response.then().statusCode(400);
    }

    @Test
    @WithDBData(value = { "search-config-data.xml" }, deleteBeforeInsert = true, deleteAfterTest = true)
    void shouldNotUpdateSearchConfigNotExists() {

        // given
        String searchConfigId = "NotExists";

        String application = "support-tool-ui";
        String name = "criteria-name";
        String page = "criteria-page";

        UpdateSearchConfigRequestDTOV1 updateRequestBody = new UpdateSearchConfigRequestDTOV1();
        updateRequestBody.setApplication(application);
        updateRequestBody.setName(name);
        updateRequestBody.setPage(page);
        updateRequestBody.setModificationCount(1);

        // when
        var response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .body(updateRequestBody)
                .put(SEARCH_CONFIG_CONTROLLER_V1_ENDPOINT + "/" + searchConfigId);

        // then
        response.then().statusCode(404);
    }

    @Test
    @WithDBData(value = { "search-config-data.xml" }, deleteBeforeInsert = true, deleteAfterTest = true)
    void shouldDeleteById() {
        // given
        String configId = "1";

        // when
        var response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .delete(SEARCH_CONFIG_CONTROLLER_V1_ENDPOINT + "/" + configId).prettyPeek();

        // then
        response.then().statusCode(204);

        // check deletion
        var checkResponse = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .get(SEARCH_CONFIG_CONTROLLER_INTERNAL_ENDPOINT + "/" + configId).prettyPeek();

        checkResponse.then().statusCode(404);

    }

    @Test
    @WithDBData(value = { "search-config-data.xml" }, deleteBeforeInsert = true, deleteAfterTest = true)
    void shouldNotDeleteWhenNotExists() {
        // given
        String configId = "NotExists";

        // when
        var response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .delete(SEARCH_CONFIG_CONTROLLER_V1_ENDPOINT + "/" + configId);

        // then
        response.then().statusCode(404);
    }

    @Test
    @WithDBData(value = { "search-config-data.xml" }, deleteBeforeInsert = true, deleteAfterTest = true)
    void shouldFindByCriteria() {
        // given
        String application = "support-tool-ui";
        SearchConfigSearchRequestDTOV1 requestBody = new SearchConfigSearchRequestDTOV1();
        requestBody.setApplication(application);

        String[] expectedIds = { "1", "2", "3", "4" };

        // when
        var response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .body(requestBody)
                .post(SEARCH_CONFIG_CONTROLLER_V1_ENDPOINT + "/search");

        // then
        response.then().statusCode(200);

        GetSearchConfigResponseDTOV1 responseDTOV1 = response.as(GetSearchConfigResponseDTOV1.class);
        List<SearchConfigDTOV1> configs = responseDTOV1.getConfigs();

        assertThat(configs.size()).isSameAs(4);

        List<String> searchConfigApplications = configs.stream()
                .map(SearchConfigDTOV1::getApplication)
                .collect(Collectors.toList());
        assertThat(searchConfigApplications).contains(application);

        List<String> configIds = configs.stream()
                .map(SearchConfigDTOV1::getId)
                .collect(Collectors.toList());
        assertThat(configIds).containsAll(Arrays.asList(expectedIds));
    }

    @Test
    @WithDBData(value = { "search-config-data.xml" }, deleteBeforeInsert = true, deleteAfterTest = true)
    void shouldFindByCriteriaPage() {
        // given
        String page = "page1";
        SearchConfigSearchRequestDTOV1 requestBody = new SearchConfigSearchRequestDTOV1();
        requestBody.setPage(page);

        String[] expectedIds = { "1", "2" };

        // when
        var response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .body(requestBody)
                .post(SEARCH_CONFIG_CONTROLLER_V1_ENDPOINT + "/search");

        // then
        response.then().statusCode(200);

        GetSearchConfigResponseDTOV1 responseDTOV1 = response.as(GetSearchConfigResponseDTOV1.class);
        List<SearchConfigDTOV1> configs = responseDTOV1.getConfigs();

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
    @WithDBData(value = { "search-config-data.xml" }, deleteBeforeInsert = true, deleteAfterTest = true)
    void shouldFindOneResultByCriteria() {
        // given
        String application = "support-tool-ui";
        String page = "page1";
        String name = "name1";
        SearchConfigSearchRequestDTOV1 requestBody = new SearchConfigSearchRequestDTOV1();
        requestBody.setApplication(application);
        requestBody.setPage(page);
        requestBody.setName(name);

        // when
        var response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .body(requestBody)
                .post(SEARCH_CONFIG_CONTROLLER_V1_ENDPOINT + "/search");

        // then
        response.then().statusCode(200);

        GetSearchConfigResponseDTOV1 responseDTOV1 = response.as(GetSearchConfigResponseDTOV1.class);
        List<SearchConfigDTOV1> configs = responseDTOV1.getConfigs();

        assertThat(configs.size()).isSameAs(1);

        SearchConfigDTOV1 searchConfigDTOV1 = configs.get(0);
        assertThat(searchConfigDTOV1.getId()).isEqualTo("2");
        assertThat(searchConfigDTOV1.getApplication()).isEqualTo(application);
        assertThat(searchConfigDTOV1.getName()).isEqualTo(name);
        assertThat(searchConfigDTOV1.getPage()).isEqualTo(page);
    }

    @Test
    @WithDBData(value = { "search-config-data.xml" }, deleteBeforeInsert = true, deleteAfterTest = true)
    void shouldFindByCriteriaNoMatch() {
        // given
        String application = "no-match-app";
        SearchConfigSearchRequestDTOV1 requestBody = new SearchConfigSearchRequestDTOV1();
        requestBody.setApplication(application);

        // when
        var response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .body(requestBody)
                .post(SEARCH_CONFIG_CONTROLLER_V1_ENDPOINT + "/search");

        // then
        response.then().statusCode(200);

        GetSearchConfigResponseDTOV1 responseDTOV1 = response.as(GetSearchConfigResponseDTOV1.class);
        assertThat(responseDTOV1.getConfigs()).isEmpty();
    }

    @Test
    @WithDBData(value = { "search-config-data.xml" }, deleteBeforeInsert = true, deleteAfterTest = true)
    void shouldFindAllByCriteriaEmpty() {
        // given
        SearchConfigSearchRequestDTOV1 searchConfigSearchCriteria = new SearchConfigSearchRequestDTOV1();

        // when
        var response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .body(searchConfigSearchCriteria)
                .post(SEARCH_CONFIG_CONTROLLER_V1_ENDPOINT + "/search");

        // then
        response.then().statusCode(200);

        GetSearchConfigResponseDTOV1 responseDTOV1 = response.as(GetSearchConfigResponseDTOV1.class);
        assertThat(responseDTOV1.getConfigs()).hasSize(8);
    }

    @Test
    @WithDBData(value = { "search-config-data.xml" }, deleteBeforeInsert = true, deleteAfterTest = true)
    void shouldNotFindByCriteriaNullCriteria() {
        // given

        // when
        var response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post(SEARCH_CONFIG_CONTROLLER_V1_ENDPOINT + "/search");

        // then
        response.then().statusCode(400);
    }
}
