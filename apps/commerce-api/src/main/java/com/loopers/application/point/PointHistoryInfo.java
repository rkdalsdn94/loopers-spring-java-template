package com.loopers.application.point;

import com.loopers.domain.point.PointHistory;
import com.loopers.domain.point.PointTransactionType;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record PointHistoryInfo(
    Long id,
    String userId,
    PointTransactionType transactionType,
    BigDecimal amount,
    BigDecimal balanceAfter,
    String description,
    ZonedDateTime createdAt
) {
    public static PointHistoryInfo from(PointHistory history) {
        return new PointHistoryInfo(
            history.getId(),
            history.getUserId(),
            history.getTransactionType(),
            history.getAmount(),
            history.getBalanceAfter(),
            history.getDescription(),
            history.getCreatedAt()
        );
    }
}
