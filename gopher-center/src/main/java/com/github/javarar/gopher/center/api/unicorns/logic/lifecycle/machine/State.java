package com.github.javarar.gopher.center.api.unicorns.logic.lifecycle.machine;

public interface State<T> {

    /**
     *
     * @return
     */
    String processID();

    /**
     *
     * @return
     */
    T data();
}
