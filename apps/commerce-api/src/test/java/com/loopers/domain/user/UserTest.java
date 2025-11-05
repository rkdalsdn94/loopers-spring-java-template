package com.loopers.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("회원가입")
class UserTest {

    @DisplayName("User 객체 생성 시")
    @Nested
    class Create {
        @DisplayName("유효한 정보가 주어지면, 정상적으로 생성된다.")
        @Test
        void createsUser_whenValidInfoProvided() {
            // given
            String userId = "user123";
            String email = "test@test.com";
            String birthDate = "2004-12-03";
            Gender gender = Gender.MALE;

            // when
            User actual = User.builder()
                .userId(userId)
                .email(email)
                .birthdate(birthDate)
                .gender(gender)
                .build();

            // then
            User expected = User.builder()
                .userId(userId)
                .email(email)
                .birthdate(birthDate)
                .gender(gender)
                .build();

            assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual).isEqualTo(expected)
            );
        }

        @DisplayName("ID가 10자를 초과하면, User 객체 생성에 실패한다.")
        @Test
        void throwsException_whenUserIdExceeds10Characters() {
            // given
            String invalidUserId = "user1234567"; // 11자

            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                User.builder()
                    .userId(invalidUserId)
                    .email("test@test.com")
                    .birthdate("2004-12-03")
                    .gender(Gender.MALE)
                    .build()
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("ID에 특수문자가 포함되면, User 객체 생성에 실패한다.")
        @Test
        void throwsException_whenUserIdContainsSpecialCharacters() {
            // given
            String invalidUserId = "user@123";

            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                User.builder()
                    .userId(invalidUserId)
                    .email("test@test.com")
                    .birthdate("2004-12-03")
                    .gender(Gender.MALE)
                    .build()
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("ID에 한글이 포함되면, User 객체 생성에 실패한다.")
        @Test
        void throwsException_whenUserIdContainsKorean() {
            // given
            String invalidUserId = "유저123";

            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                User.builder()
                    .userId(invalidUserId)
                    .email("test@test.com")
                    .birthdate("2004-12-03")
                    .gender(Gender.MALE)
                    .build()
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("이메일이 @가 없으면, User 객체 생성에 실패한다.")
        @Test
        void throwsException_whenEmailHasNoAtSign() {
            // given
            String invalidEmail = "invalid-email";

            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                User.builder()
                    .userId("user123")
                    .email(invalidEmail)
                    .birthdate("2004-12-03")
                    .gender(Gender.MALE)
                    .build()
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("이메일이 도메인이 없으면, User 객체 생성에 실패한다.")
        @Test
        void throwsException_whenEmailHasNoDomain() {
            // given
            String invalidEmail = "test@";

            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                User.builder()
                    .userId("user123")
                    .email(invalidEmail)
                    .birthdate("2004-12-03")
                    .gender(Gender.MALE)
                    .build()
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("이메일이 xx@yy.zz 형식이 아니면, User 객체 생성에 실패한다.")
        @Test
        void throwsException_whenEmailIsInvalid() {
            // given
            String invalidEmail = "test@invalid";

            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                User.builder()
                    .userId("user123")
                    .email(invalidEmail)
                    .birthdate("2004-12-03")
                    .gender(Gender.MALE)
                    .build()
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("생년월일이 yyyy-MM-dd 형식이 아니면(슬래시 구분), User 객체 생성에 실패한다.")
        @Test
        void throwsException_whenBirthdateHasSlash() {
            // given
            String invalidBirthdate = "2004/12/03";

            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                User.builder()
                    .userId("user123")
                    .email("test@test.com")
                    .birthdate(invalidBirthdate)
                    .gender(Gender.MALE)
                    .build()
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("생년월일이 yyyy-MM-dd 형식이 아니면(점 구분), User 객체 생성에 실패한다.")
        @Test
        void throwsException_whenBirthdateHasDot() {
            // given
            String invalidBirthdate = "2004.12.03";

            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                User.builder()
                    .userId("user123")
                    .email("test@test.com")
                    .birthdate(invalidBirthdate)
                    .gender(Gender.MALE)
                    .build()
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("생년월일이 숫자가 아닌 문자를 포함하면, User 객체 생성에 실패한다.")
        @Test
        void throwsException_whenBirthdateContainsNonNumeric() {
            // given
            String invalidBirthdate = "19ab-12-03";

            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                User.builder()
                    .userId("user123")
                    .email("test@test.com")
                    .birthdate(invalidBirthdate)
                    .gender(Gender.MALE)
                    .build()
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @DisplayName("equals 메서드")
    @Nested
    class EqualsMethod {
        @DisplayName("동일한 값을 가진 User 객체는 같다고 판단된다.")
        @Test
        void testEquals() {
            // given
            User user1 = User.builder()
                .userId("user123")
                .email("test@test.com")
                .birthdate("2004-12-03")
                .gender(Gender.MALE)
                .build();

            User user2 = User.builder()
                .userId("user123")
                .email("test@test.com")
                .birthdate("2004-12-03")
                .gender(Gender.MALE)
                .build();

            // when & then
            assertThat(user1).isEqualTo(user2);
            assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
        }

        @DisplayName("다른 userId를 가진 User 객체는 다르다고 판단된다.")
        @Test
        void testNotEquals_whenDifferentUserId() {
            // given
            User user1 = User.builder()
                .userId("user123")
                .email("test@test.com")
                .birthdate("2004-12-03")
                .gender(Gender.MALE)
                .build();

            User user2 = User.builder()
                .userId("user456")
                .email("test@test.com")
                .birthdate("2004-12-03")
                .gender(Gender.MALE)
                .build();

            // when & then
            assertThat(user1).isNotEqualTo(user2);
        }
    }

    @DisplayName("포인트 충전 시")
    @Nested
    @SuppressWarnings("deprecation")
    class ChargePoint {
        @DisplayName("0원을 충전하면, 실패한다.")
        @Test
        void throwsException_whenChargeAmountIsZero() {
            // given
            User user = User.builder()
                .userId("user123")
                .email("test@test.com")
                .birthdate("2004-12-03")
                .gender(Gender.MALE)
                .point(BigDecimal.valueOf(1000))
                .build();

            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                user.chargePoint(BigDecimal.ZERO)
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("음수를 충전하면, 실패한다.")
        @Test
        void throwsException_whenChargeAmountIsNegative() {
            // given
            User user = User.builder()
                .userId("user123")
                .email("test@test.com")
                .birthdate("2004-12-03")
                .gender(Gender.MALE)
                .point(BigDecimal.valueOf(1000))
                .build();

            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                user.chargePoint(BigDecimal.valueOf(-100))
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("양수를 충전하면, 포인트가 증가한다.")
        @Test
        void increasesPoint_whenChargeAmountIsPositive() {
            // given
            User user = User.builder()
                .userId("user123")
                .email("test@test.com")
                .birthdate("2004-12-03")
                .gender(Gender.MALE)
                .point(BigDecimal.valueOf(1000))
                .build();

            // when
            user.chargePoint(BigDecimal.valueOf(500));

            // then
            assertThat(user.getPoint()).isEqualTo(BigDecimal.valueOf(1500));
        }
    }
}
