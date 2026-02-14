package com.example.concurrency.service;

import com.example.concurrency.domain.Point;
import com.example.concurrency.facade.DistributedPointFacade;
import com.example.concurrency.repository.PointRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DistributedPointServiceTest {

    @Autowired
    private DistributedPointFacade distributedPointFacade;

    @Autowired
    private PointRepository pointRepository;

    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        distributedPointFacade.init(userId, 0L);
    }

    @AfterEach
    void tearDown() {
        pointRepository.deleteAll();
    }

    @Test
    @DisplayName("분산 락 (FakeRedisLock) - 10개 스레드가 동시에 1000원씩 충전하면 최종 잔액은 10000원이다")
    void concurrentCharge() throws InterruptedException {
        int threadCount = 10;
        Long chargeAmount = 1000L;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    distributedPointFacade.charge(userId, chargeAmount);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        Point point = distributedPointFacade.getPoint(userId);
        assertThat(point.getAmount()).isEqualTo(10000L);
    }
}
