package com.loopers.domain.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("포인트 거래 내역")
class PointHistoryTest {

    @DisplayName("PointHistory 객체 생성 시")
    @Nested
    class Create {
        @DisplayName("유효한 정보가 주어지면, 정상적으로 생성된다.")
        @Test
        void createsPointHistory_whenValidInfoProvided() {
            // given
            String userId = "user123";
            PointTransactionType transactionType = PointTransactionType.CHARGE;
            BigDecimal amount = BigDecimal.valueOf(1000);
            BigDecimal balanceAfter = BigDecimal.valueOf(2000);
            String description = "포인트 충전";

            // when
            PointHistory actual = PointHistory.builder()
                .userId(userId)
                .transactionType(transactionType)
                .amount(amount)
                .balanceAfter(balanceAfter)
                .description(description)
                .build();

            // then
            assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getUserId()).isEqualTo(userId),
                () -> assertThat(actual.getTransactionType()).isEqualTo(transactionType),
                () -> assertThat(actual.getAmount()).isEqualTo(amount),
                () -> assertThat(actual.getBalanceAfter()).isEqualTo(balanceAfter),
                () -> assertThat(actual.getDescription()).isEqualTo(description)
            );
        }

        @DisplayName("userId가 null이면, 실패한다.")
        @Test
        void throwsException_whenUserIdIsNull() {
            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                PointHistory.builder()
                    .userId(null)
                    .transactionType(PointTransactionType.CHARGE)
                    .amount(BigDecimal.valueOf(1000))
                    .balanceAfter(BigDecimal.valueOf(2000))
                    .build()
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("userId가 빈 문자열이면, 실패한다.")
        @Test
        void throwsException_whenUserIdIsBlank() {
            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                PointHistory.builder()
                    .userId("")
                    .transactionType(PointTransactionType.CHARGE)
                    .amount(BigDecimal.valueOf(1000))
                    .balanceAfter(BigDecimal.valueOf(2000))
                    .build()
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("transactionType이 null이면, 실패한다.")
        @Test
        void throwsException_whenTransactionTypeIsNull() {
            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                PointHistory.builder()
                    .userId("user123")
                    .transactionType(null)
                    .amount(BigDecimal.valueOf(1000))
                    .balanceAfter(BigDecimal.valueOf(2000))
                    .build()
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("amount가 0이면, 실패한다.")
        @Test
        void throwsException_whenAmountIsZero() {
            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                PointHistory.builder()
                    .userId("user123")
                    .transactionType(PointTransactionType.CHARGE)
                    .amount(BigDecimal.ZERO)
                    .balanceAfter(BigDecimal.valueOf(2000))
                    .build()
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("amount가 음수이면, 실패한다.")
        @Test
        void throwsException_whenAmountIsNegative() {
            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                PointHistory.builder()
                    .userId("user123")
                    .transactionType(PointTransactionType.CHARGE)
                    .amount(BigDecimal.valueOf(-100))
                    .balanceAfter(BigDecimal.valueOf(2000))
                    .build()
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("balanceAfter가 음수이면, 실패한다.")
        @Test
        void throwsException_whenBalanceAfterIsNegative() {
            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                PointHistory.builder()
                    .userId("user123")
                    .transactionType(PointTransactionType.USE)
                    .amount(BigDecimal.valueOf(1000))
                    .balanceAfter(BigDecimal.valueOf(-100))
                    .build()
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @DisplayName("equals 메서드")
    @Nested
    class EqualsMethod {
        @DisplayName("동일한 값을 가진 PointHistory 객체는 같다고 판단된다.")
        @Test
        void testEquals() {
            // given
            PointHistory history1 = PointHistory.builder()
                .userId("user123")
                .transactionType(PointTransactionType.CHARGE)
                .amount(BigDecimal.valueOf(1000))
                .balanceAfter(BigDecimal.valueOf(2000))
                .description("충전")
                .build();

            PointHistory history2 = PointHistory.builder()
                .userId("user123")
                .transactionType(PointTransactionType.CHARGE)
                .amount(BigDecimal.valueOf(1000))
                .balanceAfter(BigDecimal.valueOf(2000))
                .description("충전")
                .build();

            // when & then
            assertThat(history1).isEqualTo(history2);
            assertThat(history1.hashCode()).isEqualTo(history2.hashCode());
        }

        @DisplayName("다른 transactionType을 가진 PointHistory 객체는 다르다고 판단된다.")
        @Test
        void testNotEquals_whenDifferentTransactionType() {
            // given
            PointHistory history1 = PointHistory.builder()
                .userId("user123")
                .transactionType(PointTransactionType.CHARGE)
                .amount(BigDecimal.valueOf(1000))
                .balanceAfter(BigDecimal.valueOf(2000))
                .build();

            PointHistory history2 = PointHistory.builder()
                .userId("user123")
                .transactionType(PointTransactionType.USE)
                .amount(BigDecimal.valueOf(1000))
                .balanceAfter(BigDecimal.valueOf(2000))
                .build();

            // when & then
            assertThat(history1).isNotEqualTo(history2);
        }
    }
}
