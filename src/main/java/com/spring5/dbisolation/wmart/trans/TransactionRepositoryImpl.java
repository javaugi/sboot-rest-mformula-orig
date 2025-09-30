/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart.trans;

// import com.spring5.dbisolation.wmart.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class TransactionRepositoryImpl implements TransactionRepositoryCustom {

    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    public TransactionRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    @Override
    public Page<Transaction> findByCriteria(TransactionQueryCriteria criteria, Pageable pageable) {
        CriteriaQuery<Transaction> query = criteriaBuilder.createQuery(Transaction.class);
        Root<Transaction> root = query.from(Transaction.class);

        // Build predicates dynamically
        List<Predicate> predicates = buildPredicates(criteria, root);
        query.where(predicates.toArray(new Predicate[0]));

        // Add ordering
        if (pageable.getSort().isSorted()) {
            List<Order> orders = new ArrayList<>();
            pageable
                    .getSort()
                    .forEach(
                            order -> {
                                if (order.isAscending()) {
                                    orders.add(criteriaBuilder.asc(root.get(order.getProperty())));
                                } else {
                                    orders.add(criteriaBuilder.desc(root.get(order.getProperty())));
                                }
                            });
            query.orderBy(orders);
        }

        // Execute query with pagination
        TypedQuery<Transaction> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Transaction> result = typedQuery.getResultList();

        // Get total count for pagination
        Long total = getTotalCount(criteria);

        return new PageImpl<>(result, pageable, total);
    }

    private List<Predicate> buildPredicates(
            TransactionQueryCriteria criteria, Root<Transaction> root) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(criteriaBuilder.equal(root.get("userId"), criteria.getUserId()));

        if (criteria.getStartDate() != null) {
            predicates.add(
                    criteriaBuilder.greaterThanOrEqualTo(
                            root.get("createdAt").as(LocalDate.class), criteria.getStartDate()));
        }

        if (criteria.getEndDate() != null) {
            predicates.add(
                    criteriaBuilder.lessThanOrEqualTo(
                            root.get("createdAt").as(LocalDate.class), criteria.getEndDate()));
        }

        if (criteria.getType() != null) {
            predicates.add(criteriaBuilder.equal(root.get("type"), criteria.getType()));
        }

        return predicates;
    }

    private Long getTotalCount(TransactionQueryCriteria criteria) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Transaction> root = countQuery.from(Transaction.class);

        List<Predicate> predicates = buildPredicates(criteria, root);
        countQuery.select(criteriaBuilder.count(root));
        countQuery.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
