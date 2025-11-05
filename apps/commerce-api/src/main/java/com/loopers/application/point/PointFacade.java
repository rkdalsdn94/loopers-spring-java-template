package com.loopers.application.point;

import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointHistory;
import com.loopers.domain.point.PointService;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PointFacade {
    private final PointService pointService;

    public PointInfo initializePoint(String userId) {
        Point point = pointService.initializePoint(userId);
        return PointInfo.from(point);
    }

    public PointInfo getPoint(String userId) {
        Point point = pointService.getPoint(userId);
        return PointInfo.from(point);
    }

    public BigDecimal getBalance(String userId) {
        return pointService.getBalance(userId);
    }

    public BigDecimal chargePoint(String userId, BigDecimal amount) {
        return pointService.chargePoint(userId, amount);
    }

    public BigDecimal usePoint(String userId, BigDecimal amount) {
        return pointService.usePoint(userId, amount);
    }

    public BigDecimal refundPoint(String userId, BigDecimal amount) {
        return pointService.refundPoint(userId, amount);
    }

    public List<PointHistoryInfo> getPointHistories(String userId) {
        List<PointHistory> histories = pointService.getPointHistories(userId);
        return histories.stream()
            .map(PointHistoryInfo::from)
            .toList();
    }
}
