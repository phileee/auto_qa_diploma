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
import org.pages.AuthorizedPage;
import org.pages.LoginPage;
import org.pages.RegisterPage;
import org.pojo.User;

import java.util.Random;

import static io.restassured.RestAssured.given;

@RunWith(Parameterized.class)
public class RegisterTest {
    private WebDriver driver;

    private User user;
    private String accessToken;
    private final String API_AUTH_LOGIN = "/api/auth/login";
    private final String API_AUTH_USER = "/api/auth/user";

    public RegisterTest(String property) {
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

    @Step("Заполнение полей на странице регистрации и клик")
    public void registration() {
        RegisterPage registerPage = new RegisterPage(driver);
        registerPage.fillingFields(user.getName(), user.getEmail(), user.getPassword());
        registerPage.clickRegisterButton();
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
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";

        accessToken = given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(API_AUTH_LOGIN).then().extract().path("accessToken");

        given().header("Authorization", accessToken).delete(API_AUTH_USER);
    }

    @Step("Создание пользователя с некорректным паролем случайной длины от 1 до 5 символов")
    public void createUserWithShortPassword() {
        createUser();

        StringBuilder incorrectPassword = new StringBuilder("p");
        Random countRandom = new Random();
        int count = countRandom.nextInt(5);
        for(int i = 1; i <= count; i++) {
            incorrectPassword.append(i);
        }

        user.setPassword(incorrectPassword.toString());
    }

    @Step("Ввод пароля и проверка присутствия сообщения об ошибке")
    public void assertErrorMessage() {
        RegisterPage registerPage = new RegisterPage(driver);
        registerPage.fillingFields(user.getPassword());
        registerPage.clickRegisterButton();
        registerPage.assertDisplayedErrorMessage();
    }

    @Before
    @Step("Создаем дравер")
    public void setupDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
    }

    @Test
    @DisplayName("Тест успешной регистрации в Chrome/YandexBrowser")
    public void registrationSuccess() {
        driver.get("https://stellarburgers.nomoreparties.site/register");
        createUser();
        registration();
        login();
        assertLogin();
    }

    @Test
    @DisplayName("Тест возникновения ошибки при некорректном пароле при регистрации в Chrome/YandexBrowser")
    public void errorMessageWithWrongPassword() {
        driver.get("https://stellarburgers.nomoreparties.site/register");
        createUserWithShortPassword();
        assertErrorMessage();
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
