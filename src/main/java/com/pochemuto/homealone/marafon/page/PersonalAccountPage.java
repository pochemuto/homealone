package com.pochemuto.homealone.marafon.page;

import com.pochemuto.homealone.utils.WaitUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

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

    public PersonalAccountPage moveToElements(){
        Actions actions = new Actions(driver);
        actions.moveToElement(dinner);
        actions.perform();
        return this;
    }



}
