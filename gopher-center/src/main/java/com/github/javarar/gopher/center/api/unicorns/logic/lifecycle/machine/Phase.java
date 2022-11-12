package com.github.javarar.gopher.center.api.unicorns.logic.lifecycle.machine;

import java.util.function.Consumer;

public interface Phase<T> {
    Consumer<State<T>> command();
}
