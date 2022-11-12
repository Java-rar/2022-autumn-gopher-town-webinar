package com.github.javarar.unicorns.welfare.impl.logic.commission;

import com.github.javarar.unicorns.welfare.api.input.SurviveUnicornReport;
import com.github.javarar.unicorns.welfare.api.logic.commission.AsyncGroupExecutionException;
import com.github.javarar.unicorns.welfare.api.logic.commission.DestroyTimeBoundException;
import com.github.javarar.unicorns.welfare.api.logic.commission.Unicorn;
import com.github.javarar.unicorns.welfare.api.logic.commission.UnicornCabinet;
import com.github.javarar.unicorns.welfare.api.logic.repository.ReportEntity;
import com.github.javarar.unicorns.welfare.api.logic.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class QueueUnicornCabinet implements UnicornCabinet, DisposableBean {

    private final String number;
    private final Unicorn owner;

    // один экзекьютор на все инстансы, чтобы не сломать концепцию работы с async group
    // асинхронная очередь для записи событий на обработку находится внутри ExecutorService
    private final ExecutorService thread
            = Executors.newSingleThreadExecutor();

    // текущий размер очереди
    private final AtomicInteger queueCount = new AtomicInteger();

    // максимально возможный размер очереди
    private final int queueCapacity;

    // барьер для того чтобы очередь не перегружалась и не забивалась выше нормы сообщениями
    private final Lock capacityBarrier;
    private final Condition capacityBarrierCondition;

    // время ожидания завершения executor'a
    private static final long EXECUTOR_TERMINATION_TIMEOUT = 600000L;

    // персистентное хранилище данных об отчетах единорогов
    private final ReportRepository repository;

    public QueueUnicornCabinet(String number, Unicorn owner, int queueCapacity, ReportRepository repository) {
        this.number = number;
        this.owner = owner;
        this.queueCapacity = queueCapacity;
        this.repository = repository;
        // инициализация барьера
        this.capacityBarrier = new ReentrantLock(false);
        this.capacityBarrierCondition = this.capacityBarrier.newCondition();
    }

    @Override
    public void acceptReport(SurviveUnicornReport unicornReport) {
        try (QueueCapacityBarrier barrier = new QueueCapacityBarrier()) {
            barrier.pushTask(() -> {
                log.info("запуск задачи по асинхронной обработке отчета о состоянии единорога в группе = {} { task = {} }", number, unicornReport);
                // Inserts the specified element at the tail of this queue. As the queue is unbounded, this method will never throw IllegalStateException or return false.
                thread.submit(new QueueCapacityTask(new ReportTask(unicornReport)));
                // постоянный вызов queue.size() может сильно-негативно влиять на производительность и отрабатывать долго
                queueCount.incrementAndGet();
            });
        }
    }

    public int queueSize() {
        return queueCount.get();
    }

    @Scheduled(fixedRateString = "${report.event.queue.monitoring.period:1000}")
    public void asyncGroupMonitoringTask() {
        log.info("Отслеживаем количество запросов в кабинете (чтобы стол не прогнулся) { active task count = {} }", queueCount.get());
    }

    @Override
    public void destroy() throws Exception {
        thread.shutdown();
        if (!thread.awaitTermination(EXECUTOR_TERMINATION_TIMEOUT, TimeUnit.MILLISECONDS))
            throw new DestroyTimeBoundException("не удалось корректно обработать очередь из событий отчетности о состоянии единорогов { queue_size = " + queueSize() + " }");
    }

    class QueueCapacityBarrier implements AutoCloseable {

        public void pushTask(Runnable task) {
            capacityBarrier.lock();

            try {
                while (queueSize() >= queueCapacity)
                    capacityBarrierCondition.await();

                task.run();
            } catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
                throw new AsyncGroupExecutionException(interruptedException);
            }
        }

        @Override
        public void close() {
            capacityBarrier.unlock();
        }
    }

    @RequiredArgsConstructor
    class QueueCapacityTask implements Runnable {
        // оригинальная задача для исполнения
        private final ReportTask task;

        @Override
        public void run() {
            try {
                task.run();

                // hold lock after task execution (не совсем правильно под траем вызывать лок)
                capacityBarrier.lock();
                // нотифицируем ожидающие потоки о том что можно проверить условие
                capacityBarrierCondition.signalAll();
            } finally {
                // уменьшаем кол-во задач на 1
                queueCount.decrementAndGet();
                // release lock after notification
                capacityBarrier.unlock();
            }
        }
    }

    @RequiredArgsConstructor
    private class ReportTask implements Runnable {

        private final SurviveUnicornReport unicornReport;

        @Override
        public void run() {
            log.info("Единорог ({} {{}}) из кабинета ({}) приступил к обработке отчета = {}", owner.getName(), owner.getGrade(), number, unicornReport);
            ReportEntity entity = convert(unicornReport);
            repository.save(entity);
            log.info("Единорог ({} {{}}) из кабинета ({}) завершил обработку отчета = {}", owner.getName(), owner.getGrade(), number, unicornReport);
        }

        private ReportEntity convert(SurviveUnicornReport unicornReport) {
            ReportEntity entity = new ReportEntity();
            entity.setReportIdentifier(unicornReport.getReportIdentifier());
            entity.setUnicornState(unicornReport.getUnicornState());
            entity.setAssessment(unicornReport.getAssessment());
            return entity;
        }

    }

    @Override
    public String cabinetNumber() {
        return number;
    }

    @Override
    public Unicorn cabinetOwner() {
        return owner;
    }
}
