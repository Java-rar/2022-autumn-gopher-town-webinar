package com.github.javarar.gopher.center.api.unicorns.logic.lifecycle.machine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CommonState<T> implements State<T> {

    // уникальный идентификатор отчета
    private String processID;
    // данные
    private T data;

    @Override
    public String processID() {
        return processID;
    }

    @Override
    public T data() {
        return data;
    }
}
