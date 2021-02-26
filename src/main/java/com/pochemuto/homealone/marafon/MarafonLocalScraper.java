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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class MarafonLocalScraper {
    private static final File IMG_FILE = Path.of("C:\\Users\\Кирилл\\Desktop\\" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".png").toFile();
    public static void main(String[] args) throws IOException {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);
        WebDriver driver = new ChromeDriver(chromeOptions);

        PersonalAccountPage personalAccountPage = new PersonalAccountPage(driver);
        personalAccountPage.openPage("https://lk.lerchekmarafon.ru/");
        personalAccountPage.windowMaximize();

        personalAccountPage.populateLoginNameField("******");
        personalAccountPage.populateLoginPasswordField("*****");
        personalAccountPage.submitLoginForm();
        WaitUtils.waitABit(1000);
        personalAccountPage.openPage(personalAccountPage.generateFoodPageUrl(1, 2));
        WaitUtils.waitABit(2000);
        personalAccountPage.moveToElements();
        ScreenShotUtil screenShotUtil = new ScreenShotUtil(driver);
        screenShotUtil.captureScreenShot("dinner");
    }
}
