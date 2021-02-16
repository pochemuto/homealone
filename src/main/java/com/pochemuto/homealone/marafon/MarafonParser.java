package com.pochemuto.homealone.marafon;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.google.common.base.Preconditions;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.pochemuto.homealone.marafon.UrlUtils.url;
import static java.util.Collections.emptyMap;

@Slf4j
@Component
public class MarafonParser {
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_6)" +
            " AppleWebKit/605.1.15 (KHTML, like Gecko)" +
            " Version/14.0.3 Safari/605.1.15";

    @Autowired
    private MarafonProperties properties;

    private static final String URL = "https://lk.lerchekmarafon.ru";

    private final AtomicReference<Map<String, String>> cookies = new AtomicReference<>(emptyMap());

    public boolean login() throws IOException {
        Connection.Response loginResponse = Jsoup.connect(URL)
                .method(Method.GET)
                .userAgent(USER_AGENT)
                .execute();

        Map<String, String> cookies = Map.copyOf(loginResponse.cookies());
        Document loginDocument = loginResponse.parse();
        FormElement loginForm = ((FormElement) loginDocument.selectFirst("#login-form"));

        loginForm.getElementById("loginform-email").val(properties.getLogin());
        loginForm.getElementById("loginform-password").val(properties.getPassword());

        Connection.Response loginResult = loginForm.submit()
                .cookies(cookies)
                .referrer(URL)
                .userAgent(USER_AGENT)
                .execute();

        this.cookies.set(loginResult.cookies());

        return isAuthorized(loginResult.parse());
    }

    private boolean isAuthorized(Document page) {
        Element logoutLink = page.selectFirst(".header_user_drop_menu li a[href='/site/logout']");
        boolean authorized = logoutLink != null;
        if (!authorized) {
            log.info("Response page doesn't look like authorized page");
        }
        return authorized;
    }

    @SneakyThrows
    public List<Meal> getFood(int week, DayOfWeek day) {
        log.info("Getting food for week {} at {}", week, day);
        String url = url(URL).path("marafon/food")
                .queryParam("week", week)
                .queryParam("number", day.getValue())
                .build()
                .toUriString();

        return parseFood(getPage(url), week, day);
    }

    private List<Meal> parseFood(Document page, int week, DayOfWeek day) {
        Elements mealElements = page.select(".food_list .food_item");
        List<Meal> meals = new ArrayList<>();
        int num = 0;
        for (Element mealElement : mealElements) {
            String title = mealElement.select(".food_tittle").text();
            if (title.isEmpty()) {
                log.info("Skipping meal, empty title");
                continue;
            }

            var ingredients = new ArrayList<Ingredient>();
            Elements ingredientElements = mealElement.select(".ingredients");
            for (Element ingredientElement : ingredientElements) {
                var ingredient = new Ingredient();
                ingredient.setName(ingredientElement.text());
                ingredients.add(ingredient);
            }

            var meal = new Meal();
            meal.setId(new Meal.Key(week, day, num++));
            meal.setTitle(title);
            meal.setIngredients(ingredients);
            meal.setCooking(mealElement.select(".cooking p").text());

            meals.add(meal);
        }

        return meals;
    }


    private Document getPage(String url) throws IOException {
        log.info("Requesting {}", url);

        for (int i = 0; i < 2; i++) {
            Connection.Response result = Jsoup.connect(url)
                    .cookies(cookies.get())
                    .referrer(URL)
                    .userAgent(USER_AGENT)
                    .execute();

            Document document = result.parse();

            if (!isAuthorized(document)) {
                log.info("User not authorized, perform login");
                boolean authorized = login();
                log.info("Authorization status {}", authorized);
                Preconditions.checkState(authorized, "Authorization failed");
                continue;
            }

            return document;
        }

        throw new RuntimeException("couldn't get page, authorization issue");
    }
}
