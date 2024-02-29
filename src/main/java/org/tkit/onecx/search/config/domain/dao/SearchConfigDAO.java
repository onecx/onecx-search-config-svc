package org.tkit.onecx.search.config.domain.dao;

import static org.tkit.onecx.search.config.domain.dao.SearchConfigDAO.SearchTemplateErrorKey.FIND_SEARCH_CONFIGS_BY_CRITERIA_FAILED;
import static org.tkit.onecx.search.config.domain.dao.SearchConfigDAO.SearchTemplateErrorKey.FIND_SEARCH_CONFIG_FAILED;
import static org.tkit.quarkus.jpa.utils.QueryCriteriaUtil.addSearchStringPredicate;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.tkit.onecx.search.config.domain.criteria.SearchConfigCriteria;
import org.tkit.onecx.search.config.domain.models.SearchConfig;
import org.tkit.onecx.search.config.domain.models.SearchConfig_;
import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.jpa.exceptions.DAOException;

import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class SearchConfigDAO extends AbstractDAO<SearchConfig> {

    public PageResult<SearchConfig> findByApplicationAndPage(String productName, String application, String page) {

        try {
            CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<SearchConfig> criteriaQuery = criteriaBuilder.createQuery(SearchConfig.class);
            Root<SearchConfig> searchConfigRoot = criteriaQuery.from(SearchConfig.class);

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(searchConfigRoot.get(SearchConfig_.PRODUCT_NAME), productName));
            predicates.add(criteriaBuilder.equal(searchConfigRoot.get(SearchConfig_.APP_ID), application));
            predicates.add(criteriaBuilder.equal(searchConfigRoot.get(SearchConfig_.PAGE), page));

            if (!predicates.isEmpty()) {
                criteriaQuery.where(predicates.toArray(new Predicate[] {}));
            }

            return createPageQuery(criteriaQuery, Page.of(0, 100))
                    .getPageResult();
        } catch (Exception exception) {
            throw new DAOException(FIND_SEARCH_CONFIG_FAILED, exception);
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
            throw new DAOException(FIND_SEARCH_CONFIGS_BY_CRITERIA_FAILED, exception);
        }
    }

    public enum SearchTemplateErrorKey {
        /**
         * The find default search template failed.
         */
        FIND_SEARCH_CONFIG_FAILED,
        /**
         * The find search templates by criteria failed.
         */
        FIND_SEARCH_CONFIGS_BY_CRITERIA_FAILED
    }

}
