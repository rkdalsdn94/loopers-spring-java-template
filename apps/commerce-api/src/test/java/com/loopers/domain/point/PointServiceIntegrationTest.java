package com.loopers.domain.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import com.loopers.infrastructure.point.PointHistoryJpaRepository;
import com.loopers.infrastructure.point.PointJpaRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

@SpringBootTest
class PointServiceIntegrationTest {
    @Autowired
    private PointService pointService;

    @SpyBean
    private PointRepository pointRepository;

    @SpyBean
    private PointHistoryRepository pointHistoryRepository;

    @Autowired
    private PointJpaRepository pointJpaRepository;

    @Autowired
    private PointHistoryJpaRepository pointHistoryJpaRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("포인트 초기화 시")
    @Nested
    class InitializePoint {
        @DisplayName("포인트 초기화 시, Point 저장이 수행된다.")
        @Test
        void savesPoint_whenInitialize() {
            // given
            String userId = "user123";

            // when
            Point saved = pointService.initializePoint(userId);

            // then - spy 검증
            verify(pointRepository).save(saved);

            // then - 저장 확인
            assertAll(
                () -> assertThat(saved).isNotNull(),
                () -> assertThat(saved.getUserId()).isEqualTo(userId),
                () -> assertThat(saved.getBalance()).isEqualTo(BigDecimal.ZERO),
                () -> assertThat(saved.getId()).isNotNull()
            );
        }

        @DisplayName("이미 초기화된 유저의 포인트를 초기화하면, 실패한다.")
        @Test
        void throwsException_whenAlreadyInitialized() {
            // given
            String userId = "user123";
            Point existingPoint = Point.builder()
                .userId(userId)
                .balance(BigDecimal.ZERO)
                .build();
            pointJpaRepository.save(existingPoint);

            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                pointService.initializePoint(userId)
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @DisplayName("포인트 조회 시")
    @Nested
    class GetPoint {
        @DisplayName("해당 유저의 포인트가 존재할 경우, 포인트 정보가 반환된다.")
        @Test
        void returnsPoint_whenPointExists() {
            // given
            String userId = "user123";
            Point point = Point.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(1000))
                .build();
            pointJpaRepository.save(point);

            // when
            Point found = pointService.getPoint(userId);

            // then
            assertAll(
                () -> assertThat(found).isNotNull(),
                () -> assertThat(found.getUserId()).isEqualTo(userId),
                () -> assertThat(found.getBalance()).isEqualTo(BigDecimal.valueOf(1000))
            );
        }

        @DisplayName("해당 유저의 포인트가 존재하지 않을 경우, 예외가 발생한다.")
        @Test
        void throwsException_whenPointNotExists() {
            // given
            String userId = "nonexistent";

            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                pointService.getPoint(userId)
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }

    @DisplayName("포인트 잔액 조회 시")
    @Nested
    class GetBalance {
        @DisplayName("해당 유저의 포인트가 존재할 경우, 잔액이 반환된다.")
        @Test
        void returnsBalance_whenPointExists() {
            // given
            String userId = "user123";
            Point point = Point.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(5000))
                .build();
            pointJpaRepository.save(point);

            // when
            BigDecimal balance = pointService.getBalance(userId);

            // then
            assertThat(balance).isEqualTo(BigDecimal.valueOf(5000));
        }

        @DisplayName("해당 유저의 포인트가 존재하지 않을 경우, 예외가 발생한다.")
        @Test
        void throwsException_whenPointNotExists() {
            // given
            String userId = "nonexistent";

            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                pointService.getBalance(userId)
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }

    @DisplayName("포인트 충전 시")
    @Nested
    class ChargePoint {
        @DisplayName("포인트 충전 시, 잔액이 증가하고 히스토리가 저장된다.")
        @Test
        void increasesBalanceAndSavesHistory_whenCharge() {
            // given
            String userId = "user123";
            Point point = Point.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(1000))
                .build();
            pointJpaRepository.save(point);

            // when
            BigDecimal newBalance = pointService.chargePoint(userId, BigDecimal.valueOf(500));

            // then - 잔액 확인
            assertThat(newBalance).isEqualTo(BigDecimal.valueOf(1500));

            // then - 히스토리 확인
            List<PointHistory> histories = pointHistoryJpaRepository.findByUserIdOrderByCreatedAtDesc(
                userId);
            assertAll(
                () -> assertThat(histories).hasSize(1),
                () -> assertThat(histories.get(0).getTransactionType()).isEqualTo(
                    PointTransactionType.CHARGE),
                () -> assertThat(histories.get(0).getAmount()).isEqualTo(BigDecimal.valueOf(500)),
                () -> assertThat(histories.get(0).getBalanceAfter()).isEqualTo(
                    BigDecimal.valueOf(1500))
            );
        }

        @DisplayName("포인트가 존재하지 않으면, 예외가 발생한다.")
        @Test
        void throwsException_whenPointNotExists() {
            // given
            String userId = "nonexistent";

            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                pointService.chargePoint(userId, BigDecimal.valueOf(500))
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }

    @DisplayName("포인트 사용 시")
    @Nested
    class UsePoint {
        @DisplayName("포인트 사용 시, 잔액이 감소하고 히스토리가 저장된다.")
        @Test
        void decreasesBalanceAndSavesHistory_whenUse() {
            // given
            String userId = "user123";
            Point point = Point.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(1000))
                .build();
            pointJpaRepository.save(point);

            // when
            BigDecimal newBalance = pointService.usePoint(userId, BigDecimal.valueOf(300));

            // then - 잔액 확인
            assertThat(newBalance).isEqualTo(BigDecimal.valueOf(700));

            // then - 히스토리 확인
            List<PointHistory> histories = pointHistoryJpaRepository.findByUserIdOrderByCreatedAtDesc(
                userId);
            assertAll(
                () -> assertThat(histories).hasSize(1),
                () -> assertThat(histories.get(0).getTransactionType()).isEqualTo(
                    PointTransactionType.USE),
                () -> assertThat(histories.get(0).getAmount()).isEqualTo(BigDecimal.valueOf(300)),
                () -> assertThat(histories.get(0).getBalanceAfter()).isEqualTo(
                    BigDecimal.valueOf(700))
            );
        }

        @DisplayName("잔액이 부족하면, 예외가 발생한다.")
        @Test
        void throwsException_whenBalanceInsufficient() {
            // given
            String userId = "user123";
            Point point = Point.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(500))
                .build();
            pointJpaRepository.save(point);

            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                pointService.usePoint(userId, BigDecimal.valueOf(1000))
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @DisplayName("포인트 환불 시")
    @Nested
    class RefundPoint {
        @DisplayName("포인트 환불 시, 잔액이 증가하고 히스토리가 저장된다.")
        @Test
        void increasesBalanceAndSavesHistory_whenRefund() {
            // given
            String userId = "user123";
            Point point = Point.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(1000))
                .build();
            pointJpaRepository.save(point);

            // when
            BigDecimal newBalance = pointService.refundPoint(userId, BigDecimal.valueOf(200));

            // then - 잔액 확인
            assertThat(newBalance).isEqualTo(BigDecimal.valueOf(1200));

            // then - 히스토리 확인
            List<PointHistory> histories = pointHistoryJpaRepository.findByUserIdOrderByCreatedAtDesc(
                userId);
            assertAll(
                () -> assertThat(histories).hasSize(1),
                () -> assertThat(histories.get(0).getTransactionType()).isEqualTo(
                    PointTransactionType.REFUND),
                () -> assertThat(histories.get(0).getAmount()).isEqualTo(BigDecimal.valueOf(200)),
                () -> assertThat(histories.get(0).getBalanceAfter()).isEqualTo(
                    BigDecimal.valueOf(1200))
            );
        }
    }

