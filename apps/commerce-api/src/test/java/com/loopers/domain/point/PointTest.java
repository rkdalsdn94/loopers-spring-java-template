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

@DisplayName("포인트 관리")
class PointTest {

    @DisplayName("Point 객체 생성 시")
    @Nested
    class Create {
        @DisplayName("유효한 정보가 주어지면, 정상적으로 생성된다.")
        @Test
        void createsPoint_whenValidInfoProvided() {
            // given
            String userId = "user123";
            BigDecimal balance = BigDecimal.valueOf(1000);

            // when
            Point actual = Point.builder()
                .userId(userId)
                .balance(balance)
                .build();

            // then
            assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getUserId()).isEqualTo(userId),
                () -> assertThat(actual.getBalance()).isEqualTo(balance)
            );
        }

        @DisplayName("잔액이 null이면, 0으로 초기화된다.")
        @Test
        void initializesBalanceToZero_whenBalanceIsNull() {
            // given
            String userId = "user123";

            // when
            Point actual = Point.builder()
                .userId(userId)
                .balance(null)
                .build();

            // then
            assertThat(actual.getBalance()).isEqualTo(BigDecimal.ZERO);
        }

        @DisplayName("userId가 null이면, 실패한다.")
        @Test
        void throwsException_whenUserIdIsNull() {
            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                Point.builder()
                    .userId(null)
                    .balance(BigDecimal.ZERO)
                    .build()
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("userId가 빈 문자열이면, 실패한다.")
        @Test
        void throwsException_whenUserIdIsBlank() {
            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                Point.builder()
                    .userId("")
                    .balance(BigDecimal.ZERO)
                    .build()
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @DisplayName("포인트 충전 시")
    @Nested
    class Charge {
        @DisplayName("양수를 충전하면, 잔액이 증가한다.")
        @Test
        void increasesBalance_whenChargeAmountIsPositive() {
            // given
            Point point = Point.builder()
                .userId("user123")
                .balance(BigDecimal.valueOf(1000))
                .build();

            // when
            point.charge(BigDecimal.valueOf(500));

            // then
            assertThat(point.getBalance()).isEqualTo(BigDecimal.valueOf(1500));
        }

        @DisplayName("0원을 충전하면, 실패한다.")
        @Test
        void throwsException_whenChargeAmountIsZero() {
            // given
            Point point = Point.builder()
                .userId("user123")
                .balance(BigDecimal.valueOf(1000))
                .build();

            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                point.charge(BigDecimal.ZERO)
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("음수를 충전하면, 실패한다.")
        @Test
        void throwsException_whenChargeAmountIsNegative() {
            // given
            Point point = Point.builder()
                .userId("user123")
                .balance(BigDecimal.valueOf(1000))
                .build();

            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                point.charge(BigDecimal.valueOf(-100))
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @DisplayName("포인트 사용 시")
    @Nested
    class Use {
        @DisplayName("잔액이 충분하면, 사용이 성공한다.")
        @Test
        void decreasesBalance_whenBalanceIsSufficient() {
            // given
            Point point = Point.builder()
                .userId("user123")
                .balance(BigDecimal.valueOf(1000))
                .build();

            // when
            point.use(BigDecimal.valueOf(300));

            // then
            assertThat(point.getBalance()).isEqualTo(BigDecimal.valueOf(700));
        }

        @DisplayName("잔액이 부족하면, 실패한다.")
        @Test
        void throwsException_whenBalanceIsInsufficient() {
            // given
            Point point = Point.builder()
                .userId("user123")
                .balance(BigDecimal.valueOf(500))
                .build();

            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                point.use(BigDecimal.valueOf(1000))
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("0원을 사용하면, 실패한다.")
        @Test
        void throwsException_whenUseAmountIsZero() {
            // given
            Point point = Point.builder()
                .userId("user123")
                .balance(BigDecimal.valueOf(1000))
                .build();

            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                point.use(BigDecimal.ZERO)
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("음수를 사용하면, 실패한다.")
        @Test
        void throwsException_whenUseAmountIsNegative() {
            // given
            Point point = Point.builder()
                .userId("user123")
                .balance(BigDecimal.valueOf(1000))
                .build();

            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                point.use(BigDecimal.valueOf(-100))
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @DisplayName("포인트 환불 시")
    @Nested
    class Refund {
        @DisplayName("양수를 환불하면, 잔액이 증가한다.")
        @Test
        void increasesBalance_whenRefundAmountIsPositive() {
            // given
            Point point = Point.builder()
                .userId("user123")
                .balance(BigDecimal.valueOf(1000))
                .build();

            // when
            point.refund(BigDecimal.valueOf(200));

            // then
            assertThat(point.getBalance()).isEqualTo(BigDecimal.valueOf(1200));
        }

        @DisplayName("0원을 환불하면, 실패한다.")
        @Test
        void throwsException_whenRefundAmountIsZero() {
            // given
            Point point = Point.builder()
                .userId("user123")
                .balance(BigDecimal.valueOf(1000))
                .build();

            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                point.refund(BigDecimal.ZERO)
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("음수를 환불하면, 실패한다.")
        @Test
        void throwsException_whenRefundAmountIsNegative() {
            // given
            Point point = Point.builder()
                .userId("user123")
                .balance(BigDecimal.valueOf(1000))
                .build();

            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                point.refund(BigDecimal.valueOf(-100))
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @DisplayName("equals 메서드")
    @Nested
    class EqualsMethod {
        @DisplayName("동일한 값을 가진 Point 객체는 같다고 판단된다.")
        @Test
        void testEquals() {
            // given
            Point point1 = Point.builder()
                .userId("user123")
                .balance(BigDecimal.valueOf(1000))
                .build();

            Point point2 = Point.builder()
                .userId("user123")
                .balance(BigDecimal.valueOf(1000))
                .build();

            // when & then
            assertThat(point1).isEqualTo(point2);
            assertThat(point1.hashCode()).isEqualTo(point2.hashCode());
        }

        @DisplayName("다른 userId를 가진 Point 객체는 다르다고 판단된다.")
        @Test
        void testNotEquals_whenDifferentUserId() {
            // given
            Point point1 = Point.builder()
                .userId("user123")
                .balance(BigDecimal.valueOf(1000))
                .build();

            Point point2 = Point.builder()
                .userId("user456")
                .balance(BigDecimal.valueOf(1000))
                .build();

            // when & then
            assertThat(point1).isNotEqualTo(point2);
        }

        @DisplayName("다른 balance를 가진 Point 객체는 다르다고 판단된다.")
        @Test
        void testNotEquals_whenDifferentBalance() {
            // given
            Point point1 = Point.builder()
                .userId("user123")
                .balance(BigDecimal.valueOf(1000))
                .build();

            Point point2 = Point.builder()
                .userId("user123")
                .balance(BigDecimal.valueOf(2000))
                .build();

            // when & then
            assertThat(point1).isNotEqualTo(point2);
        }
    }
}
