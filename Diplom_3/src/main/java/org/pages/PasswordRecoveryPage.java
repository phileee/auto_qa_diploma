package org.pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PasswordRecoveryPage {

    private final By loginButton = By.xpath(".//a[@class='Auth_link__1fOlj']");
    private final WebDriver driver;

    public PasswordRecoveryPage(WebDriver driver) {
        this.driver = driver;
    }

    public void clickLoginButton() {
        new WebDriverWait(driver, 3).until(ExpectedConditions.visibilityOfElementLocated(loginButton));
        driver.findElement(loginButton).click();
    }
}
