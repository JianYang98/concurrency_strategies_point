package com.example.concurrency.facade;

import com.example.concurrency.domain.Point;
import com.example.concurrency.service.OptimisticPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OptimisticPointFacade {

    private final OptimisticPointService optimisticPointService;

    public void charge(Long userId, Long amount) {
        int maxRetry = 100;
        int retryCount = 0;

        while (retryCount < maxRetry) {
            try {
                optimisticPointService.charge(userId, amount);
                return;
            } catch (Exception e) {
                if (isOptimisticLockException(e)) {
                    retryCount++;
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(ex);
                    }
                } else {
                    throw e;
                }
            }
        }
        throw new RuntimeException("재시도 횟수 초과");
    }

    private boolean isOptimisticLockException(Throwable e) {
        if (e == null) return false;
        if (e instanceof ObjectOptimisticLockingFailureException) return true;
        if (e instanceof org.hibernate.StaleObjectStateException) return true;
        return isOptimisticLockException(e.getCause());
    }

    public void init(Long userId, Long amount) {
        optimisticPointService.init(userId, amount);
    }

    public Point getPoint(Long userId) {
        return optimisticPointService.getPoint(userId);
    }
}
