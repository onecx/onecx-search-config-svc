package org.onecx.search.config.rs.external.v1;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.onecx.search.config.rs.test.AbstractTest;

import org.tkit.quarkus.test.WithDBData;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class SearchTemplateRestControllerTest extends AbstractTest {



    @Test
    @WithDBData(value = {"search-config-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    public void shouldGetSearchTemplateById() {


        // given
        String templateId = "0bdd8a31-185a-4e13-9c4c-780ed1beec07";

        String expectedApplication = "support-tool-ui";
        String expectedUser = "89e93c2c-8fd5-48a0-81b2-8b988a7cb5cc";
        String expectedCriteria = "{\"name\": \"name\",\"id\": \"id\",\"responsible\": \"responsible\"}";

        // when
        Response response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .get("/v1/searchTemplates/" + templateId);

        // then
        response.then().statusCode(200);

        SearchTemplateDTOv1 searchTemplateDTOv1 = response.as(SearchTemplateDTOv1.class);

        assertEquals(templateId, searchTemplateDTOv1.getId());
        assertEquals(expectedApplication, searchTemplateDTOv1.getApplication());
        assertEquals(expectedUser, searchTemplateDTOv1.getUser());
        assertEquals(expectedCriteria, searchTemplateDTOv1.getCriteriaAsJson());
    }

    @Test
    @WithDBData(value = {"tkitportal-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    public void shouldNotGetSearchTemplateByIdNotExists() {
        // given
        String templateId = "NotExists";

        // when
        Response response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .header("Authorization", "bearer " + defaultValidToken)
                .get("/v1/searchTemplates/" + templateId);

        // then
        response.then().statusCode(200);

        assertTrue(response.getBody().asString().isEmpty());
    }

    @Test
    @WithDBData(value = {"tkitportal-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    public void shouldCreateSearchTemplate() {
        // given
        String application = "support-tool-ui";
        String name = "criteria-name";
        String page = "criteria-page";
        String user = "89e93c2c-8fd5-48a0-81b2-8b988a7cb5cc";
        String criteria = "{\"name\": \"name\",\"id\": \"id\",\"responsible\": \"responsible\"}";

        SearchTemplateDTOv1 newSearchTemplateDTOv1 = new SearchTemplateDTOv1();
        newSearchTemplateDTOv1.setApplication(application);
        newSearchTemplateDTOv1.setName(name);
        newSearchTemplateDTOv1.setPage(page);
        newSearchTemplateDTOv1.setUser(user);
        newSearchTemplateDTOv1.setCriteriaAsJson(criteria);

        // when
        Response response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .body(newSearchTemplateDTOv1)
                .header("Authorization", "bearer " + defaultValidToken)
                .post("/v1/searchTemplates");

        // then
        response.then().statusCode(201);

        SearchTemplateDTOv1 searchTemplateDTOv1 = response.as(SearchTemplateDTOv1.class);

        assertNotNull(searchTemplateDTOv1.getId());
        assertEquals(newSearchTemplateDTOv1.getApplication(), searchTemplateDTOv1.getApplication());
        assertEquals(newSearchTemplateDTOv1.getName(), searchTemplateDTOv1.getName());
        assertEquals(newSearchTemplateDTOv1.getUser(), searchTemplateDTOv1.getUser());
        assertEquals(newSearchTemplateDTOv1.getPage(), searchTemplateDTOv1.getPage());
        assertEquals(newSearchTemplateDTOv1.getCriteriaAsJson(), searchTemplateDTOv1.getCriteriaAsJson());
    }

    @Test
    @WithDBData(value = {"tkitportal-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    public void shouldCreateDefaultSearchTemplate() {
        // given
        String application = "support-tool-ui";
        String name = "criteria-name";
        String page = "page1";
        String user = "89e93c2c-8fd5-48a0-81b2-8b988a7cb5cc";
        String criteria = "{\"name\": \"name\",\"id\": \"id\",\"responsible\": \"responsible\"}";

        SearchTemplateDTOv1 newSearchTemplateDTOv1 = new SearchTemplateDTOv1();
        newSearchTemplateDTOv1.setApplication(application);
        newSearchTemplateDTOv1.setName(name);
        newSearchTemplateDTOv1.setPage(page);
        newSearchTemplateDTOv1.setUser(user);
        newSearchTemplateDTOv1.setCriteriaAsJson(criteria);
        newSearchTemplateDTOv1.setDefaultTemplate(true);

        // when
        Response response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .body(newSearchTemplateDTOv1)
                .header("Authorization", "bearer " + defaultValidToken)
                .post("/v1/searchTemplates");

        // then
        response.then().statusCode(201);

        SearchTemplateDTOv1 searchTemplateDTOv1 = response.as(SearchTemplateDTOv1.class);

        assertNotNull(searchTemplateDTOv1.getId());
        assertEquals(newSearchTemplateDTOv1.getApplication(), searchTemplateDTOv1.getApplication());
        assertEquals(newSearchTemplateDTOv1.getName(), searchTemplateDTOv1.getName());
        assertEquals(newSearchTemplateDTOv1.getUser(), searchTemplateDTOv1.getUser());
        assertEquals(newSearchTemplateDTOv1.getPage(), searchTemplateDTOv1.getPage());
        assertEquals(newSearchTemplateDTOv1.getCriteriaAsJson(), searchTemplateDTOv1.getCriteriaAsJson());

        // check old default
        Response checkResponse =
                given().contentType(MediaType.APPLICATION_JSON)
                        .when()
                        .header("Authorization", "bearer " + defaultValidToken)
                        .get("/v1/searchTemplates/" + "0bdd8a31-185a-4e13-9c4c-780ed1beec07");

        checkResponse.then().statusCode(200);

        SearchTemplateDTOv1 oldDefaultSearchTemplateDTOv1 = checkResponse.as(SearchTemplateDTOv1.class);

        assertFalse(oldDefaultSearchTemplateDTOv1.isDefaultTemplate());
    }

    @Test
    @WithDBData(value = {"tkitportal-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    public void shouldNotCreateSearchTemplateNullTemplate() {
        // given
        String expectedErrorCode = "BAD_REQUEST";

        // when
        Response response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .header("Authorization", "bearer " + defaultValidToken)
                .post("/v1/searchTemplates");

        // then
        response.then().statusCode(400);

        RestExceptionDTO restExceptionDTO = response.as(RestExceptionDTO.class);

        assertEquals(expectedErrorCode, restExceptionDTO.getErrorCode());
    }

    @Test
    @WithDBData(value = {"tkitportal-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    public void shouldUpdateSearchTemplateDefaultAsNotDefault() {
        // given
        String searchTemplateId = "0bdd8a31-185a-4e13-9c4c-780ed1beec07";

        String application = "support-tool-ui";
        String name = "criteria-name";
        String page = "criteria-page";
        String user = "89e93c2c-8fd5-48a0-81b2-8b988a7cb5cc";
        String criteria = "{\"name\": \"name\",\"id\": \"id\",\"responsible\": \"responsible\"}";

        SearchTemplateDTOv1 newSearchTemplateDTOv1 = new SearchTemplateDTOv1();
        newSearchTemplateDTOv1.setApplication(application);
        newSearchTemplateDTOv1.setName(name);
        newSearchTemplateDTOv1.setPage(page);
        newSearchTemplateDTOv1.setUser(user);
        newSearchTemplateDTOv1.setDefaultTemplate(false);
        newSearchTemplateDTOv1.setCriteriaAsJson(criteria);

        // when
        Response response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .body(newSearchTemplateDTOv1)
                .header("Authorization", "bearer " + defaultValidToken)
                .put("/v1/searchTemplates/" + searchTemplateId);

        // then
        response.then().statusCode(200);

        SearchTemplateDTOv1 searchTemplateDTOv1 = response.as(SearchTemplateDTOv1.class);

        assertEquals(searchTemplateId, searchTemplateDTOv1.getId());
        assertEquals(newSearchTemplateDTOv1.getApplication(), searchTemplateDTOv1.getApplication());
        assertEquals(newSearchTemplateDTOv1.getName(), searchTemplateDTOv1.getName());
        assertEquals(false, searchTemplateDTOv1.isDefaultTemplate());
        assertEquals(newSearchTemplateDTOv1.getUser(), searchTemplateDTOv1.getUser());
        assertEquals(newSearchTemplateDTOv1.getPage(), searchTemplateDTOv1.getPage());
        assertEquals(newSearchTemplateDTOv1.getCriteriaAsJson(), searchTemplateDTOv1.getCriteriaAsJson());
    }

    @Test
    @WithDBData(value = {"tkitportal-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    public void shouldUpdateSearchTemplateDefaultAsDefault() {
        // given
        String searchTemplateId = "0bdd8a31-185a-4e13-9c4c-780ed1beec07";

        String application = "support-tool-ui";
        String name = "criteria-name";
        String page = "criteria-page";
        String user = "89e93c2c-8fd5-48a0-81b2-8b988a7cb5cc";
        String criteria = "{\"name\": \"name\",\"id\": \"id\",\"responsible\": \"responsible\"}";

        SearchTemplateDTOv1 newSearchTemplateDTOv1 = new SearchTemplateDTOv1();
        newSearchTemplateDTOv1.setApplication(application);
        newSearchTemplateDTOv1.setName(name);
        newSearchTemplateDTOv1.setPage(page);
        newSearchTemplateDTOv1.setUser(user);
        newSearchTemplateDTOv1.setDefaultTemplate(true);
        newSearchTemplateDTOv1.setModificationCount(1);
        newSearchTemplateDTOv1.setCriteriaAsJson(criteria);

        // when
        Response response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .body(newSearchTemplateDTOv1)
                .header("Authorization", "bearer " + defaultValidToken)
                .put("/v1/searchTemplates/" + searchTemplateId);

        // then
        response.then().statusCode(200);

        SearchTemplateDTOv1 searchTemplateDTOv1 = response.as(SearchTemplateDTOv1.class);

        assertEquals(searchTemplateId, searchTemplateDTOv1.getId());
        assertEquals(newSearchTemplateDTOv1.getApplication(), searchTemplateDTOv1.getApplication());
        assertEquals(newSearchTemplateDTOv1.getName(), searchTemplateDTOv1.getName());
        assertEquals(true, searchTemplateDTOv1.isDefaultTemplate());
        assertEquals(newSearchTemplateDTOv1.getUser(), searchTemplateDTOv1.getUser());
        assertEquals(newSearchTemplateDTOv1.getPage(), searchTemplateDTOv1.getPage());
        assertEquals(newSearchTemplateDTOv1.getModificationCount(), searchTemplateDTOv1.getModificationCount());
        assertEquals(newSearchTemplateDTOv1.getCriteriaAsJson(), searchTemplateDTOv1.getCriteriaAsJson());
    }

    @Test
    @WithDBData(value = {"tkitportal-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    public void shouldUpdateSearchTemplateNotDefaultAsDefault() {
        // given
        String searchTemplateId = "2267f153-724f-4616-b547-d82fafdf8efc";

        String application = "support-tool-ui";
        String name = "criteria-name";
        String page = "page1";
        String user = "89e93c2c-8fd5-48a0-81b2-8b988a7cb5cc";
        String criteria = "{\"name\": \"name\",\"id\": \"id\",\"responsible\": \"responsible\"}";

        SearchTemplateDTOv1 newSearchTemplateDTOv1 = new SearchTemplateDTOv1();
        newSearchTemplateDTOv1.setApplication(application);
        newSearchTemplateDTOv1.setName(name);
        newSearchTemplateDTOv1.setPage(page);
        newSearchTemplateDTOv1.setUser(user);
        newSearchTemplateDTOv1.setDefaultTemplate(true);
        newSearchTemplateDTOv1.setCriteriaAsJson(criteria);

        // when
        Response response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .body(newSearchTemplateDTOv1)
                .header("Authorization", "bearer " + defaultValidToken)
                .put("/v1/searchTemplates/" + searchTemplateId);

        // then
        response.then().statusCode(200);

        SearchTemplateDTOv1 searchTemplateDTOv1 = response.as(SearchTemplateDTOv1.class);

        assertEquals(searchTemplateId, searchTemplateDTOv1.getId());
        assertEquals(newSearchTemplateDTOv1.getApplication(), searchTemplateDTOv1.getApplication());
        assertEquals(newSearchTemplateDTOv1.getName(), searchTemplateDTOv1.getName());
        assertEquals(true, searchTemplateDTOv1.isDefaultTemplate());
        assertEquals(newSearchTemplateDTOv1.getUser(), searchTemplateDTOv1.getUser());
        assertEquals(newSearchTemplateDTOv1.getPage(), searchTemplateDTOv1.getPage());
        assertEquals(newSearchTemplateDTOv1.getCriteriaAsJson(), searchTemplateDTOv1.getCriteriaAsJson());

        // check old default
        Response checkResponse =
                given().contentType(MediaType.APPLICATION_JSON)
                        .when()
                        .header("Authorization", "bearer " + defaultValidToken)
                        .get("/v1/searchTemplates/" + "0bdd8a31-185a-4e13-9c4c-780ed1beec07");

        checkResponse.then().statusCode(200);

        SearchTemplateDTOv1 oldDefaultSearchTemplateDTOv1 = checkResponse.as(SearchTemplateDTOv1.class);

        assertFalse(oldDefaultSearchTemplateDTOv1.isDefaultTemplate());
    }

    @Test
    @WithDBData(value = {"tkitportal-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    public void shouldNotUpdateSearchTemplateNullTemplate() {
        // given
        String searchTemplateId = "0bdd8a31-185a-4e13-9c4c-780ed1beec07";

        String expectedErrorCode = "BAD_REQUEST";

        // when
        Response response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .header("Authorization", "bearer " + defaultValidToken)
                .put("/v1/searchTemplates/" + searchTemplateId);

        // then
        response.then().statusCode(400);

        RestExceptionDTO restExceptionDTO = response.as(RestExceptionDTO.class);

        assertEquals(expectedErrorCode, restExceptionDTO.getErrorCode());
    }

    @Test
    @WithDBData(value = {"tkitportal-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    public void shouldNotUpdateSearchTemplateNotExists() {
        // given
        String searchTemplateId = "NotExists";

        String application = "support-tool-ui";
        String name = "criteria-name";
        String page = "criteria-page";
        String user = "89e93c2c-8fd5-48a0-81b2-8b988a7cb5cc";
        String criteria = "{\"name\": \"name\",\"id\": \"id\",\"responsible\": \"responsible\"}";

        SearchTemplateDTOv1 newSearchTemplateDTOv1 = new SearchTemplateDTOv1();
        newSearchTemplateDTOv1.setApplication(application);
        newSearchTemplateDTOv1.setName(name);
        newSearchTemplateDTOv1.setPage(page);
        newSearchTemplateDTOv1.setUser(user);
        newSearchTemplateDTOv1.setDefaultTemplate(true);
        newSearchTemplateDTOv1.setCriteriaAsJson(criteria);

        String expectedErrorCode = "BAD_REQUEST";

        // when
        Response response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .body(newSearchTemplateDTOv1)
                .header("Authorization", "bearer " + defaultValidToken)
                .put("/v1/searchTemplates/" + searchTemplateId);

        // then
        response.then().statusCode(400);

        RestExceptionDTO restExceptionDTO = response.as(RestExceptionDTO.class);

        assertEquals(expectedErrorCode, restExceptionDTO.getErrorCode());
    }

    @Test
    @WithDBData(value = {"tkitportal-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    public void shouldDeleteSearchTemplateById() {
        // given
        String templateId = "0bdd8a31-185a-4e13-9c4c-780ed1beec07";

        // when
        Response response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .header("Authorization", "bearer " + defaultValidToken)
                .delete("/v1/searchTemplates/" + templateId);

        // then
        response.then().statusCode(204);

        // check deletion
        Response checkResponse = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .header("Authorization", "bearer " + defaultValidToken)
                .get("/v1/searchTemplates/" + templateId);

        checkResponse.then().statusCode(200);

        assertTrue(checkResponse.getBody().asString().isEmpty());
    }

    @Test
    @WithDBData(value = {"tkitportal-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    public void shouldIgnoreDeleteSearchTemplateNotExists() {
        // given
        String searchTemplateId = "NotExists";

        // when
        Response response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .header("Authorization", "bearer " + defaultValidToken)
                .delete("/v1/searchTemplates/" + searchTemplateId);

        // then
        response.then().statusCode(204);
    }

    @Test
    @WithDBData(value = {"tkitportal-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    public void shouldFindByCriteria() {
        // given
        String application = "support-tool-ui";
        SearchTemplateSearchCriteria searchTemplateSearchCriteria = new SearchTemplateSearchCriteria();
        searchTemplateSearchCriteria.setApplication(application);

        String[] expectedIds = {
                "0bdd8a31-185a-4e13-9c4c-780ed1beec07",
                "9897dfbe-baa5-4d54-9e3b-6131f87684e1",
                "2267f153-724f-4616-b547-d82fafdf8efc",
                "42c1cf75-e1c6-4d20-ab71-20422cdd224a",
                "d91532d1-a727-44e5-8ed6-ed72b1bec5fe",
                "18a0c67a-d1b6-4b06-badf-8a9bea72c4cc",
        };

        // when
        Response response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .header("Authorization", "bearer " + defaultValidToken)
                .body(searchTemplateSearchCriteria)
                .post("/v1/searchTemplates/search/criteria");

        // then
        response.then().statusCode(200);

        List<SearchTemplateDTOv1> searchTemplateDTOv1s = Arrays.asList(response.as(SearchTemplateDTOv1[].class));

        assertEquals(6, searchTemplateDTOv1s.size());

        List<String> searchTemplateApplications = searchTemplateDTOv1s.stream()
                .map(SearchTemplateDTOv1::getApplication)
                .collect(Collectors.toList());
        assertTrue(searchTemplateApplications.contains(application));

        List<String> searchTemplateIds = searchTemplateDTOv1s.stream()
                .map(SearchTemplateDTOv1::getId)
                .collect(Collectors.toList());
        assertTrue(searchTemplateIds.containsAll(Arrays.asList(expectedIds)));
    }

    @Test
    @WithDBData(value = {"tkitportal-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    public void shouldFindByCriteriaGlobalAndUser() {
        // given
        String application = "support-tool-ui";
        SearchTemplateSearchCriteria searchTemplateSearchCriteria = new SearchTemplateSearchCriteria();
        searchTemplateSearchCriteria.setApplication(application);
        searchTemplateSearchCriteria.setIncludeGlobal(true);
        searchTemplateSearchCriteria.setUser("525824b8-d8ab-49cd-a165-68c451ae4839");

        String[] expectedIds = {
                "0bdd8a31-185a-4e13-9c4c-780ed1beec07",
                "9897dfbe-baa5-4d54-9e3b-6131f87684e1",
                "42c1cf75-e1c6-4d20-ab71-20422cdd224a",
                "d91532d1-a727-44e5-8ed6-ed72b1bec5fe",
        };

        // when
        Response response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .body(searchTemplateSearchCriteria)
                .header("Authorization", "bearer " + defaultValidToken)
                .post("/v1/searchTemplates/search/criteria");

        // then
        response.then().statusCode(200);

        List<SearchTemplateDTOv1> searchTemplateDTOv1s = Arrays.asList(response.as(SearchTemplateDTOv1[].class));

        assertEquals(4, searchTemplateDTOv1s.size());

        List<String> searchTemplateApplications = searchTemplateDTOv1s.stream()
                .map(SearchTemplateDTOv1::getApplication)
                .collect(Collectors.toList());
        assertTrue(searchTemplateApplications.contains(application));

        List<String> searchTemplateIds = searchTemplateDTOv1s.stream()
                .map(SearchTemplateDTOv1::getId)
                .collect(Collectors.toList());
        assertTrue(searchTemplateIds.containsAll(Arrays.asList(expectedIds)));
    }

    @Test
    @WithDBData(value = {"tkitportal-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    public void shouldFindByCriteriaGlobal() {
        // given
        String application = "support-tool-ui";
        SearchTemplateSearchCriteria searchTemplateSearchCriteria = new SearchTemplateSearchCriteria();
        searchTemplateSearchCriteria.setApplication(application);
        searchTemplateSearchCriteria.setIncludeGlobal(true);

        String[] expectedIds = {
                "0bdd8a31-185a-4e13-9c4c-780ed1beec07",
                "42c1cf75-e1c6-4d20-ab71-20422cdd224a",
                "d91532d1-a727-44e5-8ed6-ed72b1bec5fe",
        };

        // when
        Response response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .header("Authorization", "bearer " + defaultValidToken)
                .body(searchTemplateSearchCriteria)
                .post("/v1/searchTemplates/search/criteria");

        // then
        response.then().statusCode(200);

        List<SearchTemplateDTOv1> searchTemplateDTOv1s = Arrays.asList(response.as(SearchTemplateDTOv1[].class));

        assertEquals(3, searchTemplateDTOv1s.size());

        List<String> searchTemplateApplications = searchTemplateDTOv1s.stream()
                .map(SearchTemplateDTOv1::getApplication)
                .collect(Collectors.toList());
        assertTrue(searchTemplateApplications.contains(application));

        List<Boolean> searchTemplateGlobal = searchTemplateDTOv1s.stream()
                .map(SearchTemplateDTOv1::getGlobal)
                .collect(Collectors.toList());
        assertFalse(searchTemplateGlobal.contains(false));

        List<String> searchTemplateIds = searchTemplateDTOv1s.stream()
                .map(SearchTemplateDTOv1::getId)
                .collect(Collectors.toList());
        assertTrue(searchTemplateIds.containsAll(Arrays.asList(expectedIds)));
    }

    @Test
    @WithDBData(value = {"tkitportal-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    public void shouldFindByCriteriaUser() {
        // given
        SearchTemplateSearchCriteria searchTemplateSearchCriteria = new SearchTemplateSearchCriteria();
        searchTemplateSearchCriteria.setUser("525824b8-d8ab-49cd-a165-68c451ae4839");

        String[] expectedIds = {
                "9897dfbe-baa5-4d54-9e3b-6131f87684e1",
                "9a76c5fc-44dd-49e4-8fc0-c687881405bf"
        };

        // when
        Response response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .header("Authorization", "bearer " + defaultValidToken)
                .body(searchTemplateSearchCriteria)
                .post("/v1/searchTemplates/search/criteria");

        // then
        response.then().statusCode(200);

        List<SearchTemplateDTOv1> searchTemplateDTOv1s = Arrays.asList(response.as(SearchTemplateDTOv1[].class));

        assertEquals(2, searchTemplateDTOv1s.size());

        List<String> searchTemplateUsers = searchTemplateDTOv1s.stream()
                .map(SearchTemplateDTOv1::getUser)
                .collect(Collectors.toList());
        assertTrue(searchTemplateUsers.contains("525824b8-d8ab-49cd-a165-68c451ae4839"));

        List<String> searchTemplateIds = searchTemplateDTOv1s.stream()
                .map(SearchTemplateDTOv1::getId)
                .collect(Collectors.toList());
        assertTrue(searchTemplateIds.containsAll(Arrays.asList(expectedIds)));
    }

    @Test
    @WithDBData(value = {"tkitportal-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    public void shouldFindByCriteriaEmptyCriteria() {
        // given
        String application = "no-match-app";
        SearchTemplateSearchCriteria searchTemplateSearchCriteria = new SearchTemplateSearchCriteria();
        searchTemplateSearchCriteria.setApplication(application);

        // when
        Response response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .header("Authorization", "bearer " + defaultValidToken)
                .body(searchTemplateSearchCriteria)
                .post("/v1/searchTemplates/search/criteria");

        // then
        response.then().statusCode(200);

        List<SearchTemplateDTOv1> searchTemplateDTOv1s = Arrays.asList(response.as(SearchTemplateDTOv1[].class));
        assertTrue(searchTemplateDTOv1s.isEmpty());
    }

    @Test
    @WithDBData(value = {"tkitportal-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    public void shouldFindByCriteriaNoMatch() {
        // given
        SearchTemplateSearchCriteria searchTemplateSearchCriteria = new SearchTemplateSearchCriteria();

        // when
        Response response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .header("Authorization", "bearer " + defaultValidToken)
                .body(searchTemplateSearchCriteria)
                .post("/v1/searchTemplates/search/criteria");

        // then
        response.then().statusCode(200);

        List<SearchTemplateDTOv1> searchTemplateDTOv1s = Arrays.asList(response.as(SearchTemplateDTOv1[].class));

        assertEquals(15, searchTemplateDTOv1s.size());
    }

    @Test
    @WithDBData(value = {"tkitportal-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    public void shouldNotFindByCriteriaNullCriteria() {
        // given
        String expectedErrorCode = "BAD_REQUEST";

        // when
        Response response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .header("Authorization", "bearer " + defaultValidToken)
                .post("/v1/searchTemplates/search/criteria");

        // then
        response.then().statusCode(400);

        RestExceptionDTO restExceptionDTO = response.as(RestExceptionDTO.class);

        assertEquals(expectedErrorCode, restExceptionDTO.getErrorCode());
    }

    @Test
    @WithDBData(value = {"tkitportal-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    public void shouldGetDefaultSearchTemplate() {
        // given
        String application = "support-tool-ui";
        String page = "page1";
        String user = "89e93c2c-8fd5-48a0-81b2-8b988a7cb5cc";

        String expectedId = "0bdd8a31-185a-4e13-9c4c-780ed1beec07";
        String expectedCriteria = "{\"name\": \"name\",\"id\": \"id\",\"responsible\": \"responsible\"}";

        // when
        Response response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .header("Authorization", "bearer " + defaultValidToken)
                .get("/v1/searchTemplates/default/" + application +
                        "/" + page +
                        "/" + user);

        // then
        response.then().statusCode(200);

        SearchTemplateDTOv1 searchTemplateDTOv1 = response.as(SearchTemplateDTOv1.class);

        assertEquals(expectedId, searchTemplateDTOv1.getId());
        assertEquals(application, searchTemplateDTOv1.getApplication());
        assertEquals(user, searchTemplateDTOv1.getUser());
        assertEquals(true, searchTemplateDTOv1.isDefaultTemplate());
        assertEquals(expectedCriteria, searchTemplateDTOv1.getCriteriaAsJson());
    }

    @Test
    @WithDBData(value = {"tkitportal-testdata.xls"}, deleteBeforeInsert = true, deleteAfterTest = true)
    public void shouldNotGetDefaultSearchTemplateNotExists() {
        // given
        String application = "tkit-process-ui";
        String page = "page2";
        String user = "ce36cbec-3674-4d4a-ba83-9f7958c45d8d";

        // when
        Response response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .header("Authorization", "bearer " + defaultValidToken)
                .get("/v1/searchTemplates/default/" + application +
                        "/" + page +
                        "/" + user);

        // then
        response.then().statusCode(200);

        assertTrue(response.getBody().asString().isEmpty());
    }
}
