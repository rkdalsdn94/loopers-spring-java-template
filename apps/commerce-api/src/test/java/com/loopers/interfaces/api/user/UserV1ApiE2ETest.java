package com.loopers.interfaces.api.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.loopers.domain.user.Gender;
import com.loopers.domain.user.User;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.utils.DatabaseCleanUp;
import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserV1ApiE2ETest {

    private final TestRestTemplate testRestTemplate;
    private final UserJpaRepository userJpaRepository;
    private final DatabaseCleanUp databaseCleanUp;

    @Autowired
    public UserV1ApiE2ETest(
        TestRestTemplate testRestTemplate,
        UserJpaRepository userJpaRepository,
        DatabaseCleanUp databaseCleanUp
    ) {
        this.testRestTemplate = testRestTemplate;
        this.userJpaRepository = userJpaRepository;
        this.databaseCleanUp = databaseCleanUp;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("POST /api/v1/users - 회원 가입")
    @Nested
    class RegisterUser {
        @DisplayName("회원 가입이 성공할 경우, 생성된 유저 정보를 응답으로 반환한다.")
        @Test
        void returnsUserInfo_whenRegisterSuccess() {
            // given
            UserV1Dto.RegisterRequest request = new UserV1Dto.RegisterRequest(
                "newuser",
                "new@test.com",
                "1990-01-01",
                Gender.FEMALE
            );

            // when
            ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>> responseType =
                new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response =
                testRestTemplate.exchange(
                    "/api/v1/users",
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    responseType
                );

            // then
            assertAll(
                () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                () -> assertThat(response.getBody().data().userId()).isEqualTo("newuser"),
                () -> assertThat(response.getBody().data().email()).isEqualTo("new@test.com"),
                () -> assertThat(response.getBody().data().birthdate()).isEqualTo("1990-01-01"),
                () -> assertThat(response.getBody().data().gender()).isEqualTo(Gender.FEMALE),
                () -> assertThat(response.getBody().data().id()).isNotNull()
            );
        }

        @DisplayName("회원 가입 시에 성별이 없을 경우, 400 Bad Request 응답을 반환한다.")
        @Test
        void returnsBadRequest_whenGenderIsNull() {
            // given
            UserV1Dto.RegisterRequest request = new UserV1Dto.RegisterRequest(
                "newuser",
                "new@test.com",
                "1990-01-01",
                null
            );

            // when
            ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>> responseType =
                new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response =
                testRestTemplate.exchange(
                    "/api/v1/users",
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    responseType
                );

            // then
            assertAll(
                () -> assertTrue(response.getStatusCode().is4xxClientError()),
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
            );
        }
    }

    @DisplayName("GET /api/v1/users/{userId} - 내 정보 조회")
    @Nested
    class GetUser {
        @DisplayName("내 정보 조회에 성공할 경우, 해당하는 유저 정보를 응답으로 반환한다.")
        @Test
        void returnsUserInfo_whenUserExists() {
            // given
            User user = User.builder()
                .userId("testuser")
                .email("test@test.com")
                .birthdate("1990-01-01")
                .gender(Gender.MALE)
                .build();
            userJpaRepository.save(user);

            // when
            ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>> responseType =
                new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response =
                testRestTemplate.exchange(
                    "/api/v1/users/testuser",
                    HttpMethod.GET,
                    new HttpEntity<>(null),
                    responseType
                );

            // then
            assertAll(
                () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                () -> assertThat(response.getBody().data().userId()).isEqualTo("testuser"),
                () -> assertThat(response.getBody().data().email()).isEqualTo("test@test.com"),
                () -> assertThat(response.getBody().data().birthdate()).isEqualTo("1990-01-01"),
                () -> assertThat(response.getBody().data().gender()).isEqualTo(Gender.MALE)
            );
        }

        @DisplayName("존재하지 않는 ID로 조회할 경우, 404 Not Found 응답을 반환한다.")
        @Test
        void returnsNotFound_whenUserNotExists() {
            // when
            ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>> responseType =
                new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response =
                testRestTemplate.exchange(
                    "/api/v1/users/nonexistent",
                    HttpMethod.GET,
                    new HttpEntity<>(null),
                    responseType
                );

            // then
            assertAll(
                () -> assertTrue(response.getStatusCode().is4xxClientError()),
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
            );
        }
    }

    @DisplayName("GET /api/v1/users/{userId}/points - 포인트 조회")
    @Nested
    class GetPoint {
        @DisplayName("포인트 조회에 성공할 경우, 보유 포인트를 응답으로 반환한다.")
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
            ParameterizedTypeReference<ApiResponse<UserV1Dto.PointResponse>> responseType =
                new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<UserV1Dto.PointResponse>> response =
                testRestTemplate.exchange(
                    "/api/v1/users/testuser/points",
                    HttpMethod.GET,
                    new HttpEntity<>(null),
                    responseType
                );

            // then
            assertAll(
                () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                () -> assertThat(response.getBody().data().point())
                    .isEqualByComparingTo(BigDecimal.valueOf(5000))
            );
        }

        @DisplayName("존재하지 않는 ID로 조회할 경우, 404 Not Found 응답을 반환한다.")
        @Test
        void returnsNotFound_whenUserNotExists() {
            // when
            ParameterizedTypeReference<ApiResponse<UserV1Dto.PointResponse>> responseType =
                new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<UserV1Dto.PointResponse>> response =
                testRestTemplate.exchange(
                    "/api/v1/users/nonexistent/points",
                    HttpMethod.GET,
                    new HttpEntity<>(null),
                    responseType
                );

            // then
            assertAll(
                () -> assertTrue(response.getStatusCode().is4xxClientError()),
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
            );
        }
    }

    @DisplayName("POST /api/v1/users/{userId}/points/charge - 포인트 충전")
    @Nested
    class ChargePoint {
        @DisplayName("존재하는 유저가 1000원을 충전할 경우, 충전된 보유 총량을 응답으로 반환한다.")
        @Test
        void returnsChargedPoint_whenUserExists() {
            // given
            User user = User.builder()
                .userId("testuser")
                .email("test@test.com")
                .birthdate("1990-01-01")
                .gender(Gender.MALE)
                .point(BigDecimal.valueOf(2000))
                .build();
            userJpaRepository.save(user);

            UserV1Dto.ChargeRequest request = new UserV1Dto.ChargeRequest(
                BigDecimal.valueOf(1000)
            );

            // when
            ParameterizedTypeReference<ApiResponse<UserV1Dto.PointResponse>> responseType =
                new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<UserV1Dto.PointResponse>> response =
                testRestTemplate.exchange(
                    "/api/v1/users/testuser/points/charge",
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    responseType
                );

            // then
            assertAll(
                () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                () -> assertThat(response.getBody().data().point())
                    .isEqualByComparingTo(BigDecimal.valueOf(3000))
            );
        }

        @DisplayName("존재하지 않는 유저로 요청할 경우, 404 Not Found 응답을 반환한다.")
        @Test
        void returnsNotFound_whenUserNotExists() {
            // given
            UserV1Dto.ChargeRequest request = new UserV1Dto.ChargeRequest(
                BigDecimal.valueOf(1000)
            );

            // when
            ParameterizedTypeReference<ApiResponse<UserV1Dto.PointResponse>> responseType =
                new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<UserV1Dto.PointResponse>> response =
                testRestTemplate.exchange(
                    "/api/v1/users/nonexistent/points/charge",
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    responseType
                );

            // then
            assertAll(
                () -> assertTrue(response.getStatusCode().is4xxClientError()),
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
            );
        }
    }
}
