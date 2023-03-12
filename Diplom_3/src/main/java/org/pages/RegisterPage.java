package org.pages;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class RegisterPage {

    private final WebDriver driver;

    private final By fields = By.xpath(".//input[@class='text input__textfield text_type_main-default']");
    private final By registrationButton = By.xpath(".//button[text()='Зарегистрироваться']");

    private final By errorMessage = By.xpath(".//p[@class='input__error text_type_main-default']");

    private final By loginButton = By.xpath(".//a[@class='Auth_link__1fOlj']");
    public RegisterPage(WebDriver driver) {
        this.driver = driver;
    }

    public void fillingFields(String name, String email, String password) {
        new WebDriverWait(driver, 3).until(ExpectedConditions.elementToBeClickable(registrationButton));
        List<WebElement> fieldsList = driver.findElements(fields);
        fieldsList.get(0).sendKeys(name);
        fieldsList.get(1).sendKeys(email);
        fieldsList.get(2).sendKeys(password);
    }

    public void fillingFields(String password) {
        new WebDriverWait(driver, 3).until(ExpectedConditions.elementToBeClickable(registrationButton));
        List<WebElement> fieldsList = driver.findElements(fields);
        fieldsList.get(2).sendKeys(password);
    }

    public void clickRegisterButton() {
        driver.findElement(registrationButton).click();
    }

    public void assertDisplayedErrorMessage() {
        new WebDriverWait(driver, 3).until(ExpectedConditions.visibilityOfElementLocated(errorMessage));
        Assert.assertTrue(driver.findElement(errorMessage).isDisplayed());
    }

    public void clickLoginButton() {
        new WebDriverWait(driver, 3).until(ExpectedConditions.visibilityOfElementLocated(loginButton));
        driver.findElement(loginButton).click();
    }
}
