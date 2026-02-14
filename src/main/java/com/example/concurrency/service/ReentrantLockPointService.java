package com.example.concurrency.service;

import com.example.concurrency.domain.Point;
import com.example.concurrency.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class ReentrantLockPointService {

    private final PointRepository pointRepository;
    private final ReentrantLock lock = new ReentrantLock();

    public void charge(Long userId, Long amount) {
        lock.lock();
        try {
            Point point = pointRepository.findByUserId(userId)
                    .orElseThrow(() -> new IllegalArgumentException("포인트 정보가 없습니다."));

            point.charge(amount);
            pointRepository.saveAndFlush(point);
        } finally {
            lock.unlock();
        }
    }

    @Transactional
    public void init(Long userId, Long amount) {
        Point point = new Point(userId, amount);
        pointRepository.save(point);
    }

    @Transactional(readOnly = true)
    public Point getPoint(Long userId) {
        return pointRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("포인트 정보가 없습니다."));
    }
}
