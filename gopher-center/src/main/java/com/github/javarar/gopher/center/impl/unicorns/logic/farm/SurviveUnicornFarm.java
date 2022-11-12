package com.github.javarar.gopher.center.impl.unicorns.logic.farm;

import com.github.javarar.gopher.center.api.unicorns.logic.lifecycle.Lifecycle;
import com.github.javarar.gopher.center.api.unicorns.logic.farm.UnicornFarm;
import com.github.javarar.gopher.center.impl.unicorns.logic.GrowUnicorn;
import com.github.javarar.gopher.center.impl.unicorns.logic.lifecycle.UnicornData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SurviveUnicornFarm implements UnicornFarm {

    private final Lifecycle<UnicornData> asyncUnicornsLifecycle;

    @Override
    public String requestToUnicornBreed(String name) {
        log.info("Запрос на выращивание коняшки { name = {}, lifecycle = '{}' }", name, asyncUnicornsLifecycle);
        final GrowUnicorn unicorn = new GrowUnicorn(name, asyncUnicornsLifecycle);

        // стартуем асинхронный процесс выращивание
        return unicorn.start();
    }
}
