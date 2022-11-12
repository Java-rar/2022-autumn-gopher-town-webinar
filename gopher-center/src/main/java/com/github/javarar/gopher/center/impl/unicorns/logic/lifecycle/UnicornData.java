package com.github.javarar.gopher.center.impl.unicorns.logic.lifecycle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UnicornData {

    private String id;
    private String name;
    private State lifecycle;

    @AllArgsConstructor
    public enum State {
        CREATED,
        BREED,
        GROW,
        TRAINING,
        READY
    }
}
