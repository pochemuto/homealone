package com.pochemuto.homealone.marafon;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.List;

import com.pochemuto.homealone.HomealoneApplication;
import com.pochemuto.homealone.Profile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = HomealoneApplication.class)
@ActiveProfiles(Profile.INTEGRATION_TEST)
class MarafonParserIntegrationTest {

    @Autowired
    private MarafonParser parser;

    @Autowired
    private MealRepository repository;

    @Test
    void login() throws IOException {
        assertThat(parser.login()).isTrue();
    }

    @Test
    void food() {
        List<Meal> meals = parser.getFood(1, DayOfWeek.MONDAY);
        System.out.println(meals);
        repository.saveAll(meals);
        repository.saveAll(parser.getFood(1, DayOfWeek.TUESDAY));

        var load = repository.findAll();
        System.out.println(load);
    }
}
