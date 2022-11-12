package com.github.javarar.unicorns.welfare.api.logic.repository;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ReportEntity {

    private UUID id;

    // уникальный идентификатор отчета
    private String reportIdentifier;

    // состояние текущего отчета
    private String unicornState;

    // from 1 to 10
    private int assessment;

    private int version;
}
