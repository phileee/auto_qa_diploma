package org.pages;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class MainPage {

    private final By loginButtonInMain = By.xpath(".//button[text()='Войти в аккаунт']");
    private final By loginButtonInHeader = By.xpath(".//a[@class='AppHeader_header__link__3D_hX' and @href='/account']");

    private final By sauceButton = By.xpath(".//span[text()='Соусы']/parent::div[@class='tab_tab__1SPyG  pt-4 pr-10 pb-4 pl-10 noselect']");
    private final By bunButton = By.xpath(".//span[text()='Булки']/parent::div[@class='tab_tab__1SPyG  pt-4 pr-10 pb-4 pl-10 noselect']");
    private final By fillingButton = By.xpath(".//span[text()='Начинки']/parent::div[@class='tab_tab__1SPyG  pt-4 pr-10 pb-4 pl-10 noselect']");

    private final WebDriver driver;

    public MainPage(WebDriver driver) {
        this.driver = driver;
    }

    public void clickButtonInMain() {
        new WebDriverWait(driver, 3).until(ExpectedConditions.visibilityOfElementLocated(loginButtonInMain));
        driver.findElement(loginButtonInMain).click();
    }

    public void clickButtonInHeader() {
        new WebDriverWait(driver, 3).until(ExpectedConditions.visibilityOfElementLocated(loginButtonInHeader));
        driver.findElement(loginButtonInHeader).click();
    }

    public void clickSauceButton() {
        new WebDriverWait(driver, 3).until(ExpectedConditions.visibilityOfElementLocated(sauceButton));
        driver.findElement(sauceButton).click();
    }

    public void clickBunButton() {
        new WebDriverWait(driver, 3).until(ExpectedConditions.visibilityOfElementLocated(bunButton));
        driver.findElement(bunButton).click();
    }

    public void clickFillingButton() {
        new WebDriverWait(driver, 3).until(ExpectedConditions.visibilityOfElementLocated(fillingButton));
        driver.findElement(fillingButton).click();
    }

    public void assertIsVisible(String section) {
        new WebDriverWait(driver, 3).until(ExpectedConditions.attributeToBe(By.xpath(".//span[text()='"+ section +"']/parent::div"), "class", "tab_tab__1SPyG tab_tab_type_current__2BEPc pt-4 pr-10 pb-4 pl-10 noselect"));
        Assert.assertTrue(driver.findElement(By.xpath(".//span[text()='"+ section +"']/parent::div[@class='tab_tab__1SPyG tab_tab_type_current__2BEPc pt-4 pr-10 pb-4 pl-10 noselect']")).isDisplayed());
    }
}
