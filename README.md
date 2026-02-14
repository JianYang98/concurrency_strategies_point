# 동시성 제어 5가지 전략

![Tests](https://github.com/JianYang98/concurrency_strategies_point/actions/workflows/test.yml/badge.svg)

Spring Boot 3.x + Java 17 + PostgreSQL 기반 동시성 제어 전략 구현 프로젝트

## 구현된 전략

| 전략 | 클래스 | 핵심 메커니즘 |
|------|--------|---------------|
| Synchronized | `SynchronizedPointService` | Java 내장 모니터 락 |
| ReentrantLock | `ReentrantLockPointService` | 명시적 락 제어 |
| 비관적 락 | `PessimisticPointService` | SELECT FOR UPDATE |
| 낙관적 락 | `OptimisticPointFacade` | @Version + 재시도 |
| 분산 락 | `DistributedPointFacade` | FakeRedisLock (ConcurrentHashMap 기반) |

## 테스트 실행

```bash
./gradlew test
```

모든 테스트: 10개 스레드가 동시에 1000원씩 충전 → 최종 잔액 10000원 검증
