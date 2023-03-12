package org.pages;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AuthorizedPage {

    private final By orderButton = By.xpath(".//button[text()='Оформить заказ']");

    private final By profileLink = By.xpath(".//a[@class='AppHeader_header__link__3D_hX' and @href='/account']");
    private final WebDriver driver;

    public AuthorizedPage(WebDriver driver) {
        this.driver = driver;
    }

    public void assertDisplayedOrderButton() {
        new WebDriverWait(driver, 3).until(ExpectedConditions.elementToBeClickable(orderButton));
        Assert.assertTrue(driver.findElement(orderButton).isDisplayed());
    }

    public void clickOnProfileLink() {
        new WebDriverWait(driver, 3).until(ExpectedConditions.elementToBeClickable(profileLink));
        driver.findElement(profileLink).click();
    }

}
