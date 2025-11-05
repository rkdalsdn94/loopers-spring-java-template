package com.loopers.application.point;

import com.loopers.domain.point.Point;
import java.math.BigDecimal;

public record PointInfo(
    Long id,
    String userId,
    BigDecimal balance
) {
    public static PointInfo from(Point point) {
        return new PointInfo(
            point.getId(),
            point.getUserId(),
            point.getBalance()
        );
    }
}
