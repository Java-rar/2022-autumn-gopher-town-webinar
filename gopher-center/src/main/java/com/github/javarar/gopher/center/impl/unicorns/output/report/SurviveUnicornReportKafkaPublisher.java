package com.github.javarar.gopher.center.impl.unicorns.output.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javarar.gopher.center.api.unicorns.output.report.SurviveUnicornReport;
import com.github.javarar.gopher.center.api.unicorns.output.report.SurviveUnicornReportPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.ExecutorService;

@Slf4j
@RequiredArgsConstructor
public class SurviveUnicornReportKafkaPublisher implements SurviveUnicornReportPublisher {

    private final ExecutorService detach;
    private final ObjectMapper mapper;

    private final KafkaTemplate<String, String> sender;

    public void surviveNotification(SurviveUnicornReport report) {
        detach.submit(() -> {
            log.info("Отправляем уведомление в службу защиты единорогов { processID = {} }", report.getReportIdentifier());
            sender.send("unicorns-survive-policy", mapper.writeValueAsString(report));
            return Void.class;
        });
    }
}
