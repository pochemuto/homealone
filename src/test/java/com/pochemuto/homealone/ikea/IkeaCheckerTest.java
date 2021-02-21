package com.pochemuto.homealone.ikea;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class IkeaCheckerTest {
    @ParameterizedTest
    @CsvSource(value = "18 999,18999.00,18999.001")
    void price(String value) {
        assertThat(IkeaChecker.parsePrice(value)).isEqualTo(new BigDecimal("18999.00"));
    }

    @Test
    void id() {
        assertThat(
                IkeaChecker.parseId(
                        "https://www.ikea.com/ru/ru/p/lagan-lagan-vstraivaemaya-posudomoechnaya-mashina-belyy-20376318/"
                )
        ).isEqualTo(20376318);
    }
}
