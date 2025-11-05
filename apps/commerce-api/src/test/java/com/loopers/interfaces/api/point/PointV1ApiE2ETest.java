package com.loopers.interfaces.api.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointTransactionType;
import com.loopers.infrastructure.point.PointJpaRepository;
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
class PointV1ApiE2ETest {

    private final TestRestTemplate testRestTemplate;
    private final PointJpaRepository pointJpaRepository;
    private final DatabaseCleanUp databaseCleanUp;

    @Autowired
    public PointV1ApiE2ETest(
        TestRestTemplate testRestTemplate,
        PointJpaRepository pointJpaRepository,
        DatabaseCleanUp databaseCleanUp
    ) {
        this.testRestTemplate = testRestTemplate;
        this.pointJpaRepository = pointJpaRepository;
        this.databaseCleanUp = databaseCleanUp;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("POST /api/v1/points/{userId}/initialize - 포인트 초기화")
    @Nested
    class InitializePoint {
        @DisplayName("포인트 초기화가 성공할 경우, 생성된 포인트 정보를 응답으로 반환한다.")
        @Test
        void returnsPointInfo_whenInitializeSuccess() {
            // given
            String userId = "user123";

            // when
            ParameterizedTypeReference<ApiResponse<PointV1Dto.PointResponse>> responseType =
                new ParameterizedTypeReference<>() {
                };
            ResponseEntity<ApiResponse<PointV1Dto.PointResponse>> response =
                testRestTemplate.exchange(
                    "/api/v1/points/" + userId + "/initialize",
                    HttpMethod.POST,
                    new HttpEntity<>(null),
                    responseType
                );

            // then
            assertAll(
                () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                () -> assertThat(response.getBody().data().userId()).isEqualTo(userId),
                () -> assertThat(response.getBody().data().balance()).isEqualTo(BigDecimal.ZERO),
                () -> assertThat(response.getBody().data().id()).isNotNull()
            );
        }
    }

    @DisplayName("GET /api/v1/points/{userId} - 포인트 조회")
    @Nested
    class GetPoint {
        @DisplayName("포인트 조회에 성공할 경우, 해당하는 포인트 정보를 응답으로 반환한다.")
        @Test
        void returnsPointInfo_whenPointExists() {
            // given
            String userId = "user123";
            Point point = Point.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(5000))
                .build();
            pointJpaRepository.save(point);

            // when
            ParameterizedTypeReference<ApiResponse<PointV1Dto.PointResponse>> responseType =
                new ParameterizedTypeReference<>() {
                };
            ResponseEntity<ApiResponse<PointV1Dto.PointResponse>> response =
                testRestTemplate.exchange(
                    "/api/v1/points/" + userId,
                    HttpMethod.GET,
                    new HttpEntity<>(null),
                    responseType
                );

            // then
            assertAll(
                () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                () -> assertThat(response.getBody().data().userId()).isEqualTo(userId),
                () -> assertThat(response.getBody().data().balance()).isEqualTo(
                    BigDecimal.valueOf(5000))
            );
        }

        @DisplayName("포인트가 존재하지 않을 경우, 404 Not Found 응답을 반환한다.")
        @Test
        void returnsNotFound_whenPointNotExists() {
            // given
            String userId = "nonexistent";

            // when
            ParameterizedTypeReference<ApiResponse<PointV1Dto.PointResponse>> responseType =
                new ParameterizedTypeReference<>() {
                };
            ResponseEntity<ApiResponse<PointV1Dto.PointResponse>> response =
                testRestTemplate.exchange(
                    "/api/v1/points/" + userId,
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

    @DisplayName("GET /api/v1/points/{userId}/balance - 포인트 잔액 조회")
    @Nested
    class GetBalance {
        @DisplayName("잔액 조회에 성공할 경우, 잔액을 응답으로 반환한다.")
        @Test
        void returnsBalance_whenPointExists() {
            // given
            String userId = "user123";
            Point point = Point.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(3000))
                .build();
            pointJpaRepository.save(point);

            // when
            ParameterizedTypeReference<ApiResponse<PointV1Dto.BalanceResponse>> responseType =
                new ParameterizedTypeReference<>() {
                };
            ResponseEntity<ApiResponse<PointV1Dto.BalanceResponse>> response =
                testRestTemplate.exchange(
                    "/api/v1/points/" + userId + "/balance",
                    HttpMethod.GET,
                    new HttpEntity<>(null),
                    responseType
                );

            // then
            assertAll(
                () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                () -> assertThat(response.getBody().data().balance()).isEqualTo(
                    BigDecimal.valueOf(3000))
            );
        }
    }

    @DisplayName("POST /api/v1/points/{userId}/charge - 포인트 충전")
    @Nested
    class ChargePoint {
        @DisplayName("포인트 충전이 성공할 경우, 새로운 잔액을 응답으로 반환한다.")
        @Test
        void returnsNewBalance_whenChargeSuccess() {
            // given
            String userId = "user123";
            Point point = Point.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(1000))
                .build();
            pointJpaRepository.save(point);

            PointV1Dto.ChargeRequest request = new PointV1Dto.ChargeRequest(
                BigDecimal.valueOf(500)
            );

            // when
            ParameterizedTypeReference<ApiResponse<PointV1Dto.BalanceResponse>> responseType =
                new ParameterizedTypeReference<>() {
                };
            ResponseEntity<ApiResponse<PointV1Dto.BalanceResponse>> response =
                testRestTemplate.exchange(
                    "/api/v1/points/" + userId + "/charge",
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    responseType
                );

            // then
            assertAll(
                () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                () -> assertThat(response.getBody().data().balance()).isEqualTo(
                    BigDecimal.valueOf(1500))
            );
        }

        @DisplayName("0원을 충전하면, 400 Bad Request 응답을 반환한다.")
        @Test
        void returnsBadRequest_whenChargeAmountIsZero() {
            // given
            String userId = "user123";
            Point point = Point.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(1000))
                .build();
            pointJpaRepository.save(point);

            PointV1Dto.ChargeRequest request = new PointV1Dto.ChargeRequest(BigDecimal.ZERO);

            // when
            ParameterizedTypeReference<ApiResponse<PointV1Dto.BalanceResponse>> responseType =
                new ParameterizedTypeReference<>() {
                };
            ResponseEntity<ApiResponse<PointV1Dto.BalanceResponse>> response =
                testRestTemplate.exchange(
                    "/api/v1/points/" + userId + "/charge",
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

    @DisplayName("POST /api/v1/points/{userId}/use - 포인트 사용")
    @Nested
    class UsePoint {
        @DisplayName("포인트 사용이 성공할 경우, 새로운 잔액을 응답으로 반환한다.")
        @Test
        void returnsNewBalance_whenUseSuccess() {
            // given
            String userId = "user123";
            Point point = Point.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(1000))
                .build();
            pointJpaRepository.save(point);

            PointV1Dto.UseRequest request = new PointV1Dto.UseRequest(BigDecimal.valueOf(300));

            // when
            ParameterizedTypeReference<ApiResponse<PointV1Dto.BalanceResponse>> responseType =
                new ParameterizedTypeReference<>() {
                };
            ResponseEntity<ApiResponse<PointV1Dto.BalanceResponse>> response =
                testRestTemplate.exchange(
                    "/api/v1/points/" + userId + "/use",
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    responseType
                );

            // then
            assertAll(
                () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                () -> assertThat(response.getBody().data().balance()).isEqualTo(
                    BigDecimal.valueOf(700))
            );
        }

        @DisplayName("잔액이 부족하면, 400 Bad Request 응답을 반환한다.")
        @Test
        void returnsBadRequest_whenBalanceInsufficient() {
            // given
            String userId = "user123";
            Point point = Point.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(500))
                .build();
            pointJpaRepository.save(point);

            PointV1Dto.UseRequest request = new PointV1Dto.UseRequest(
                BigDecimal.valueOf(1000));

            // when
            ParameterizedTypeReference<ApiResponse<PointV1Dto.BalanceResponse>> responseType =
                new ParameterizedTypeReference<>() {
                };
            ResponseEntity<ApiResponse<PointV1Dto.BalanceResponse>> response =
                testRestTemplate.exchange(
                    "/api/v1/points/" + userId + "/use",
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

    @DisplayName("POST /api/v1/points/{userId}/refund - 포인트 환불")
    @Nested
    class RefundPoint {
        @DisplayName("포인트 환불이 성공할 경우, 새로운 잔액을 응답으로 반환한다.")
        @Test
        void returnsNewBalance_whenRefundSuccess() {
            // given
            String userId = "user123";
            Point point = Point.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(1000))
                .build();
            pointJpaRepository.save(point);

            PointV1Dto.RefundRequest request = new PointV1Dto.RefundRequest(
                BigDecimal.valueOf(200)
            );

            // when
            ParameterizedTypeReference<ApiResponse<PointV1Dto.BalanceResponse>> responseType =
                new ParameterizedTypeReference<>() {
                };
            ResponseEntity<ApiResponse<PointV1Dto.BalanceResponse>> response =
                testRestTemplate.exchange(
                    "/api/v1/points/" + userId + "/refund",
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    responseType
                );

            // then
            assertAll(
                () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                () -> assertThat(response.getBody().data().balance()).isEqualTo(
                    BigDecimal.valueOf(1200))
            );
        }
    }

    @DisplayName("GET /api/v1/points/{userId}/histories - 포인트 내역 조회")
    @Nested
    class GetPointHistories {
        @DisplayName("포인트 내역 조회에 성공할 경우, 거래 내역 리스트를 응답으로 반환한다.")
        @Test
        void returnsHistories_whenHistoriesExist() {
            // given
            String userId = "user123";
            Point point = Point.builder()
                .userId(userId)
                .balance(BigDecimal.ZERO)
                .build();
            pointJpaRepository.save(point);

            // 충전
            PointV1Dto.ChargeRequest chargeRequest = new PointV1Dto.ChargeRequest(
                BigDecimal.valueOf(1000)
            );
            testRestTemplate.exchange(
                "/api/v1/points/" + userId + "/charge",
                HttpMethod.POST,
                new HttpEntity<>(chargeRequest),
                new ParameterizedTypeReference<ApiResponse<PointV1Dto.BalanceResponse>>() {
                }
            );

            // when
            ParameterizedTypeReference<ApiResponse<PointV1Dto.PointHistoriesResponse>> responseType =
                new ParameterizedTypeReference<>() {
                };
            ResponseEntity<ApiResponse<PointV1Dto.PointHistoriesResponse>> response =
                testRestTemplate.exchange(
                    "/api/v1/points/" + userId + "/histories",
                    HttpMethod.GET,
                    new HttpEntity<>(null),
                    responseType
                );

            // then
            assertAll(
                () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                () -> assertThat(response.getBody().data().histories()).hasSize(1),
                () -> assertThat(response.getBody().data().histories().get(0).transactionType())
                    .isEqualTo(PointTransactionType.CHARGE),
                () -> assertThat(response.getBody().data().histories().get(0).amount())
                    .isEqualTo(BigDecimal.valueOf(1000))
            );
        }
    }
}
