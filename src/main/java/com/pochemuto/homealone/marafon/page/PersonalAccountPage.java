package com.pochemuto.homealone.marafon.page;

import com.pochemuto.homealone.utils.ScreenShotUtil;
import com.pochemuto.homealone.utils.WaitUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.io.IOException;

public class PersonalAccountPage {
    //login page elements:
    @FindBy(xpath = "//*[@id=\"loginform-email\"]")
    private WebElement loginField;
    @FindBy(xpath = "//*[@id=\"loginform-password\"]")
    private WebElement passwordField;
    @FindBy(xpath = "//*[@id=\"login-form\"]//button")
    private WebElement submitButton;
    //food page elements:
    @FindBy(xpath = "//*[text()='Завтрак']")
    private WebElement breakfast;
    @FindBy(xpath = "//*[text()='Перекус 1']")
    private WebElement brunch;
    @FindBy(xpath = "//*[text()='Обед']")
    private WebElement lunch;
    @FindBy(xpath = "//*[text()='Ужин']")
    private WebElement dinner;



    WebDriver driver;
    public PersonalAccountPage(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    public PersonalAccountPage openPage(String url){
        driver.get(url);
        return this;
    }

    public PersonalAccountPage windowMaximize(){
        driver.manage().window().maximize();
        return this;
    }

    public PersonalAccountPage populateLoginNameField(String name) {
        loginField.sendKeys(name);
        return this;
    }
    public PersonalAccountPage populateLoginPasswordField (String pass){
        passwordField.sendKeys(pass);
        return this;
    }
    public PersonalAccountPage submitLoginForm (){
        submitButton.click();
         return this;
    }


    public String generateFoodPageUrl(int week, int day) {
        String foodUrl;
        return  foodUrl = "https://lk.lerchekmarafon.ru/marafon/food?week=" + week + "&number=" + day;
    }

    public PersonalAccountPage moveToElements(WebElement element)  {
        Actions actions = new Actions(driver);
        actions.sendKeys(Keys.END).build().perform();
        actions.moveToElement(dinner);
        actions.perform();
        WaitUtils.waitABit(500);
        JavascriptExecutor js = (JavascriptExecutor)driver;
        js.executeScript("arguments[0].scrollIntoView();", element);

        return this;
    }


    public PersonalAccountPage takeScreenShotsOfFood() throws IOException {
        ScreenShotUtil screenShotUtil = new ScreenShotUtil(driver);

        moveToElements(dinner);
        WaitUtils.waitABit(500);
        screenShotUtil.captureScreenShot(dinner.getText());
        WaitUtils.waitABit(500);

        moveToElements(lunch);
        WaitUtils.waitABit(500);
        screenShotUtil.captureScreenShot(lunch.getText());
        WaitUtils.waitABit(500);


        moveToElements(brunch);
        WaitUtils.waitABit(500);
        screenShotUtil.captureScreenShot(brunch.getText());
        WaitUtils.waitABit(500);

        moveToElements(breakfast);
        WaitUtils.waitABit(500);
        screenShotUtil.captureScreenShot(breakfast.getText());
        WaitUtils.waitABit(500);
        return this;
    }



}
