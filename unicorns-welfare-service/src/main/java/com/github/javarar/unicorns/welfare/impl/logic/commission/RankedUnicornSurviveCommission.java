package com.github.javarar.unicorns.welfare.impl.logic.commission;

import com.github.javarar.unicorns.welfare.api.input.SurviveUnicornReport;
import com.github.javarar.unicorns.welfare.api.logic.commission.RankingStrategy;
import com.github.javarar.unicorns.welfare.api.logic.commission.UnicornCabinet;
import com.github.javarar.unicorns.welfare.api.logic.commission.UnicornSurviveCommission;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Здесь сидят серьезные единороги в костюмах и разбирают поток отчетов
 * <p>
 * Каждый единорог в костюме сидит в отдельном кабинете, если заявка пок акому-то единорогу пришла в кабинет #1,
 * то и все остальные должны обработаться в нем!
 */
@RequiredArgsConstructor
public class RankedUnicornSurviveCommission implements UnicornSurviveCommission {

    // кабинеты, доступные сегодня
    private final List<UnicornCabinet> cabinets;

    // стратегия по выбору кабинета
    private final RankingStrategy<UnicornCabinet, SurviveUnicornReport> rankingStrategy;

    @Override
    public void acceptReport(SurviveUnicornReport unicornReport) {
        final UnicornCabinet selectedCabinet = rankingStrategy.select(unicornReport, cabinets);
        selectedCabinet.acceptReport(unicornReport);
    }
}
