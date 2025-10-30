package com.loopers.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

@SpringBootTest
class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;

    @SpyBean
    private UserRepository userRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("회원 가입 시")
    @Nested
    class RegisterUser {
        @DisplayName("회원 가입 시, User 저장이 수행된다.")
        @Test
        void savesUser_whenRegister() {
            // given
            User user = User.builder()
                .userId("newuser")
                .email("new@test.com")
                .birthdate("1990-01-01")
                .gender(Gender.FEMALE)
                .build();

            // when
            User saved = userService.registerUser(user);

            // then - spy 검증
            verify(userRepository).save(user);

            // then - 저장 확인
            assertThat(saved).isNotNull();
            assertThat(saved.getUserId()).isEqualTo("newuser");
            assertThat(saved.getId()).isNotNull();
        }

        @DisplayName("이미 가입된 ID로 회원가입 시도 시, 실패한다.")
        @Test
        void throwsException_whenDuplicateUserId() {
            // given
            User existingUser = User.builder()
                .userId("duplicate")
                .email("existing@test.com")
                .birthdate("1990-01-01")
                .gender(Gender.MALE)
                .build();
            userJpaRepository.save(existingUser);

            User newUser = User.builder()
                .userId("duplicate")
                .email("new@test.com")
                .birthdate("1995-01-01")
                .gender(Gender.FEMALE)
                .build();

            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                userService.registerUser(newUser)
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @DisplayName("내 정보 조회 시")
    @Nested
    class GetUser {
        @DisplayName("해당 ID의 회원이 존재할 경우, 회원 정보가 반환된다.")
        @Test
        void returnsUser_whenUserExists() {
            // given
            User user = User.builder()
                .userId("testuser")
                .email("test@test.com")
                .birthdate("1990-01-01")
                .gender(Gender.MALE)
                .build();
            userJpaRepository.save(user);

            // when
            User result = userService.getUser("testuser");

            // then
            assertThat(result).isNotNull();
            assertAll(
                () -> assertThat(result.getUserId()).isEqualTo("testuser"),
                () -> assertThat(result.getEmail()).isEqualTo("test@test.com"),
                () -> assertThat(result.getBirthdate()).isEqualTo("1990-01-01"),
                () -> assertThat(result.getGender()).isEqualTo(Gender.MALE)
            );
        }

        @DisplayName("해당 ID의 회원이 존재하지 않을 경우, null이 반환된다.")
        @Test
        void returnsNull_whenUserNotExists() {
            // when
            User result = userService.getUser("nonexistent");

            // then
            assertThat(result).isNull();
        }
    }

    @DisplayName("포인트 조회 시")
    @Nested
    class GetPoint {
        @DisplayName("해당 ID의 회원이 존재할 경우, 보유 포인트가 반환된다.")
        @Test
        void returnsPoint_whenUserExists() {
            // given
            User user = User.builder()
                .userId("testuser")
                .email("test@test.com")
                .birthdate("1990-01-01")
                .gender(Gender.MALE)
                .point(BigDecimal.valueOf(5000))
                .build();
            userJpaRepository.save(user);

            // when
            BigDecimal result = userService.getPoint("testuser");

            // then
            assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(5000));
        }

        @DisplayName("해당 ID의 회원이 존재하지 않을 경우, null이 반환된다.")
        @Test
        void returnsNull_whenUserNotExists() {
            // when
            BigDecimal result = userService.getPoint("nonexistent");

            // then
            assertThat(result).isNull();
        }
    }

    @DisplayName("포인트 충전 시")
    @Nested
    class ChargePoint {
        @DisplayName("존재하지 않는 유저 ID로 충전을 시도한 경우, 실패한다.")
        @Test
        void throwsException_whenUserNotExists() {
            // given
            String nonexistentUserId = "nonexistent";
            BigDecimal amount = BigDecimal.valueOf(1000);

            // when & then
            CoreException exception = assertThrows(CoreException.class, () ->
                userService.chargePoint(nonexistentUserId, amount)
            );

            assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }

        @DisplayName("존재하는 유저에게 포인트를 충전하면, 충전된 포인트가 반환된다.")
        @Test
        void returnsChargedPoint_whenUserExists() {
            // given
            User user = User.builder()
                .userId("testuser")
                .email("test@test.com")
                .birthdate("1990-01-01")
                .gender(Gender.MALE)
                .point(BigDecimal.valueOf(1000))
                .build();
            userJpaRepository.save(user);

            // when
            BigDecimal result = userService.chargePoint("testuser", BigDecimal.valueOf(500));

            // then
            assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(1500));
        }
    }
}
