package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointFacade;
import com.loopers.application.point.PointHistoryInfo;
import com.loopers.application.point.PointInfo;
import com.loopers.interfaces.api.ApiResponse;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/points")
public class PointV1Controller implements PointV1ApiSpec {

    private final PointFacade pointFacade;

    @PostMapping("/{userId}/initialize")
    @Override
    public ApiResponse<PointV1Dto.PointResponse> initializePoint(
        @PathVariable String userId
    ) {
        PointInfo pointInfo = pointFacade.initializePoint(userId);
        PointV1Dto.PointResponse response = PointV1Dto.PointResponse.from(pointInfo);
        return ApiResponse.success(response);
    }

    @GetMapping("/{userId}")
    @Override
    public ApiResponse<PointV1Dto.PointResponse> getPoint(
        @PathVariable String userId
    ) {
        PointInfo pointInfo = pointFacade.getPoint(userId);
        PointV1Dto.PointResponse response = PointV1Dto.PointResponse.from(pointInfo);
        return ApiResponse.success(response);
    }

    @GetMapping("/{userId}/balance")
    @Override
    public ApiResponse<PointV1Dto.BalanceResponse> getBalance(
        @PathVariable String userId
    ) {
        BigDecimal balance = pointFacade.getBalance(userId);
        PointV1Dto.BalanceResponse response = PointV1Dto.BalanceResponse.from(balance);
        return ApiResponse.success(response);
    }

    @PostMapping("/{userId}/charge")
    @Override
    public ApiResponse<PointV1Dto.BalanceResponse> chargePoint(
        @PathVariable String userId,
        @Valid @RequestBody PointV1Dto.ChargeRequest request
    ) {
        BigDecimal balance = pointFacade.chargePoint(userId, request.amount());
        PointV1Dto.BalanceResponse response = PointV1Dto.BalanceResponse.from(balance);
        return ApiResponse.success(response);
    }

    @PostMapping("/{userId}/use")
    @Override
    public ApiResponse<PointV1Dto.BalanceResponse> usePoint(
        @PathVariable String userId,
        @Valid @RequestBody PointV1Dto.UseRequest request
    ) {
        BigDecimal balance = pointFacade.usePoint(userId, request.amount());
        PointV1Dto.BalanceResponse response = PointV1Dto.BalanceResponse.from(balance);
        return ApiResponse.success(response);
    }

    @PostMapping("/{userId}/refund")
    @Override
    public ApiResponse<PointV1Dto.BalanceResponse> refundPoint(
        @PathVariable String userId,
        @Valid @RequestBody PointV1Dto.RefundRequest request
    ) {
        BigDecimal balance = pointFacade.refundPoint(userId, request.amount());
        PointV1Dto.BalanceResponse response = PointV1Dto.BalanceResponse.from(balance);
        return ApiResponse.success(response);
    }

    @GetMapping("/{userId}/histories")
    @Override
    public ApiResponse<PointV1Dto.PointHistoriesResponse> getPointHistories(
        @PathVariable String userId
    ) {
        List<PointHistoryInfo> histories = pointFacade.getPointHistories(userId);
        PointV1Dto.PointHistoriesResponse response = PointV1Dto.PointHistoriesResponse.from(
            histories);
        return ApiResponse.success(response);
    }
}
