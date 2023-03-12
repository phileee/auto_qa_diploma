import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.pages.*;
import org.pojo.User;

import java.util.Random;

import static io.restassured.RestAssured.given;

@RunWith(Parameterized.class)
public class LoginTest {

    private WebDriver driver;
    private User user;
    private String accessToken;

    private final String API_AUTH_REGISTER = "/api/auth/register";
    private final String API_AUTH_USER = "/api/auth/user";

    public LoginTest(String property) {
        System.setProperty("webdriver.chrome.driver", property);
    }

    @Parameterized.Parameters(name = "Запуск драйвера: {0}")
    public static Object[][] getDriver() {
        return new Object[][]{
                {"src/main/resources/yandexdriver.exe"},
                {"src/main/resources/chromedriver.exe"}
        };
    }

    @Step("Создание объекта пользователя")
    public void createUser() {
        Random random = new Random();
        user = new User("phil13@bk.ru" + random.nextInt(100000), "password" + random.nextInt(100000), "phil" + random.nextInt(100000));
    }

    @Step("Переход на экран логина по кнопке «Войти в аккаунт» на главной")
    public void clickOnButtonOrder() {
        MainPage mainPage = new MainPage(driver);
        mainPage.clickButtonInMain();
    }

    @Step("Переход на экран логина через кнопку «Личный кабинет»")
    public void clickOnButtonInHeader() {
        MainPage mainPage = new MainPage(driver);
        mainPage.clickButtonInHeader();
    }

    @Step("Переход на экран логина через кнопку в форме восстановления пароля")
    public void clickOnLoginButtonPasswordRecoveryPage() {
        PasswordRecoveryPage passwordRecoveryPage = new PasswordRecoveryPage(driver);
        passwordRecoveryPage.clickLoginButton();
    }

    @Step("Переход на экран логина по кнопке «Войти» на экране регистрации")
    public void clickOnLoginButtonRegisterPage() {
        RegisterPage registerPage = new RegisterPage(driver);
        registerPage.clickLoginButton();
    }

    @Step("Заполнение полей на странице логина и вход")
    public void login() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.fillingFields(user.getEmail(), user.getPassword());
        loginPage.clickLoginButton();
    }

    @Step("Проверка входа на авторизованную часть сайта")
    public void assertLogin() {
        AuthorizedPage authorizedPage = new AuthorizedPage(driver);
        authorizedPage.assertDisplayedOrderButton();
    }

    @Step("Удаление пользователя")
    public void deleteUser() {
        given().header("Authorization", accessToken).delete(API_AUTH_USER);
    }

    @Before
    @Step("Создаем драйвер и пользователя через API")
    public void createUserApi() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);

        createUser();

        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        accessToken = given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(API_AUTH_REGISTER).then().extract().path("accessToken");
    }

    @Test
    @DisplayName("Вход по кнопке «Войти в аккаунт» на главной в Chrome/YandexBrowser")
    public void loginButtonOrderSuccess() {
        driver.get("https://stellarburgers.nomoreparties.site");
        clickOnButtonInHeader();
        login();
        assertLogin();
    }

    @Test
    @DisplayName("Вход через кнопку «Личный кабинет» на главной в Chrome/YandexBrowser")
    public void loginButtonInHeaderSuccess() {
        driver.get("https://stellarburgers.nomoreparties.site");
        clickOnButtonOrder();
        login();
        assertLogin();
    }

    @Test
    @DisplayName("Вход через кнопку в форме восстановления пароля в Chrome/YandexBrowser")
    public void loginFromPasswordRecoveryPageSuccess() {
        driver.get("https://stellarburgers.nomoreparties.site/forgot-password");
        clickOnLoginButtonPasswordRecoveryPage();
        login();
        assertLogin();
    }

    @Test
    @DisplayName("Вход через кнопку в форме регистрации в Chrome/YandexBrowser")
    public void loginFromRegisterPageSuccess() {
        driver.get("https://stellarburgers.nomoreparties.site/register");
        clickOnLoginButtonRegisterPage();
        login();
        assertLogin();
    }

    @After
    @Step("Закрытие браузера")
    public void tearDown() {
        if (accessToken != null) {
            deleteUser();
        }
        driver.quit();
    }
}
