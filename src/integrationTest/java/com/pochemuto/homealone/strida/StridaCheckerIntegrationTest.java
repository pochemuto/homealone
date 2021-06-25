package com.pochemuto.homealone.strida;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import com.google.common.collect.MapDifference;
import com.pochemuto.homealone.HomealoneApplication;
import com.pochemuto.homealone.Profiles;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = HomealoneApplication.class)
@ActiveProfiles(Profiles.INTEGRATION_TEST)
class StridaCheckerIntegrationTest {
    @Autowired
    private StridaChecker checker;

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

    @Test
    void changes() throws IOException {
        var result = new AtomicReference<MapDifference<Integer, Item>>();
        checker.changes(result::set);

        assertThat(result.get().entriesInCommon()).isEmpty();
        assertThat(result.get().entriesDiffering()).isEmpty();
        assertThat(result.get().entriesOnlyOnLeft()).isEmpty();
        assertThat(result.get().entriesOnlyOnRight()).isNotEmpty();

        var newItems = result.get().entriesOnlyOnRight();

        checker.changes(result::set);

        assertThat(result.get().entriesInCommon()).isEqualTo(newItems);
        assertThat(result.get().entriesDiffering()).isEmpty();
        assertThat(result.get().entriesOnlyOnLeft()).isEmpty();
        assertThat(result.get().entriesOnlyOnRight()).isEmpty();
    }
}
