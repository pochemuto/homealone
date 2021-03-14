package com.pochemuto.homealone.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WaitUtils {

    public static void waitABit (int timeout) {
        try{
            Thread.sleep(timeout);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

public static void waitToBeVisible (WebDriver driver, WebElement element){
        element = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(element));
}

}
