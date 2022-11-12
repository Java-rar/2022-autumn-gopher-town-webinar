package com.github.javarar.unicorns.welfare.impl.input;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javarar.unicorns.welfare.api.input.SurviveUnicornReport;
import com.github.javarar.unicorns.welfare.api.logic.commission.UnicornSurviveCommission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.concurrent.ExecutorService;

@Slf4j
@RequiredArgsConstructor
public class SurviveUnicornReportKafkaListener {

    // единорожек ростят все больше, мы должны быстро обрабатывать входящие запросы
    private final ExecutorService detach;

    // трансформировать сообщение в формат отчета
    private final ObjectMapper objectMapper;

    // комиссия отслеживающая, и в дальнейшем выносящия решения по состоянию единорожек
    private final UnicornSurviveCommission commission;

    @KafkaListener(topics = "unicorns-survive-policy", groupId = "us-policy")
    public void listenReport(@Payload String payload) {
        log.info("Пришел новый отчет о состоянии единорожки = {}", payload);

        detach.submit(() -> {
            try {
                final SurviveUnicornReport report = objectMapper.readValue(payload, SurviveUnicornReport.class);
                commission.acceptReport(report);
            } catch (Exception exception) {
                log.error("Не удалось обработать отчет = {}", payload);
            }
        });
    }
}
