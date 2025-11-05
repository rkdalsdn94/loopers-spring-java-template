package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserInfo;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.User;
import java.math.BigDecimal;

public class UserV1Dto {

    public record RegisterRequest(
        String userId,
        String email,
        String birthdate,
        Gender gender
    ) {
        public User toEntity() {
            return User.builder()
                .userId(userId)
                .email(email)
                .birthdate(birthdate)
                .gender(gender)
                .build();
        }
    }

    public record UserResponse(
        Long id,
        String userId,
        String email,
        String birthdate,
        Gender gender,
        BigDecimal point
    ) {
        public static UserResponse from(UserInfo info) {
            return new UserResponse(
                info.id(),
                info.userId(),
                info.email(),
                info.birthdate(),
                info.gender(),
                info.point()
            );
        }
    }

    public record PointResponse(
        BigDecimal point
    ) {
        public static PointResponse from(BigDecimal point) {
            return new PointResponse(point);
        }
    }

    public record ChargeRequest(
        BigDecimal amount
    ) {
    }
}
