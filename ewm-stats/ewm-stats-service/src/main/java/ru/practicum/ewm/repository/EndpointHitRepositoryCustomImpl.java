package ru.practicum.ewm.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.entity.QEndpointHit;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;


@Repository
public class EndpointHitRepositoryCustomImpl implements EndpointHitRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ViewStatsDto> getViewsStats(String[] uris, BooleanBuilder predicates) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QEndpointHit endpointHit = QEndpointHit.endpointHit;

        List<ViewStatsDto> stats = queryFactory.select(Projections.bean(
                ViewStatsDto.class, endpointHit.app, endpointHit.uri, endpointHit.ip.count().as("hits")))
                .from(endpointHit)
                .where(predicates)
                .groupBy(endpointHit.app, endpointHit.uri)
                .orderBy(endpointHit.ip.count().desc())
                .fetch();

        return stats;
    }

    @Override
    public List<ViewStatsDto> getUniqueViewsStats(String[] uris, BooleanBuilder predicates) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QEndpointHit endpointHit = QEndpointHit.endpointHit;

        List<ViewStatsDto> stats = queryFactory.select(Projections.bean(
                        ViewStatsDto.class, endpointHit.app, endpointHit.uri, endpointHit.ip.countDistinct().as("hits")))
                .from(endpointHit)
                .where(predicates)
                .groupBy(endpointHit.app, endpointHit.uri)
                .orderBy(endpointHit.ip.countDistinct().desc())
                .fetch();

        return stats;
    }
}
