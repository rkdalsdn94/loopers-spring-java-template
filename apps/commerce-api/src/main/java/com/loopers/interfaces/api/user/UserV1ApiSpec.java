package com.loopers.interfaces.api.user;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User V1 API", description = "사용자 관리 API")
public interface UserV1ApiSpec {

    @Operation(
        summary = "회원 가입",
        description = "새로운 사용자를 등록합니다."
    )
    ApiResponse<UserV1Dto.UserResponse> registerUser(
        @Schema(description = "회원 가입 정보")
        UserV1Dto.RegisterRequest request
    );

    @Operation(
        summary = "내 정보 조회",
        description = "사용자 ID로 사용자 정보를 조회합니다."
    )
    ApiResponse<UserV1Dto.UserResponse> getUser(
        @Parameter(description = "사용자 ID", required = true)
        String userId
    );

    @Operation(
        summary = "포인트 조회",
        description = "사용자의 보유 포인트를 조회합니다."
    )
    ApiResponse<UserV1Dto.PointResponse> getPoint(
        @Parameter(description = "사용자 ID", required = true)
        String userId
    );

    @Operation(
        summary = "포인트 충전",
        description = "사용자의 포인트를 충전합니다."
    )
    ApiResponse<UserV1Dto.PointResponse> chargePoint(
        @Parameter(description = "사용자 ID", required = true)
        String userId,
        @Schema(description = "충전 요청 정보")
        UserV1Dto.ChargeRequest request
    );
}
