package com.pochemuto.homealone.marafon.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage {
    private final WebDriver driver;

    @FindBy(xpath = "#login-form")
    private WebElement form;

    @FindBy(css = "#loginform-email")
    private WebElement loginField;

    @FindBy(css = "#loginform-password")
    private WebElement passwordField;

    @FindBy(css = "#login-form button")
    private WebElement submit;

    public LoginPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
        this.driver = driver;
    }

    public LoginPage fillData(String login, String password) {
        loginField.sendKeys(login);
        passwordField.sendKeys(password);
        return this;
    }

    public LoginPage submit() {
        submit.click();
        return this;
    }
}
