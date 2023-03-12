package org.pages;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {

    private final By emailField = By.xpath(".//div[@class='input pr-6 pl-6 input_type_text input_size_default']/input");
    private final By passwordField = By.xpath(".//input[@type='password']");
    private final By loginButton = By.xpath(".//button[text()='Войти']");

    private final WebDriver driver;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public void fillingFields(String email, String password) {
        new WebDriverWait(driver, 3).until(ExpectedConditions.elementToBeClickable(loginButton));
        driver.findElement(emailField).sendKeys(email);
        driver.findElement(passwordField).sendKeys(password);
    }

    public void clickLoginButton() {
        new WebDriverWait(driver, 3).until(ExpectedConditions.elementToBeClickable(loginButton));
        driver.findElement(loginButton).click();
    }

    public void assertDisplayedLoginPage() {
        new WebDriverWait(driver, 3).until(ExpectedConditions.visibilityOfElementLocated(loginButton));
        Assert.assertTrue(driver.findElement(loginButton).isDisplayed());
    }
}
