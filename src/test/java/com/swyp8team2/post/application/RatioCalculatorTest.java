package com.swyp8team2.post.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RatioCalculatorTest {

    RatioCalculator ratioCalculator;

    @BeforeEach
    void setUp() {
        ratioCalculator = new RatioCalculator();
    }

    @ParameterizedTest(name = "{index}: totalVoteCount={0}, voteCount={1} => result={2}")
    @CsvSource({"3, 2, 66.7", "3, 1, 33.3", "4, 2, 50.0", "4, 3, 75.0", "0, 0, 0.0", "1, 0, 0.0", "1, 1, 100.0"})
    @DisplayName("비율 계산")
    void calculate(int totalVoteCount, int voteCount, String result) throws Exception {
        //given

        //when
        String ratio = ratioCalculator.calculateRatio(totalVoteCount, voteCount);

        //then
        assertThat(ratio).isEqualTo(result);
    }
}
