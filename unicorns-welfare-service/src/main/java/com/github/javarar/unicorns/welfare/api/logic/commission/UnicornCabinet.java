package com.github.javarar.unicorns.welfare.api.logic.commission;

import com.github.javarar.unicorns.welfare.api.input.SurviveUnicornReport;

public interface UnicornCabinet {

    String cabinetNumber();

    Unicorn cabinetOwner();

    void acceptReport(SurviveUnicornReport unicornReport);
}
