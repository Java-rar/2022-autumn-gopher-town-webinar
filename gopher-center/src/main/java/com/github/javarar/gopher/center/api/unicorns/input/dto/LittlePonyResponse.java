package com.github.javarar.gopher.center.api.unicorns.input.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LittlePonyResponse {
    // уникальный идентификатор заявки на выпуск единорога
    private String tickedID;
}
