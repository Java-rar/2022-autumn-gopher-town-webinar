package com.github.javarar.gopher.center.impl.unicorns.logic;

import com.github.javarar.gopher.center.api.unicorns.logic.lifecycle.machine.Phase;
import com.github.javarar.gopher.center.api.unicorns.logic.lifecycle.Lifecycle;
import com.github.javarar.gopher.center.api.unicorns.logic.lifecycle.machine.State;
import com.github.javarar.gopher.center.impl.unicorns.logic.lifecycle.UnicornData;
import com.github.javarar.gopher.center.toolbox.ThreadAPI;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Единороги хоть очень полезные в быту бурундуков существа, особенно с их колониальным амбициями,
 * но всеже очень редкие
 * <p>
 * Класс единорога является управляемым и каждый следующий шаг жизненного цикла контролируется из вне. То есть
 * сам класс единорог отвечает за функциональность роста, разивития, тренировок и тд, но не за переходы между
 * этими состояниями
 * <p>
 * Класс жизненного цикла включает в себя поддержку перехода между состояниями, а также может реализовывать различные
 * функции связанные с отслеживанием жизненного цикла охраняемых существ
 * <p>
 * Для того чтобы начать выращивание единорога, требуется вызвать метод start()
 */
@Slf4j
public class GrowUnicorn {

    private final String name;
    private final Lifecycle<UnicornData> lifecycle;

    public GrowUnicorn(String name, Lifecycle<UnicornData> lifecycle) {
        this.name = name;
        this.lifecycle = lifecycle;
    }

    private UnicornData createUnicornData() {
        return new UnicornData()
                .setName(name)
                .setId(UUID.randomUUID().toString())
                .setLifecycle(UnicornData.State.CREATED);
    }

    public String start() {
        return lifecycle.start(createUnicornData(), new StartUnicornPhase());
    }

    void breed(State<UnicornData> state) {
        ThreadAPI.sleep(5, TimeUnit.SECONDS);
        log.info("Разводим коняшек... радился новый розовый единорог { name = {}, code = {} }!", name, state.processID());
        state.data().setLifecycle(UnicornData.State.BREED);

        transition(state, new BreedUnicornPhase());
    }

    void grow(State<UnicornData> state) {
        ThreadAPI.sleep(10, TimeUnit.SECONDS);
        log.info("Выращиваем коняшку, чтобы она была большой и сильной, кормим только отборными кормами {name = {}, code = {} }!", name, state.processID());
        state.data().setLifecycle(UnicornData.State.GROW);

        transition(state, new GrowUnicornPhase());
    }

    void training(State<UnicornData> state) {
        ThreadAPI.sleep(3, TimeUnit.SECONDS);
        log.info("Тренируем коняшку, чтобы она могла скакать по горным обрывам Марса { name = {}, code = {} }!", name, state.processID());
        state.data().setLifecycle(UnicornData.State.TRAINING);

        transition(state, new TrainingUnicornPhase());
    }

    void readyForAttestation(State<UnicornData> state) {
        state.data().setLifecycle(UnicornData.State.READY);

        lifecycle.save(state);
        lifecycle.end(state.processID());
    }

    // region что-то страшное

    private void transition(State<UnicornData> state, Phase<UnicornData> phase) {
        lifecycle.transition(state.processID(), new AutoSavePhase(state, phase));
    }

    public class StartUnicornPhase implements Phase<UnicornData> {
        @Override
        public Consumer<State<UnicornData>> command() {
            return GrowUnicorn.this::breed;
        }
    }

    public class BreedUnicornPhase implements Phase<UnicornData> {
        @Override
        public Consumer<State<UnicornData>> command() {
            return GrowUnicorn.this::grow;
        }
    }

    public class GrowUnicornPhase implements Phase<UnicornData> {
        @Override
        public Consumer<State<UnicornData>> command() {
            return GrowUnicorn.this::training;
        }
    }

    public class TrainingUnicornPhase implements Phase<UnicornData> {
        @Override
        public Consumer<State<UnicornData>> command() {
            return GrowUnicorn.this::readyForAttestation;
        }
    }

    public class AutoSavePhase implements Phase<UnicornData> {

        private final Phase<UnicornData> origin;

        public AutoSavePhase(State<UnicornData> state, Phase<UnicornData> origin) {
            this.origin = origin;
            lifecycle.save(state);
        }

        @Override
        public Consumer<State<UnicornData>> command() {
            return origin.command();
        }
    }

    // endregion
}
