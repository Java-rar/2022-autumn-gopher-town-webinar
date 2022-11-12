package com.github.javarar.gopher.center.impl.unicorns.logic.lifecycle.machine;

import com.github.javarar.gopher.center.api.unicorns.logic.lifecycle.machine.CommonState;
import com.github.javarar.gopher.center.api.unicorns.logic.lifecycle.machine.Phase;
import com.github.javarar.gopher.center.api.unicorns.logic.lifecycle.machine.State;
import com.github.javarar.gopher.center.api.unicorns.logic.lifecycle.machine.StateMachine;
import com.google.common.cache.Cache;
import lombok.RequiredArgsConstructor;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor
public class CachedAsyncStateMachine<T>  implements StateMachine<T> {

    // технические поля для поддержки жизненного цикла животных
    private final ExecutorService machineExecutor;
    private final Cache<String, State<T>> cache;

    @Override
    public State<T> start(T data, Phase<T> next) {
        final State<T> state = createNewProcess(data);
        fireAndForget(next,state); //
        return state;
    }

    @Override
    public void transition(String processID, Phase<T> next) {
        final State<T> state = cache.getIfPresent(processID);
        fireAndForget(next,state);
    }

    @Override
    public void end(String processID) {
        cache.invalidate(processID);
    }

    @Override
    public void save(State<T> state) {
        cache.put(state.processID(), state);
    }

    private void fireAndForget(Phase<T> next, State<T> currentState) {
        CompletableFuture.runAsync(() -> next.command().accept(currentState), machineExecutor);
    }

    private State<T> createNewProcess(T data) {
        final String processID = UUID.randomUUID().toString();
        final State<T> report = new CommonState<>(processID,data);
        save(report);
        return report;
    }
}
