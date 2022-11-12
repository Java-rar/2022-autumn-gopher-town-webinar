package com.github.javarar.unicorns.welfare.impl.logic.repository;

import com.github.javarar.unicorns.welfare.api.logic.repository.EmptyResultSet;
import com.github.javarar.unicorns.welfare.api.logic.repository.ReportEntity;
import com.github.javarar.unicorns.welfare.api.logic.repository.ReportRepository;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;

@RequiredArgsConstructor
public class MultiSourceReportRepository implements ReportRepository {

    private final ExecutorService parallel;
    private final List<ReportRepository> repositories;

    @Override
    public ReportEntity findById(UUID id) {
        return find(repository -> List.of(repository.findById(id))).iterator().next();
    }

    @Override
    public void save(ReportEntity entity) {
        for (ReportRepository repository : repositories) {
            CompletableFuture.runAsync(() -> repository.save(entity), parallel);
        }
    }

    @Override
    public Iterable<ReportEntity> findAll() {
        return find(ReportRepository::findAll);
    }

    public Iterable<ReportEntity> find(Function<ReportRepository,Iterable<ReportEntity>> execute) {
        try {
            // точка синхронизации
            final CompletableFuture<Iterable<ReportEntity>> completion = new CompletableFuture<>(); // (0)
            // счетчик выполненых задач - нужен для определения точки выхода при последнем отказе
            final AtomicInteger failOverCounter = new AtomicInteger();
            // признак запроса с пустым ответом
            final AtomicBoolean emptyCompletion = new AtomicBoolean();

            for (ReportRepository repository : repositories) {
                CompletableFuture.supplyAsync(() -> execute.apply(repository), parallel) // (1) отдельная под каждый repository
                        .whenComplete(new Completion<>(completion, failOverCounter, emptyCompletion, repositories.size()));
            }

            return completion.get(); // ждем пока кто-то взовет complete
        } catch (Exception exception) {
            // log
            throw new RuntimeException(exception);
        }
    }


    @RequiredArgsConstructor
    private static class Completion<T> implements BiConsumer<Iterable<T>, Throwable> {

        // точка синхронизации
        private final CompletableFuture<Iterable<T>> completion;

        // счетчик выполненых задач - нужен для определения точки выхода при последнем отказе
        private final AtomicInteger failOverCounter;

        // признак запроса с пустым ответом
        private final AtomicBoolean emptyCompletion;

        // верхний порог отказов
        private final int taskLimitUpperBound;

        @Override
        public void accept(Iterable<T> result, Throwable throwable) {
            if(throwable == null){
                // если результат исполнения завершился без ошибок, то отправляем ответ в синхронизатор
                completion.complete(result);
            } else {
                // ТУТ УЖЕ ОШИБКА КАК НЕ КРУТИ

                boolean localEmptyCompletion = false;

                // если запрос завершился с ошибкой "нет данных" - то сохраним признак и побождем остальных
                if (EmptyResultSet.class.isAssignableFrom(throwable.getClass())) {
                    localEmptyCompletion = true;
                    emptyCompletion.set(true);
                }

                if(failOverCounter.incrementAndGet() == taskLimitUpperBound) {
                    if(localEmptyCompletion || emptyCompletion.get()) {
                        completion.complete(Collections.emptyList());
                    } else {
                        completion.completeExceptionally(throwable);
                    }
                }

            }
        }
    }
}
