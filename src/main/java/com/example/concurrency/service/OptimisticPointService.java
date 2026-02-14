package com.example.concurrency.service;

import com.example.concurrency.domain.Point;
import com.example.concurrency.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OptimisticPointService {

    private final PointRepository pointRepository;

    @Transactional
    public void charge(Long userId, Long amount) {
        Point point = pointRepository.findByUserIdWithOptimisticLock(userId)
                .orElseThrow(() -> new IllegalArgumentException("포인트 정보가 없습니다."));

        point.charge(amount);
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
