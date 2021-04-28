package com.pochemuto.homealone.marafon.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SelectProgramPage {
    private final WebDriver driver;

    @FindBy(css = "div.change-program__action > h1")
    private WebElement header;

    public SelectProgramPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
        this.driver = driver;
    }

    public String getHeader() {
        return header.getText();
    }
}
