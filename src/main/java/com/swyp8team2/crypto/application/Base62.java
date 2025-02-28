package com.swyp8team2.crypto.application;

import java.math.BigInteger;

public class Base62 {

    private static final String BASE62_ALPHABET =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = 62;

    public static String encode(byte[] bytes) {
        BigInteger value = new BigInteger(1, bytes);
        StringBuilder encoded = new StringBuilder();

        while (value.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] divRem = value.divideAndRemainder(BigInteger.valueOf(BASE));
            value = divRem[0];
            encoded.insert(0, BASE62_ALPHABET.charAt(divRem[1].intValue()));
        }

        return encoded.toString();
    }

    public static byte[] decode(String encoded) {
        BigInteger value = BigInteger.ZERO;

        for (char c : encoded.toCharArray()) {
            value = value.multiply(BigInteger.valueOf(BASE))
                    .add(BigInteger.valueOf(BASE62_ALPHABET.indexOf(c)));
        }

        return value.toByteArray();
    }
}
