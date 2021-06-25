package com.pochemuto.homealone.strida;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StridaCheckerIntegrationTest {
    private StridaChecker checker;

    @BeforeEach
    public void setUp() {
        checker = new StridaChecker();
    }

    @Test
    void actual() throws IOException {
        var actual = checker.getActual();
        assertThat(actual).isNotEmpty();
        assertThat(actual).extracting(Item::title).doesNotContainNull();
        assertThat(actual).extracting(Item::description).doesNotContainNull();
        assertThat(actual).extracting(Item::availability).doesNotContainNull();
        assertThat(actual).extracting(Item::price)
                .allMatch(price -> price > 25000)
                .allMatch(price -> price < 90000);
    }
}
