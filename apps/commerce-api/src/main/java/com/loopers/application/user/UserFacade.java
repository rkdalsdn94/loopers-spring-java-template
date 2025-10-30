package com.loopers.application.user;

import com.loopers.domain.user.User;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserFacade {
    private final UserService userService;

    public UserInfo registerUser(User user) {
        User saved = userService.registerUser(user);
        return UserInfo.from(saved);
    }

    public UserInfo getUser(String userId) {
        User user = userService.getUser(userId);
        if (user == null) {
            throw new CoreException(ErrorType.NOT_FOUND,
                "존재하지 않는 유저입니다: " + userId);
        }
        return UserInfo.from(user);
    }

    public BigDecimal getPoint(String userId) {
        BigDecimal point = userService.getPoint(userId);
        if (point == null) {
            throw new CoreException(ErrorType.NOT_FOUND,
                "존재하지 않는 유저입니다: " + userId);
        }
        return point;
    }

    public BigDecimal chargePoint(String userId, BigDecimal amount) {
        return userService.chargePoint(userId, amount);
    }
}
