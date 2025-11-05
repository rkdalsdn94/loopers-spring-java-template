package com.loopers.interfaces.api.point;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Point V1 API", description = "포인트 관리 API")
public interface PointV1ApiSpec {

    @Operation(
        summary = "포인트 초기화",
        description = "사용자의 포인트를 초기화합니다."
    )
    ApiResponse<PointV1Dto.PointResponse> initializePoint(
        @Parameter(description = "사용자 ID", required = true)
        String userId
    );

    @Operation(
        summary = "포인트 조회",
        description = "사용자의 포인트 정보를 조회합니다."
    )
    ApiResponse<PointV1Dto.PointResponse> getPoint(
        @Parameter(description = "사용자 ID", required = true)
        String userId
    );

    @Operation(
        summary = "포인트 잔액 조회",
        description = "사용자의 포인트 잔액을 조회합니다."
    )
    ApiResponse<PointV1Dto.BalanceResponse> getBalance(
        @Parameter(description = "사용자 ID", required = true)
        String userId
    );

    @Operation(
        summary = "포인트 충전",
        description = "사용자의 포인트를 충전합니다."
    )
    ApiResponse<PointV1Dto.BalanceResponse> chargePoint(
        @Parameter(description = "사용자 ID", required = true)
        String userId,
        @Schema(description = "충전 요청 정보")
        PointV1Dto.ChargeRequest request
    );

    @Operation(
        summary = "포인트 사용",
        description = "사용자의 포인트를 사용합니다."
    )
    ApiResponse<PointV1Dto.BalanceResponse> usePoint(
        @Parameter(description = "사용자 ID", required = true)
        String userId,
        @Schema(description = "사용 요청 정보")
        PointV1Dto.UseRequest request
    );

    @Operation(
        summary = "포인트 환불",
        description = "사용자의 포인트를 환불합니다."
    )
    ApiResponse<PointV1Dto.BalanceResponse> refundPoint(
        @Parameter(description = "사용자 ID", required = true)
        String userId,
        @Schema(description = "환불 요청 정보")
        PointV1Dto.RefundRequest request
    );

    @Operation(
        summary = "포인트 내역 조회",
        description = "사용자의 포인트 거래 내역을 조회합니다."
    )
    ApiResponse<PointV1Dto.PointHistoriesResponse> getPointHistories(
        @Parameter(description = "사용자 ID", required = true)
        String userId
    );
}
