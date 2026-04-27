java
package com.pivovarit.hamming.vavr;

import com.pivovarit.hamming.BinaryString;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VavrHammingEncoderTest {

    private final VavrHammingEncoder encoder = new VavrHammingEncoder();

    @Test
    void should_encode_empty_string() {
        assertThat(encoder.encode(BinaryString.of("")).getValue()).isEqualTo("0");
    }

    @Test
    void should_encode_single_bit_string() {
        assertThat(encoder.encode(BinaryString.of("1")).getValue()).isEqualTo("110");
    }

    @Test
    void should_encode_two_bit_string() {
        assertThat(encoder.encode(BinaryString.of("10")).getValue()).isEqualTo("10100");
    }

    @Test
    void should_encode_three_bit_string() {
        assertThat(encoder.encode(BinaryString.of("101")).getValue()).isEqualTo("1011101");
    }

    @Test
    void should_encode_four_bit_string() {
        assertThat(encoder.encode(BinaryString.of("1010")).getValue()).isEqualTo("01010010");
    }

    @Test
    void should_encode_example_string() {
        assertThat(encoder.encode(BinaryString.of("1001101")).getValue()).isEqualTo("10100110101");
    }

    @Test
    void should_encode_longer_string() {
        assertThat(encoder.encode(BinaryString.of("11010110")).getValue()).isEqualTo("0110101100100");
    }

    @Test
    void should_encode_all_ones() {
        assertThat(encoder.encode(BinaryString.of("11111111")).getValue()).isEqualTo("011011111010111");
    }

    @Test
    void should_encode_all_zeros() {
        assertThat(encoder.encode(BinaryString.of("00000000")).getValue()).isEqualTo("000000000000000");
    }
}