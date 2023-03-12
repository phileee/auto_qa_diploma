import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.pages.MainPage;

@RunWith(Parameterized.class)
public class ConstructorTest {

    private WebDriver driver;
    private MainPage mainPage;

    public ConstructorTest(String property) {
        System.setProperty("webdriver.chrome.driver", property);
    }

    @Parameterized.Parameters(name = "Запуск драйвера: {0}")
    public static Object[][] getDriver() {
        return new Object[][]{
                {"src/main/resources/yandexdriver.exe"},
                {"src/main/resources/chromedriver.exe"}
        };
    }

    @Before
    @Step("Создаем драйвер и объект страницы")
    public void createDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
        mainPage = new MainPage(driver);
    }

    @Test
    @DisplayName("Переход в секцию соусов в Chrome/YandexBrowser")
    public void clickSauceSuccess() {
        driver.get("https://stellarburgers.nomoreparties.site");
        clickOnSauceButton();
        assertTransition("Соусы");
    }

    @Test
    @DisplayName("Переход в секцию начинок в Chrome/YandexBrowser")
    public void transitionFromProfileToMainSuccess() {
        driver.get("https://stellarburgers.nomoreparties.site");
        clickOnFillingButton();
        assertTransition("Начинки");
    }

    @Test
    @DisplayName("Переход в секцию булок в Chrome/YandexBrowser")
    public void transitionFromProfileToLoginSuccess() {
        driver.get("https://stellarburgers.nomoreparties.site");
        clickOnSauceButton();
        clickOnBunButton();
        assertTransition("Булки");
    }


    @Step("Переход к соусам")
    public void clickOnSauceButton() {
        mainPage.clickSauceButton();
    }

    @Step("Переход к булкам")
    public void clickOnBunButton() {
        mainPage.clickBunButton();
    }

    @Step("Переход к начинкам")
    public void clickOnFillingButton() {
        mainPage.clickFillingButton();
    }

    @Step("Проверка перехода")
    public void assertTransition(String section) {
        mainPage.assertIsVisible(section);
    }

    @After
    @Step("Закрытие браузера")
    public void tearDown() {
        driver.quit();
    }
}
