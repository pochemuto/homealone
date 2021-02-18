package com.pochemuto.homealone.marafon;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;
import com.pochemuto.homealone.marafon.page.LoginPage;
import com.pochemuto.homealone.marafon.page.SelectProgramPage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MafaronScraper {

    private static final File IMG_FILE = Path.of(System.getProperty("user.home"), "Desktop", "img " + LocalDateTime.now().toString() + ".png").toFile();
    private static final String SELENIUM_URL = "http://localhost:4444/wd/hub";
    @Autowired
    private MarafonProperties properties;

    public static void main(String[] args) throws IOException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--force-device-scale-factor=3.0");

        WebDriver driver = new RemoteWebDriver(new URL(SELENIUM_URL), options);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.navigate().to("https://lk.lerchekmarafon.ru/marafon/food?week=1&number=2");
        var loginPage = new LoginPage(driver);
        var selectPage = new SelectProgramPage(driver);

        loginPage.fillData("pochemuto@gmail.com", "PASSWORD").submit();


        System.out.println(selectPage.getHeader());

        driver.navigate().to("https://lk.lerchekmarafon.ru/marafon/food?week=1&number=2");

        driver.manage().window().maximize();
        takeScreenshot(driver);

        System.out.println(driver.getTitle());
        var cookies = driver.manage().getCookies();
        System.out.println(cookies);
        driver.close();
    }

    private static void takeScreenshot(WebDriver driver) {

        File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        WebElement element = driver.findElement(By.cssSelector("div.food_list > div:nth-child(3)"));
//
        boolean moved = file.renameTo(IMG_FILE);
        Preconditions.checkState(moved);
        System.out.println(file.getAbsolutePath());
    }
}
