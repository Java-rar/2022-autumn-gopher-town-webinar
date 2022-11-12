package com.github.javarar.gopher.center.impl.unicorns.logic.lifecycle;

import com.github.javarar.gopher.center.api.unicorns.logic.lifecycle.machine.Phase;
import com.github.javarar.gopher.center.api.unicorns.logic.lifecycle.Lifecycle;
import com.github.javarar.gopher.center.api.unicorns.logic.lifecycle.machine.State;
import com.github.javarar.gopher.center.api.unicorns.logic.lifecycle.machine.StateMachine;
import com.github.javarar.gopher.center.api.unicorns.output.report.SurviveUnicornReport;
import com.github.javarar.gopher.center.impl.unicorns.output.report.SurviveUnicornReportKafkaPublisher;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
public class SurviveUnicornCycle implements Lifecycle<UnicornData> {

    // сервис защиты животных
    private final SurviveUnicornReportKafkaPublisher surviveService;

    // машина состояний
    private final StateMachine<UnicornData> stateMachine;


    @Override
    public String start(UnicornData data, Phase<UnicornData> next) {
        final State<UnicornData> start = stateMachine.start(data, next);
        surviveNotification(start);
        return start.processID();
    }

    @Override
    public void transition(String processID, Phase<UnicornData> next) {
        stateMachine.transition(processID,next);
    }

    @Override
    public void end(String processID) {
        stateMachine.end(processID);
    }

    @Override
    public void save(State<UnicornData> state) {
        stateMachine.save(state);
        surviveNotification(state);
    }

    private void surviveNotification(State<UnicornData> state) {
        surviveService.surviveNotification(new SurviveUnicornReport(
                state.processID(),
                state.data().getLifecycle().name(),
                ThreadLocalRandom.current().nextInt(10)));
    }

    @Override
    public String toString() {
        return "Ферма по вырасту и охране коняшек";
    }
}
