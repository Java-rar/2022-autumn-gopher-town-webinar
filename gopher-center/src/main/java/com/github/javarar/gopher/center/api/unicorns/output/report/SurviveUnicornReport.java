package com.github.javarar.gopher.center.api.unicorns.output.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurviveUnicornReport {
    // уникальный идентификатор отчета
    private String reportIdentifier;

    // состояние текущего отчета
    private String unicornState;

    // from 1 to 10
    private int assessment;
}
