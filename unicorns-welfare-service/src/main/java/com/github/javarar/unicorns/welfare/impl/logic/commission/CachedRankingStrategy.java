package com.github.javarar.unicorns.welfare.impl.logic.commission;

import com.github.javarar.unicorns.welfare.api.input.SurviveUnicornReport;
import com.github.javarar.unicorns.welfare.api.logic.commission.RankingStrategy;
import com.github.javarar.unicorns.welfare.api.logic.commission.UnicornCabinet;
import com.google.common.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
public class CachedRankingStrategy implements RankingStrategy<UnicornCabinet, SurviveUnicornReport> {

    // хранилише с данными
    private final Cache<String, UnicornCabinet> cache;

    @Override
    public UnicornCabinet select(SurviveUnicornReport request, List<UnicornCabinet> variants) {
        try {
            return cache.get(request.getReportIdentifier(), () -> {
                int pointer = ThreadLocalRandom.current().nextInt(variants.size());
                return variants.get(pointer);
            });
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
