package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserFacade;
import com.loopers.application.user.UserInfo;
import com.loopers.domain.user.User;
import com.loopers.interfaces.api.ApiResponse;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserV1Controller implements UserV1ApiSpec {

    private final UserFacade userFacade;

    @PostMapping
    @Override
    public ApiResponse<UserV1Dto.UserResponse> registerUser(
        @Valid @RequestBody UserV1Dto.RegisterRequest request
    ) {
        User user = request.toEntity();
        UserInfo userInfo = userFacade.registerUser(user);
        UserV1Dto.UserResponse response = UserV1Dto.UserResponse.from(userInfo);
        return ApiResponse.success(response);
    }

    @GetMapping("/{userId}")
    @Override
    public ApiResponse<UserV1Dto.UserResponse> getUser(
        @PathVariable String userId
    ) {
        UserInfo userInfo = userFacade.getUser(userId);
        UserV1Dto.UserResponse response = UserV1Dto.UserResponse.from(userInfo);
        return ApiResponse.success(response);
    }

    @GetMapping("/{userId}/points")
    @Override
    public ApiResponse<UserV1Dto.PointResponse> getPoint(
        @PathVariable String userId
    ) {
        BigDecimal point = userFacade.getPoint(userId);
        UserV1Dto.PointResponse response = UserV1Dto.PointResponse.from(point);
        return ApiResponse.success(response);
    }

    @PostMapping("/{userId}/points/charge")
    @Override
    public ApiResponse<UserV1Dto.PointResponse> chargePoint(
        @PathVariable String userId,
        @Valid @RequestBody UserV1Dto.ChargeRequest request
    ) {
        BigDecimal point = userFacade.chargePoint(userId, request.amount());
        UserV1Dto.PointResponse response = UserV1Dto.PointResponse.from(point);
        return ApiResponse.success(response);
    }
}
