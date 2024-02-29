package org.tkit.onecx.search.config.domain.daos;

import static org.tkit.quarkus.jpa.utils.QueryCriteriaUtil.addSearchStringPredicate;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.Predicate;

import org.tkit.onecx.search.config.domain.criteria.SearchConfigCriteria;
import org.tkit.onecx.search.config.domain.models.SearchConfig;
import org.tkit.onecx.search.config.domain.models.SearchConfig_;
import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.jpa.models.TraceableEntity_;

import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class SearchConfigDAO extends AbstractDAO<SearchConfig> {

    @Override
    public SearchConfig findById(Object id) throws DAOException {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(SearchConfig.class);
            var root = cq.from(SearchConfig.class);
            cq.where(cb.equal(root.get(TraceableEntity_.ID), id));
            return this.getEntityManager().createQuery(cq).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            throw new DAOException(ErrorKeys.FIND_ENTITY_BY_ID_FAILED, e, entityName, id);
        }
    }

    public PageResult<SearchConfig> findBySearchCriteria(SearchConfigCriteria searchCriteria) {

        try {
            var criteriaBuilder = getEntityManager().getCriteriaBuilder();
            var criteriaQuery = criteriaBuilder.createQuery(SearchConfig.class);
            var searchConfigRoot = criteriaQuery.from(SearchConfig.class);

            List<Predicate> predicates = new ArrayList<>();
            addSearchStringPredicate(predicates, criteriaBuilder, searchConfigRoot.get(SearchConfig_.PRODUCT_NAME),
                    searchCriteria.getProductName());
            addSearchStringPredicate(predicates, criteriaBuilder, searchConfigRoot.get(SearchConfig_.APP_ID),
                    searchCriteria.getAppId());
            addSearchStringPredicate(predicates, criteriaBuilder, searchConfigRoot.get(SearchConfig_.PAGE),
                    searchCriteria.getPage());
            addSearchStringPredicate(predicates, criteriaBuilder, searchConfigRoot.get(SearchConfig_.NAME),
                    searchCriteria.getName());

            if (!predicates.isEmpty()) {
                criteriaQuery.where(predicates.toArray(new Predicate[] {}));
            }

            return createPageQuery(criteriaQuery, Page.of(searchCriteria.getPageNumber(), searchCriteria.getPageSize()))
                    .getPageResult();
        } catch (Exception exception) {
            throw new DAOException(ErrorKeys.FIND_SEARCH_CONFIGS_BY_CRITERIA_FAILED, exception);
        }
    }

    public enum ErrorKeys {
        /**
         * The find search templates by criteria failed.
         */
        FIND_SEARCH_CONFIGS_BY_CRITERIA_FAILED,
        FIND_ENTITY_BY_ID_FAILED
    }

}
