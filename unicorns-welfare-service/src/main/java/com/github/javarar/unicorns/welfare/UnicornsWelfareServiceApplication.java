package com.github.javarar.unicorns.welfare;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javarar.unicorns.welfare.api.logic.commission.Unicorn;
import com.github.javarar.unicorns.welfare.api.logic.commission.UnicornSurviveCommission;
import com.github.javarar.unicorns.welfare.api.logic.repository.ReportRepository;
import com.github.javarar.unicorns.welfare.impl.input.SurviveUnicornReportKafkaListener;
import com.github.javarar.unicorns.welfare.impl.logic.commission.CachedRankingStrategy;
import com.github.javarar.unicorns.welfare.impl.logic.commission.QueueUnicornCabinet;
import com.github.javarar.unicorns.welfare.impl.logic.commission.RankedUnicornSurviveCommission;
import com.github.javarar.unicorns.welfare.impl.logic.repository.CachedReportRepository;
import com.github.javarar.unicorns.welfare.impl.logic.repository.MultiSourceReportRepository;
import com.google.common.cache.CacheBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class UnicornsWelfareServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UnicornsWelfareServiceApplication.class, args);
    }

    @Bean
    public SurviveUnicornReportKafkaListener surviveUnicornReportKafkaListener(UnicornSurviveCommission commission) {
        return new SurviveUnicornReportKafkaListener(
                Executors.newFixedThreadPool(10),
                new ObjectMapper(),
                commission
        );
    }

    @Bean
    public UnicornSurviveCommission unicornSurviveCommission(ReportRepository repository) {
        return new RankedUnicornSurviveCommission(
                List.of(
                        new QueueUnicornCabinet(
                                "cabinet#1",
                                new Unicorn("Bear", "Очень важный единорог"),
                                100,
                                repository
                        ),
                        new QueueUnicornCabinet(
                                "cabinet#2",
                                new Unicorn("Работяга", "Единорог который делает всю работу"),
                                1000,
                                repository
                        ),
                        new QueueUnicornCabinet(
                                "cabinet#3",
                                new Unicorn("Стажер", "Молодой, но горячо любимый"),
                                500,
                                repository
                        )
                ),
                new CachedRankingStrategy(CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build())
        );
    }

    @Bean
    public ReportRepository reportRepository() {
        return new MultiSourceReportRepository(
                Executors.newFixedThreadPool(10),
                List.of(
                        new CachedReportRepository(CacheBuilder.newBuilder().expireAfterAccess(15, TimeUnit.MINUTES).build()),
                        new CachedReportRepository(CacheBuilder.newBuilder().expireAfterAccess(15, TimeUnit.MINUTES).build()),
                        new CachedReportRepository(CacheBuilder.newBuilder().expireAfterAccess(15, TimeUnit.MINUTES).build())
                )
        );
    }

}