package com.swyp8team2.common.util;

import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.common.exception.InternalServerException;

import java.util.Arrays;
import java.util.Objects;

public class Validator {

    public static void validateNull(Object... object) {
        Arrays.stream(object)
                .filter(Objects::isNull)
                .forEach(o -> {
                    throw new InternalServerException(ErrorCode.INVALID_INPUT_VALUE);
                });
    }

    public static void validateEmptyString(String... strings) {
        Arrays.stream(strings)
                .filter(String::isEmpty)
                .forEach(s -> {
                    throw new InternalServerException(ErrorCode.INVALID_INPUT_VALUE);
                });
    }
}
