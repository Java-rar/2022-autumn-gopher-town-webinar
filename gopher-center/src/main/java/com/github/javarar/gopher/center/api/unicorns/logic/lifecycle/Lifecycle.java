package com.github.javarar.gopher.center.api.unicorns.logic.lifecycle;

import com.github.javarar.gopher.center.api.unicorns.logic.lifecycle.machine.Phase;
import com.github.javarar.gopher.center.api.unicorns.logic.lifecycle.machine.State;

public interface Lifecycle<T> {

    /**
     *
     *
     * @param next
     */
    String start(T data, Phase<T> next);

    /**
     *
     *
     * @param next
     */
    void transition(String processID, Phase<T> next);

    /**
     *
     *
     */
    void end(String processID);

    /**
     *
     *
     * @param state
     */
    void save(State<T> state);
}
