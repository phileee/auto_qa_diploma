package org.pages;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ProfilePage {

    private final By profileLink = By.xpath(".//a[text()='Профиль']");
    private final By constructorLink = By.xpath(".//a[@class='AppHeader_header__link__3D_hX' and @href='/']");
    private final By logoLink = By.xpath(".//div[@class='AppHeader_header__logo__2D0X2']/a[@href='/']");

    private final By exitButton = By.xpath(".//button[text()='Выход']");
    private final WebDriver driver;

    public ProfilePage(WebDriver driver) {
        this.driver = driver;
    }

    public void assertDisplayedProfilePage() {
        new WebDriverWait(driver, 3).until(ExpectedConditions.visibilityOfElementLocated(profileLink));
        Assert.assertTrue(driver.findElement(profileLink).isDisplayed());
    }

    public void clickOnConstructor() {
        new WebDriverWait(driver, 3).until(ExpectedConditions.visibilityOfElementLocated(constructorLink));
        driver.findElement(constructorLink).click();
    }

    public void clickOnLogo() {
        new WebDriverWait(driver, 3).until(ExpectedConditions.visibilityOfElementLocated(logoLink));
        driver.findElement(logoLink).click();
    }

    public void clickExit() {
        new WebDriverWait(driver, 3).until(ExpectedConditions.visibilityOfElementLocated(exitButton));
        driver.findElement(exitButton).click();
    }
}
