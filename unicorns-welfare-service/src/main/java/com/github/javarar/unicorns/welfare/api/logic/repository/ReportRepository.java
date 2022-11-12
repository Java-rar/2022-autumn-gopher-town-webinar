package com.github.javarar.unicorns.welfare.api.logic.repository;

import java.util.UUID;

public interface ReportRepository {

    ReportEntity findById(UUID id);

    void save(ReportEntity entity);

    Iterable<ReportEntity> findAll();
}
