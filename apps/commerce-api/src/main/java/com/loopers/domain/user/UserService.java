package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByUserId(user.getUserId())) {
            throw new CoreException(ErrorType.BAD_REQUEST,
                "이미 가입된 ID입니다: " + user.getUserId());
        }
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUser(String userId) {
        return userRepository.findByUserId(userId)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND,
                "존재하지 않는 유저입니다: " + userId));
    }

    @Transactional(readOnly = true)
    public BigDecimal getPoint(String userId) {
        return userRepository.findByUserId(userId)
            .map(User::getPoint)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND,
                "존재하지 않는 유저입니다: " + userId));
    }

    @Transactional
    public BigDecimal chargePoint(String userId, BigDecimal amount) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND,
                "존재하지 않는 유저입니다: " + userId));

        user.chargePoint(amount);
        return user.getPoint();
    }
}
