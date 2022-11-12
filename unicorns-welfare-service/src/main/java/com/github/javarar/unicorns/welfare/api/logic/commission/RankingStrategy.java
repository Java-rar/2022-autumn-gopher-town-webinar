package com.github.javarar.unicorns.welfare.api.logic.commission;

import java.util.List;

public interface RankingStrategy<T, R> {

    T select(R request, List<T> variants);
}
