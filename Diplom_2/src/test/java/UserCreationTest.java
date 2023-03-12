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

public class UserCreationTest {

    private User user;
    private String accessToken;

    private final String API_AUTH_REGISTER = "/api/auth/register";
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

    @Step("Создание объекта уникального пользователя без имени")
    public void createUserWithoutName() {
        Random random = new Random();
        user = new User();
        user.setEmail("phil13@bk.ru" + random.nextInt(10000));
        user.setPassword("password" + random.nextInt(10000));
    }

    @Step("Создание объекта уникального пользователя без пароля")
    public void createUserWithoutPassword() {
        Random random = new Random();
        user = new User();
        user.setName("phil" + random.nextInt(10000));
        user.setEmail("phil13@bk.ru" + random.nextInt(10000));
    }

    @Step("Создание объекта уникального пользователя без почтового адреса")
    public void createUserWithoutEmail() {
        Random random = new Random();
        user = new User();
        user.setName("phil" + random.nextInt(10000));
        user.setPassword("password" + random.nextInt(10000));
    }

    @Step("Отправка запроса на создание пользователя \"/api/auth/register\"")
    public Response responseCreateUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(API_AUTH_REGISTER);
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

    @Step("Проверка тела ответа при создании пользователя")
    public void responseBodyAssert(Response response, User user) {
        response.then()
                .assertThat()
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", startsWith("Bearer"))
                .body("refreshToken", notNullValue());
    }

    @Step("Проверка тела ответа при создании пользователя, который уже существует")
    public void responseBodyOfUserTwiceAssert(Response response, String message) {
        response.then()
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo(message));
    }

    @Test
    @DisplayName("Создание уникального пользователя и проверка тела ответа")
    public void createUserResponseBodyContainsData() {
        user = createFullUser();
        Response response = responseCreateUser(user);
        accessToken = extractAccessToken(response);

        responseCodeAssert(response, 200);
        responseBodyAssert(response, user);
    }

    @Test
    @DisplayName("Создание пользователя, который уже существует, и проверка тела ответа")
    public void createTwiceUserResponseBodyContainsMessage() {
        user = createFullUser();
        Response response = responseCreateUser(user);
        accessToken = extractAccessToken(response);
        Response responseSecond = responseCreateUser(user);

        responseCodeAssert(responseSecond, 403);
        responseBodyOfUserTwiceAssert(responseSecond, "User already exists");
    }

    @Test
    @DisplayName("Создание пользователя без имени и проверка кода и тела ответа")
    public void createUserWithoutNameResponseBodyContainMessage() {
        createUserWithoutName();
        Response response = responseCreateUser(user);

        responseCodeAssert(response, 403);
        responseBodyOfUserTwiceAssert(response, "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Создание пользователя без пароля и проверка кода и тела ответа")
    public void createUserWithoutPasswordResponseBodyContainMessage() {
        createUserWithoutPassword();
        Response response = responseCreateUser(user);

        responseCodeAssert(response, 403);
        responseBodyOfUserTwiceAssert(response, "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Создание пользователя без email и проверка кода и тела ответа")
    public void createUserWithoutEmailResponseBodyContainMessage() {
        createUserWithoutEmail();
        Response response = responseCreateUser(user);
        responseBodyOfUserTwiceAssert(response, "Email, password and name are required fields");
    }

    @After
    @Step("Удаление пользователя после теста")
    public void deleteUser() {
        if (accessToken != null) {
            given().header("Authorization", accessToken).delete(API_AUTH_USER);
        }
    }
}
