import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.pojo.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.equalTo;

public class UserLoginTest {

    private User user;
    private String accessToken;

    private final String API_AUTH_REGISTER = "/api/auth/register";
    private final String API_AUTH_LOGIN = "/api/auth/login";
    private final String API_AUTH_USER = "/api/auth/user";

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Step("Создание полного объекта уникального пользователя")
    public User createFullUser() {
        Random random = new Random();
        return new User("phil13@bk.ru" + random.nextInt(10000), "password" + random.nextInt(10000), "phil" + random.nextInt(10000));
    }

    @Step("Изменение имени у ранее созданного объекта пользователя")
    public void modificationUserEmail() {
        Random random = new Random();
        user.setEmail("phil13@bk.ru" + random.nextInt(10000));
    }

    @Step("Изменение пароля у ранее созданного объекта пользователя")
    public void modificationUserPassword() {
        Random random = new Random();
        user.setPassword("password" + random.nextInt(10000));
    }

    @Step("Отправка запроса на создание пользователя \"/api/auth/register\"")
    public Response responseCreateUser(User user){
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(API_AUTH_REGISTER);
    }

    @Step("Отправка запроса на логин пользователя \"/api/auth/login\"")
    public Response responseLoginUser(User user){
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(API_AUTH_LOGIN);
    }

    @Step("Выделяем access-токен после создания пользователя")
    public String extractAccessToken(Response response) {
        return response.then().extract().path("accessToken");
    }

    @Step("Проверка кода ответа")
    public void responseCodeAssert(Response response, int statusCode) {
        response.then()
                .statusCode(statusCode);
    }

    @Step("Проверка тела ответа при логине")
    public void responseBodyAssert(Response response, User user) {
        response.then()
                .assertThat()
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", startsWith("Bearer"))
                .body("refreshToken", notNullValue());
    }

    @Step("Проверка тела ответа при логине пользователя с неправильным логином или паролем")
    public void responseBodyLoginIncorrectUser(Response response, String message) {
        response.then()
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo(message));
    }

    @Test
    @DisplayName("Проверка кода и тела ответа после логина под существующим пользователем")
    public void loginUserResponseBodyContainData() {
        user = createFullUser();
        Response responseRegister = responseCreateUser(user);
        accessToken = extractAccessToken(responseRegister);
        Response responseLogin = responseLoginUser(user);

        responseCodeAssert(responseLogin, 200);
        responseBodyAssert(responseLogin, user);
    }

    @Test
    @DisplayName("Проверка кода и тела ответа после логина под пользователем с неверным логином")
    public void loginIncorrectUserEmailResponseBodyContainsMessage() {
        user = createFullUser();
        Response responseRegister = responseCreateUser(user);
        accessToken = extractAccessToken(responseRegister);
        modificationUserEmail();
        Response responseLogin = responseLoginUser(user);

        responseCodeAssert(responseLogin, 401);
        responseBodyLoginIncorrectUser(responseLogin, "email or password are incorrect");
    }

    @Test
    @DisplayName("Проверка кода и тела ответа после логина под пользователем с неверным паролем")
    public void loginIncorrectPasswordResponseBodyContainsMessage() {
        user = createFullUser();
        Response responseRegister = responseCreateUser(user);
        accessToken = extractAccessToken(responseRegister);
        modificationUserPassword();
        Response responseLogin = responseLoginUser(user);

        responseCodeAssert(responseLogin, 401);
        responseBodyLoginIncorrectUser(responseLogin, "email or password are incorrect");
    }

    @After
    @Step("Удаление пользователя после теста")
    public void deleteUser() {
        if (accessToken != null) {
            given().header("Authorization", accessToken).delete(API_AUTH_USER);
        }
    }
}
