package com.github.javarar.gopher.center.api.unicorns.logic.lifecycle.machine;

public interface StateMachine<T> {

    /**
     *
     *
     * @param next
     */
    State<T> start(T data, Phase<T> next);

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
