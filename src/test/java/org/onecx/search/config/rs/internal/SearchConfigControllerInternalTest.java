package org.onecx.search.config.rs.internal;

import gen.io.github.onecx.search.config.rs.internal.model.*;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;
import org.onecx.search.config.rs.test.AbstractTest;
import org.tkit.quarkus.test.WithDBData;

import java.util.*;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class SearchConfigControllerInternalTest extends AbstractTest {

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
    @WithDBData(value = {"search-config-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    void shouldGetSearchConfigsById() {

        // given
        String configId = "1";

        var response = given()
                .when()
                .contentType(MediaType.APPLICATION_JSON)
                .get(SEARCH_CONFIG_CONTROLLER_INTERNAL_ENDPOINT + "/" + configId)
                .prettyPeek();

        SearchConfigDTO searchConfigDTO = response.as(SearchConfigDTO.class);
        // then
        response.then().statusCode(200);
        assertThat(searchConfigDTO.getApplication()).isEqualTo("support-tool-ui");
    }

    @Test
    @WithDBData(value = {"search-config-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    void shouldCreateSearchConfig() {
        // given
        String application = "support-tool-ui";
        String name = "criteria-name";
        String page = "criteria-page";

        CreateSearchConfigRequestDTO requestBody = new CreateSearchConfigRequestDTO();
        SearchConfigDTO newSearchConfig = new SearchConfigDTO();
        newSearchConfig.setApplication(application);
        newSearchConfig.setName(name);
        newSearchConfig.setPage(page);
        newSearchConfig.values(setupValues());
        newSearchConfig.setColumns(setupColumns());
        newSearchConfig.setFieldListVersion(1);
        newSearchConfig.setIsReadOnly(true);
        newSearchConfig.setIsAdvanced(false);
        requestBody.setConfig(newSearchConfig);

        // when
        var response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .body(requestBody)
                .post(SEARCH_CONFIG_CONTROLLER_INTERNAL_ENDPOINT);

        // then
        response.then().statusCode(201);

        SearchConfigDTO searchConfigDTO = response.as(SearchConfigDTO.class);

        assertThat(searchConfigDTO.getId()).isNotNull();
        assertThat(newSearchConfig.getApplication()).isEqualTo(searchConfigDTO.getApplication());
        assertThat(newSearchConfig.getName()).isEqualTo(searchConfigDTO.getName());
        assertThat(newSearchConfig.getPage()).isEqualTo(searchConfigDTO.getPage());
        assertThat(newSearchConfig.getColumns()).isEqualTo(searchConfigDTO.getColumns());
        assertThat(newSearchConfig.getValues()).isEqualTo(searchConfigDTO.getValues());
    }

    @Test
    @WithDBData(value = {"search-config-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    void shouldNotCreateSearchConfig() {
        // given

        // when
        var response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post(SEARCH_CONFIG_CONTROLLER_INTERNAL_ENDPOINT);

        // then
        response.then().statusCode(400);
    }

    @Test
    @WithDBData(value = {"search-config-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    void shouldUpdateModificationCount() {
        // given
        String searchConfigId = "1";

        String application = "support-tool-ui";
        String name = "criteria-name";
        String page = "criteria-page";

        UpdateSearchConfigRequestDTO updateRequestBody = new UpdateSearchConfigRequestDTO();
        SearchConfigDTO searchConfigUpdate = new SearchConfigDTO();
        searchConfigUpdate.setApplication(application);
        searchConfigUpdate.setName(name);
        searchConfigUpdate.setId(searchConfigId);
        searchConfigUpdate.setPage(page);
        searchConfigUpdate.setModificationCount(0);
        updateRequestBody.setConfig(searchConfigUpdate);

        // when
        var response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .body(updateRequestBody)
                .put(SEARCH_CONFIG_CONTROLLER_INTERNAL_ENDPOINT + "/" + searchConfigId);

        // then
        response.then().statusCode(200);

        SearchConfigDTO searchConfigDTO = response.as(SearchConfigDTO.class);
        assertThat(searchConfigDTO.getId()).isEqualTo(searchConfigId);
        assertThat(searchConfigDTO.getApplication()).isEqualTo(searchConfigUpdate.getApplication());
        assertThat(searchConfigDTO.getName()).isEqualTo(searchConfigUpdate.getName());
        assertThat(searchConfigDTO.getPage()).isEqualTo(searchConfigUpdate.getPage());
        assertThat(searchConfigDTO.getModificationCount()).isEqualTo(1);
    }

    @Test
    @WithDBData(value = {"search-config-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    void shouldNotUpdateSearchConfigWhenBadRequest() {
        // given
        String configId = "1";

        // when
        var response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .put(SEARCH_CONFIG_CONTROLLER_INTERNAL_ENDPOINT + "/" + configId).prettyPeek();

        // then
        response.then().statusCode(400);
    }

    @Test
    @WithDBData(value = {"search-config-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    void shouldNotUpdateSearchConfigNotExists() {

        // given
        String searchConfigId = "NotExists";

        String application = "support-tool-ui";
        String name = "criteria-name";
        String page = "criteria-page";

        UpdateSearchConfigRequestDTO updateRequestBody = new UpdateSearchConfigRequestDTO();
        SearchConfigDTO searchConfigUpdate = new SearchConfigDTO();
        searchConfigUpdate.setApplication(application);
        searchConfigUpdate.setName(name);
        searchConfigUpdate.setId(searchConfigId);
        searchConfigUpdate.setPage(page);
        searchConfigUpdate.setModificationCount(0);
        updateRequestBody.setConfig(searchConfigUpdate);

        // when
        var response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .body(updateRequestBody)
                .put(SEARCH_CONFIG_CONTROLLER_INTERNAL_ENDPOINT + "/" + searchConfigId).prettyPeek();

        // then
        response.then().statusCode(404);
    }

    @Test
    @WithDBData(value = {"search-config-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    void shouldDeleteById() {
        // given
        String configId = "1";

        // when
        var response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .delete(SEARCH_CONFIG_CONTROLLER_INTERNAL_ENDPOINT + "/" + configId).prettyPeek();

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
    @WithDBData(value = {"search-config-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    void shouldNotDeleteWhenNotExists() {
        // given
        String configId = "NotExists";

        // when
        var response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .delete(SEARCH_CONFIG_CONTROLLER_INTERNAL_ENDPOINT + "/" + configId);

        // then
        response.then().statusCode(404);
    }

    @Test
    @WithDBData(value = {"search-config-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    void shouldFindByCriteria() {
        // given
        String application = "support-tool-ui";
        SearchConfigSearchRequestDTO requestBody = new SearchConfigSearchRequestDTO();
        requestBody.setApplication(application);

        String[] expectedIds = {"1", "2", "3", "4"};

        // when
        var response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .body(requestBody)
                .post(SEARCH_CONFIG_CONTROLLER_INTERNAL_ENDPOINT + "/search");

        // then
        response.then().statusCode(200);

        GetSearchConfigResponseDTO responseDTO = response.as(GetSearchConfigResponseDTO.class);
        List<SearchConfigDTO> configs = responseDTO.getConfigs();

        assertThat(configs.size()).isSameAs(4);

        List<String> searchConfigApplications = configs.stream()
                .map(SearchConfigDTO::getApplication)
                .collect(Collectors.toList());
        assertThat(searchConfigApplications).contains(application);

        List<String> configIds = configs.stream()
                .map(SearchConfigDTO::getId)
                .collect(Collectors.toList());
        assertThat(configIds).containsAll(Arrays.asList(expectedIds));
    }

    @Test
    @WithDBData(value = {"search-config-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    void shouldFindByCriteriaNoMatch() {
        // given
        String application = "no-match-app";
        SearchConfigSearchRequestDTO requestBody = new SearchConfigSearchRequestDTO();
        requestBody.setApplication(application);

        // when
        var response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .body(requestBody)
                .post(SEARCH_CONFIG_CONTROLLER_INTERNAL_ENDPOINT + "/search");

        // then
        response.then().statusCode(200);

        GetSearchConfigResponseDTO responseDTO = response.as(GetSearchConfigResponseDTO.class);
        assertThat(responseDTO.getConfigs()).isEmpty();
    }

    @Test
    @WithDBData(value = {"search-config-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    void shouldFindAllByCriteriaEmpty() {
        // given
        SearchConfigSearchRequestDTO searchConfigSearchCriteria = new SearchConfigSearchRequestDTO();

        // when
        var response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .body(searchConfigSearchCriteria)
                .post(SEARCH_CONFIG_CONTROLLER_INTERNAL_ENDPOINT + "/search");

        // then
        response.then().statusCode(200);

        GetSearchConfigResponseDTO responseDTO = response.as(GetSearchConfigResponseDTO.class);
        assertThat(responseDTO.getConfigs()).hasSize(8);
    }

    @Test
    @WithDBData(value = {"search-config-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    void shouldNotFindByCriteriaNullCriteria() {
        // given

        // when
        var response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post(SEARCH_CONFIG_CONTROLLER_INTERNAL_ENDPOINT + "/search");

        // then
        response.then().statusCode(400);
    }
}
