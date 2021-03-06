package com.pochemuto.homealone.marafon;

import com.google.common.base.Preconditions;
import com.pochemuto.homealone.marafon.page.PersonalAccountPage;
import com.pochemuto.homealone.utils.ScreenShotUtil;
import com.pochemuto.homealone.utils.WaitUtils;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class MarafonLocalScraper {

    public  void getData() throws IOException {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);
        WebDriver driver = new ChromeDriver(chromeOptions);

        PersonalAccountPage personalAccountPage = new PersonalAccountPage(driver);
        personalAccountPage.openPage("https://lk.lerchekmarafon.ru/");
        personalAccountPage.windowMaximize();

        personalAccountPage.populateLoginNameField("******");
        personalAccountPage.populateLoginPasswordField("****");
        personalAccountPage.submitLoginForm();
        WaitUtils.waitABit(1000);
        personalAccountPage.openPage(personalAccountPage.generateFoodPageUrl(countWeeks(),countDays()));
        WaitUtils.waitABit(1000);
        personalAccountPage.takeScreenShotsOfFood();
        driver.quit();
    }

    public int countWeeks(){
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        final String firstInput = "2021-02-15";
        LocalDate now = LocalDate.now();
        formatter.format(now);
        String currentData = now.toString();
        final String secondInput = currentData;
        final LocalDate firstDate = LocalDate.parse(firstInput, formatter);
        final LocalDate secondDate = LocalDate.parse(secondInput, formatter);
        final int days = (int) ChronoUnit.DAYS.between(firstDate, secondDate);
        int weeks = days/7;
        return weeks;
    }

    public int countDays(){
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        final String firstInput = "2021-02-15";
        LocalDate now = LocalDate.now();
        formatter.format(now);
        String currentData = now.toString();

        final String secondInput = currentData;
        final LocalDate firstDate = LocalDate.parse(firstInput, formatter);
        final LocalDate secondDate = LocalDate.parse(secondInput, formatter);
        final int days = (int) ChronoUnit.DAYS.between(firstDate, secondDate)-countWeeks()*7;
        return days;
    }


}
