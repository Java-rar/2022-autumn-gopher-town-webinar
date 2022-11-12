package com.github.javarar.gopher.center.impl.unicorns.input;

import com.github.javarar.gopher.center.api.unicorns.input.dto.LittlePonyRequest;
import com.github.javarar.gopher.center.api.unicorns.input.dto.LittlePonyResponse;
import com.github.javarar.gopher.center.api.unicorns.logic.farm.UnicornFarm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("unicorns")
@RestController
@RequiredArgsConstructor
public class UnicornBoxOfficeWindow {

    private final UnicornFarm farm;

    /**
     * Свободная касса, принимаем запрос на выращивание единорога,
     * и на месте инициализируем процесс
     * <p>
     * У нас есть шаблон выведения единорога и жизненный цикл
     * по которому можнор вырастить настоящего
     *
     * @param request запрос на вырост единорога
     * @return ответ с уникальным идентификатором единорога
     */
    @PostMapping("create")
    public ResponseEntity<LittlePonyResponse> createUnicorn(@RequestBody LittlePonyRequest request) {
        String unicornID = farm.requestToUnicornBreed(request.getAttributes().getName());

        return ResponseEntity.ok(new LittlePonyResponse(unicornID));
    }

}
