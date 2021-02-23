package com.pochemuto.homealone.marafon;

import com.pochemuto.homealone.marafon.page.PersonalAccountPage;
import com.pochemuto.homealone.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class MarafonLocalScraper {

    public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();
        PersonalAccountPage personalAccountPage = new PersonalAccountPage(driver);
        personalAccountPage.openLoginPage("https://lk.lerchekmarafon.ru/");

        personalAccountPage.populateLoginNameField("secret.login");
        personalAccountPage.populateLoginPasswordField("secret.pass");
        personalAccountPage.submitLoginForm();

        WaitUtils.waitABit(1000);
        personalAccountPage.navigateToFoodPage(1,2);
    }
}
