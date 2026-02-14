package com.example.concurrency.facade;

import com.example.concurrency.domain.Point;
import com.example.concurrency.lock.FakeRedisLock;
import com.example.concurrency.service.DistributedPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class DistributedPointFacade {

    private final FakeRedisLock fakeRedisLock;
    private final DistributedPointService distributedPointService;

    public void charge(Long userId, Long amount) {
        String lockKey = "point:" + userId;

        boolean acquired = fakeRedisLock.tryLock(lockKey, 10, 3, TimeUnit.SECONDS);
        if (!acquired) {
            throw new RuntimeException("락 획득 실패");
        }

        try {
            distributedPointService.charge(userId, amount);
        } finally {
            fakeRedisLock.unlock(lockKey);
        }
    }

    public void init(Long userId, Long amount) {
        distributedPointService.init(userId, amount);
    }

    public Point getPoint(Long userId) {
        return distributedPointService.getPoint(userId);
    }
}
