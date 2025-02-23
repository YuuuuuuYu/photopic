package com.swyp8team2.post.application;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RatioCalculator {

    public String calculateRatio(int totalVoteCount, int voteCount) {
        if (totalVoteCount == 0) {
            return "0.0";
        }
        BigDecimal totalCount = new BigDecimal(totalVoteCount);
        BigDecimal count = new BigDecimal(voteCount);
        BigDecimal bigDecimal = count.divide(totalCount, 3, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100));
        return String.format("%.1f", bigDecimal);
    }
}
