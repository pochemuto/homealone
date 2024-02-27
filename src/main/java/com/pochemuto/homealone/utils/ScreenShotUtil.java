package com.pochemuto.homealone.utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;

public class ScreenShotUtil {
    private WebDriver driver;

    public ScreenShotUtil(WebDriver driver){
        this.driver = driver;
    }
    public void captureScreenShot(String fileName) throws IOException {
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        File targetFile = new File("./Screenshots/" + fileName + ".png");
        FileUtils.copyFile(scrFile, targetFile);

    }
}
