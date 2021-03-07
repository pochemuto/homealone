package com.pochemuto.homealone.marafon;

import com.google.common.base.Preconditions;
import com.pochemuto.homealone.ikea.IkeaChecker;
import com.pochemuto.homealone.marafon.page.PersonalAccountPage;
import com.pochemuto.homealone.utils.ScreenShotUtil;
import com.pochemuto.homealone.utils.WaitUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
@Slf4j
@Component
public class MarafonLocalScraper {
    final LocalDate startDate = LocalDate.of(2021, 2, 15);
    private static final String SELENIUM_URL = "http://localhost:4444/wd/hub";
    @Autowired
    private MarafonProperties marafonProperties;

    public  void getData() throws IOException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--force-device-scale-factor=3.0");

        WebDriver driver = new RemoteWebDriver(new URL(SELENIUM_URL), options);

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        log.info("open browser");
        PersonalAccountPage personalAccountPage = new PersonalAccountPage(driver);
        personalAccountPage.openPage("https://lk.lerchekmarafon.ru/");
        personalAccountPage.windowMaximize();


        personalAccountPage.populateLoginNameField(marafonProperties.getLogin());
        personalAccountPage.populateLoginPasswordField(marafonProperties.getPassword());
        personalAccountPage.submitLoginForm();
        log.info("go to account page {}", marafonProperties.getLogin());
        WaitUtils.waitABit(1000);
        personalAccountPage.openPage(personalAccountPage.generateFoodPageUrl(getCurrentWeek(startDate),getCurrentDay()));
        WaitUtils.waitABit(1000);
        log.info("go to food page");
        personalAccountPage.takeScreenShotsOfFood();
        log.info("take screenshots");
        driver.quit();
    }

    public long getCurrentWeek(LocalDate startDate){
        LocalDate currentDate = LocalDate.now();
        return ChronoUnit.WEEKS.between(startDate, currentDate);
    }

    public int getCurrentDay(){
        return LocalDate.now().getDayOfWeek().getValue();
    }

//    public static void main(String[] args) {
//        MarafonLocalScraper week = new MarafonLocalScraper();
//       long w = week.getCurrentWeek(LocalDate.of(2021, 02, 15));
//       int d = week.getCurrentDay();
//
//        System.out.println(w);
//        System.out.println(d);
//    }


}
