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
public class TransitionsTest {

    private WebDriver driver;
    private User user;
    private String accessToken;

    private final String API_AUTH_REGISTER = "/api/auth/register";
    private final String API_AUTH_USER = "/api/auth/user";

    public TransitionsTest(String property) {
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

    @Step("Переход на экран логина через кнопку «Личный кабинет»")
    public void clickOnButtonInHeader() {
        MainPage mainPage = new MainPage(driver);
        mainPage.clickButtonInHeader();
    }

    @Step("Заполнение полей на странице логина и вход")
    public void login() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.fillingFields(user.getEmail(), user.getPassword());
        loginPage.clickLoginButton();
    }

    @Step("Переход в профиль")
    public void transitionToProfile() {
        AuthorizedPage authorizedPage = new AuthorizedPage(driver);
        authorizedPage.clickOnProfileLink();
    }

    @Step("Проверка входа в профиль")
    public void assertProfile() {
        ProfilePage profilePage = new ProfilePage(driver);
        profilePage.assertDisplayedProfilePage();
    }

    @Step("Переход в конструктор по клику на конструктор")
    public void transitionToConstructorByLink() {
        ProfilePage profilePage = new ProfilePage(driver);
        profilePage.clickOnConstructor();
    }

    @Step("Переход в конструктор по клику на лого")
    public void transitionToConstructorByLogo() {
        ProfilePage profilePage = new ProfilePage(driver);
        profilePage.clickOnLogo();
    }

    @Step("Проверка перехода в конструктор")
    public void assertConstructor() {
        AuthorizedPage authorizedPage = new AuthorizedPage(driver);
        authorizedPage.assertDisplayedOrderButton();
    }

    @Step("Выход из профиля")
    public void exitFromProfile() {
        ProfilePage profilePage = new ProfilePage(driver);
        profilePage.clickExit();
    }

    @Step("Проверка перехода в конструктор")
    public void assertExit() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.assertDisplayedLoginPage();
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
    @DisplayName("Переход в личный кабинет в Chrome/YandexBrowser")
    public void transitionToProfilePageSuccess() {
        driver.get("https://stellarburgers.nomoreparties.site");
        clickOnButtonInHeader();
        login();
        transitionToProfile();
        assertProfile();
    }

    @Test
    @DisplayName("Переход из личного кабинета в конструктор в Chrome/YandexBrowser")
    public void transitionFromProfileToConstructorSuccess() {
        driver.get("https://stellarburgers.nomoreparties.site");
        clickOnButtonInHeader();
        login();
        transitionToProfile();
        transitionToConstructorByLink();
        assertConstructor();
    }

    @Test
    @DisplayName("Переход из личного кабинета на главную по клику на лого в Chrome/YandexBrowser")
    public void transitionFromProfileToMainSuccess() {
        driver.get("https://stellarburgers.nomoreparties.site");
        clickOnButtonInHeader();
        login();
        transitionToProfile();
        transitionToConstructorByLogo();
        assertConstructor();
    }

    @Test
    @DisplayName("Проверка выхода из профиля в Chrome/YandexBrowser")
    public void transitionFromProfileToLoginSuccess() {
        driver.get("https://stellarburgers.nomoreparties.site");
        clickOnButtonInHeader();
        login();
        transitionToProfile();
        exitFromProfile();
        assertExit();
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
