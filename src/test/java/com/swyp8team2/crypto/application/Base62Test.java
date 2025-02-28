package com.swyp8team2.crypto.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.*;

class Base62Test {

    @Test
    @DisplayName("인코딩 디코딩")
    void encodingAndDecoding() throws Exception {
        //given
        String plainText = "Hello, World!";
        byte[] bytes = plainText.getBytes(StandardCharsets.UTF_8);

        //when
        String encode = Base62.encode(bytes);
        byte[] decode = Base62.decode(encode);

        String decodeText = new String(decode, StandardCharsets.UTF_8);

        //then
        assertThat(decodeText).isEqualTo(plainText);
    }

    @Test
    @DisplayName("인코딩 디코딩 - 다른 문자열")
    void encodingAndDecoding_differentText() throws Exception {
        //given
        String plainText = "Hello, World!";
        byte[] bytes = plainText.getBytes(StandardCharsets.UTF_8);

        //when
        String encode = Base62.encode(bytes);
        byte[] decode = Base62.decode("different");

        String decodeText = new String(decode, StandardCharsets.UTF_8);

        //then
        assertThat(decodeText).isNotEqualTo(plainText);
    }
}