    @DisplayName("포인트 히스토리 조회 시")
    @Nested
    class GetPointHistories {
        @DisplayName("여러 거래 내역이 있으면, 최신순으로 반환된다.")
        @Test
        void returnsHistories_inDescendingOrder() {
            // given
            String userId = "user123";
            Point point = Point.builder()
                .userId(userId)
                .balance(BigDecimal.ZERO)
                .build();
            pointJpaRepository.save(point);

            // 충전, 사용, 환불 순서로 거래
            pointService.chargePoint(userId, BigDecimal.valueOf(1000));
            pointService.usePoint(userId, BigDecimal.valueOf(300));
            pointService.refundPoint(userId, BigDecimal.valueOf(100));

            // when
            List<PointHistory> histories = pointService.getPointHistories(userId);

            // then
            assertAll(
                () -> assertThat(histories).hasSize(3),
                () -> assertThat(histories.get(0).getTransactionType()).isEqualTo(
                    PointTransactionType.REFUND),
                () -> assertThat(histories.get(1).getTransactionType()).isEqualTo(
                    PointTransactionType.USE),
                () -> assertThat(histories.get(2).getTransactionType()).isEqualTo(
                    PointTransactionType.CHARGE)
            );
        }

        @DisplayName("거래 내역이 없으면, 빈 리스트가 반환된다.")
        @Test
        void returnsEmptyList_whenNoHistory() {
            // given
            String userId = "user123";

            // when
            List<PointHistory> histories = pointService.getPointHistories(userId);

            // then
            assertThat(histories).isEmpty();
        }
    }
}
