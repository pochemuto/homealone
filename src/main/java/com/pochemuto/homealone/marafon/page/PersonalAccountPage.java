package com.pochemuto.homealone.marafon.page;

import com.pochemuto.homealone.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import javax.swing.*;
import java.util.concurrent.TimeUnit;

public class PersonalAccountPage {
    //login page elements:
    @FindBy(xpath = "//*[@id=\"loginform-email\"]")
    private WebElement loginField;
    @FindBy(xpath = "//*[@id=\"loginform-password\"]")
    private WebElement passwordField;
    @FindBy(xpath = "//*[@id=\"login-form\"]//button")
    private WebElement submitButton;

    //account page elements:
    @FindBy(xpath = "/html/body/div[2]/div/div[2]/div/a[1]/span[1]")
    private WebElement currentCourse;
    @FindBy(xpath = "//a[2]/div")
    private WebElement food;
    @FindBy(xpath = "//*[contains(text(),'1-я неделя')]")
    private WebElement firstWeek;
    @FindBy(xpath = "//*[contains(text(),'2-я неделя')]")
    private WebElement secondWeek;
    @FindBy(xpath = "//*[contains(text(),'День 1')]")
    private WebElement firsDay;
    @FindBy(xpath = "//*[contains(text(),'День 2')]")
    private WebElement secondDay;
    @FindBy(xpath = "//*[contains(text(),'День 3')]")
    private WebElement thirdDay;
    @FindBy(xpath = "//*[contains(text(),'День 4')]")
    private WebElement fourthDay;
    @FindBy(xpath = "//*[contains(text(),'День 5')]")
    private WebElement fifththDay;
    @FindBy(xpath = "//*[contains(text(),'День 6')]")
    private WebElement sixthDay;
    @FindBy(xpath = "//*[contains(text(),'День 7')]")
    private WebElement sevenththDay;
    @FindBy(xpath = "/html/body/div[2]/div/div[2]/div[1]/div[4]/div[2]")
    private WebElement breakfast;

    WebDriver driver;
    public PersonalAccountPage(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    public PersonalAccountPage openLoginPage(String url){
        driver.get(url);
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
        WaitUtils.waitABit(1000);
       // driver.get("https://lk.lerchekmarafon.ru/marafon/food");
        return this;
    }
    public PersonalAccountPage navigateToFoodPage(int week, int day) {
        driver.get("https://lk.lerchekmarafon.ru/marafon/food");
        driver.manage().timeouts().pageLoadTimeout(2, TimeUnit.SECONDS);

        food.click();
        WaitUtils.waitABit(500);
        switch (week) {
            case 1:
                firstWeek.click();
                break;
            case 2:
                secondWeek.click();
                break;
        }
        WaitUtils.waitABit(500);
        switch (day) {
            case 1:
                firsDay.click();
                break;
            case 2:
                secondDay.click();
                break;
            case 3:
                thirdDay.click();
                break;
            case 4:
                fourthDay.click();
                break;
            case 5:
                fifththDay.click();
                break;
            case 6:
                sixthDay.click();
                break;
            case 7:
                sevenththDay.click();
                break;
        }
        return this;
    }



}
