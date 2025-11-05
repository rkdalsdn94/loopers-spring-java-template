package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointHistoryInfo;
import com.loopers.application.point.PointInfo;
import com.loopers.domain.point.PointTransactionType;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

public class PointV1Dto {

    public record PointResponse(
        Long id,
        String userId,
        BigDecimal balance
    ) {
        public static PointResponse from(PointInfo info) {
            return new PointResponse(
                info.id(),
                info.userId(),
                info.balance()
            );
        }
    }

    public record BalanceResponse(
        BigDecimal balance
    ) {
        public static BalanceResponse from(BigDecimal balance) {
            return new BalanceResponse(balance);
        }
    }

    public record ChargeRequest(
        BigDecimal amount
    ) {
    }

    public record UseRequest(
        BigDecimal amount
    ) {
    }

    public record RefundRequest(
        BigDecimal amount
    ) {
    }

    public record PointHistoryResponse(
        Long id,
        String userId,
        PointTransactionType transactionType,
        BigDecimal amount,
        BigDecimal balanceAfter,
        String description,
        ZonedDateTime createdAt
    ) {
        public static PointHistoryResponse from(PointHistoryInfo info) {
            return new PointHistoryResponse(
                info.id(),
                info.userId(),
                info.transactionType(),
                info.amount(),
                info.balanceAfter(),
                info.description(),
                info.createdAt()
            );
        }
    }

    public record PointHistoriesResponse(
        List<PointHistoryResponse> histories
    ) {
        public static PointHistoriesResponse from(List<PointHistoryInfo> infos) {
            List<PointHistoryResponse> histories = infos.stream()
                .map(PointHistoryResponse::from)
                .toList();
            return new PointHistoriesResponse(histories);
        }
    }
}
