package com.github.javarar.unicorns.welfare.impl.logic.repository;

import com.github.javarar.unicorns.welfare.api.logic.repository.ReportEntity;
import com.github.javarar.unicorns.welfare.api.logic.repository.ReportRepository;
import com.google.common.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class CachedReportRepository implements ReportRepository {

    // хранилише с данными
    private final Cache<UUID, ReportEntity> cache;

    @Override
    public ReportEntity findById(UUID id) {
        return cache.getIfPresent(id);
    }

    @Override
    public void save(ReportEntity entity) {
        if(entity.getId() == null)
            entity.setId(UUID.randomUUID());

        entity.setVersion(entity.getVersion() + 1);

        log.info("Создани или сохранение уже существующего единорога в хранилище { store = {}, entity = {} }", this, entity);
        cache.put(entity.getId(), entity);
    }

    @Override
    public Iterable<ReportEntity> findAll() {
        return cache.getAllPresent(cache.asMap().keySet()).values();
    }
}
