package com.github.javarar.gopher.center.api.unicorns.input.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class LittlePonyRequest {

    // уникальный идентификатор запроса на выпуск единорога
    private UUID requestID;

    // атрибуты единорога, по которым будем выращивать коняшку
    private UnicornAttributes attributes;

    @Data
    public static class UnicornAttributes {
        // ...
        private String name;
    }
}
