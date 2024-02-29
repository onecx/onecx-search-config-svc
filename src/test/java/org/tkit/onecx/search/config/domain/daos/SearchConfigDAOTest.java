package org.tkit.onecx.search.config.domain.daos;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.tkit.onecx.search.config.test.AbstractTest;
import org.tkit.quarkus.jpa.exceptions.DAOException;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class SearchConfigDAOTest extends AbstractTest {

    @Inject
    SearchConfigDAO dao;

    @InjectMock
    EntityManager em;

    @BeforeEach
    void beforeAll() {
        Mockito.when(em.getCriteriaBuilder()).thenThrow(new RuntimeException("Test technical error exception"));
    }

    @Test
    void findSearchConfigByCriteriaTest() {
        methodExceptionTests(() -> dao.findBySearchCriteria(null),
                SearchConfigDAO.ErrorKeys.FIND_SEARCH_CONFIGS_BY_CRITERIA_FAILED);
        methodExceptionTests(() -> dao.findById(null),
                SearchConfigDAO.ErrorKeys.FIND_ENTITY_BY_ID_FAILED);
    }

    void methodExceptionTests(Executable fn, Enum<?> key) {
        var exc = Assertions.assertThrows(DAOException.class, fn);
        Assertions.assertEquals(key, exc.key);
    }
}
