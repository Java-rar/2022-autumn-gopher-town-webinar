package com.github.javarar.gopher.center;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javarar.gopher.center.impl.unicorns.logic.farm.SurviveUnicornFarm;
import com.github.javarar.gopher.center.impl.unicorns.logic.lifecycle.machine.CachedAsyncStateMachine;
import com.github.javarar.gopher.center.impl.unicorns.logic.lifecycle.SurviveUnicornCycle;
import com.github.javarar.gopher.center.api.unicorns.logic.farm.UnicornFarm;
import com.github.javarar.gopher.center.impl.unicorns.output.report.SurviveUnicornReportKafkaPublisher;
import com.github.javarar.gopher.center.api.unicorns.logic.lifecycle.Lifecycle;
import com.github.javarar.gopher.center.impl.unicorns.logic.lifecycle.UnicornData;
import com.google.common.cache.CacheBuilder;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.Executors;

@SpringBootApplication
public class GopherCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(GopherCenterApplication.class, args);
    }

    @Bean
    public UnicornFarm unicornFarm(Lifecycle<UnicornData> unicornDataLifecycle) {
        return new SurviveUnicornFarm(unicornDataLifecycle);
    }

    @Bean
    public Lifecycle<UnicornData> asyncUnicornLifeCycle(SurviveUnicornReportKafkaPublisher unicornService) {
        return new SurviveUnicornCycle(
                unicornService,
                new CachedAsyncStateMachine<>(
                        Executors.newFixedThreadPool(10),
                        CacheBuilder.newBuilder().build()
                )
        );
    }

    @Bean
    public SurviveUnicornReportKafkaPublisher unicornService(KafkaTemplate<String, String> kafkaTemplate) {
        return new SurviveUnicornReportKafkaPublisher(
                Executors.newFixedThreadPool(10),
                new ObjectMapper(),
                kafkaTemplate
        );
    }

    @Bean
    public NewTopic unicornSurvivePolicyTopic() {
        return TopicBuilder.name("unicorns-survive-policy")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
