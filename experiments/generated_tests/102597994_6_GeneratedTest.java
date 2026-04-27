java
package com.tolpp.memguard.encoder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class RandomXorEncoderFactoryTest {

    @Test
    void create() {
        RandomXorEncoderFactory factory = RandomXorEncoderFactory.create();
        assertNotNull(factory);
    }
}