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
import static org.hamcrest.CoreMatchers.equalTo;

public class UserModificationTest {

    private User user;
    private String accessToken;
    private String accessTokenOfSecondUser;

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

    @Step("Создание объекта пользователя с новой почтой")
    public User userWithNewEmail() {
        Random random = new Random();
        User user = new User();
        user.setEmail("phil13@bk.ru" + random.nextInt(10000));
        return user;
    }

    @Step("Создание объекта пользователя с новым именем")
    public User userWithNewName() {
        Random random = new Random();
        User user = new User();
        user.setName("phil13@bk.ru" + random.nextInt(10000));
        return user;
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

    @Step("Отправка запроса на изменение пользователя \"/api/auth/login\" с токеном")
    public Response responsePatchUserWithToken(User user, String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .patch(API_AUTH_USER);
    }

    @Step("Отправка запроса на изменение пользователя \"/api/auth/login\" без токена")
    public Response responsePatchUserWithoutToken(User user) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .patch(API_AUTH_USER);
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

    @Step("Проверка логина в теле ответа")
    public void responseBodyEmailAssert(Response response, User oldUser, User newUser) {
        response.then()
                .assertThat()
                .body("success", equalTo(true))
                .body("user.email", equalTo(newUser.getEmail()))
                .body("user.name", equalTo(oldUser.getName()));
    }

    @Step("Проверка имени в теле ответа")
    public void responseBodyNameAssert(Response response, User oldUser, User newUser) {
        response.then()
                .assertThat()
                .body("success", equalTo(true))
                .body("user.email", equalTo(oldUser.getEmail()))
                .body("user.name", equalTo(newUser.getName()));
    }

    @Step("Проверка тела ответа при логине пользователя с неправильным логином или паролем")
    public void responseBodyLoginIncorrectUser(Response response, String message) {
        response.then()
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo(message));
    }

    @Test
    @DisplayName("Проверка кода и тела ответа при изменении логина пользователя")
    public void updateUserEmailResponseBodyContainsData() {
        user = createFullUser();
        Response response = responseCreateUser(user);
        String accessTokenForUpdate = extractAccessToken(response);
        accessToken = accessTokenForUpdate;
        User userWithEmail = userWithNewEmail();
        Response responsePatch = responsePatchUserWithToken(userWithEmail, accessTokenForUpdate);

        responseCodeAssert(responsePatch, 200);
        responseBodyEmailAssert(responsePatch, user, userWithEmail);
    }

    @Test
    @DisplayName("Проверка кода и тела ответа при изменении имени пользователя")
    public void updateUserNameResponseBodyContainsData() {
        user = createFullUser();
        Response response = responseCreateUser(user);
        String accessTokenForUpdate = extractAccessToken(response);
        accessToken = accessTokenForUpdate;
        User userWithName = userWithNewName();
        Response responsePatch = responsePatchUserWithToken(userWithName, accessTokenForUpdate);

        responseCodeAssert(responsePatch, 200);
        responseBodyNameAssert(responsePatch, user, userWithName);
    }

    @Test
    @DisplayName("Проверка кода и наличия сообщения об ошибке при попытке изменить логин без отправки токена")
    public void updateUserEmailWithoutTokenResponseBodyContainsMessage() {
        user = createFullUser();
        Response response = responseCreateUser(user);
        accessToken = extractAccessToken(response);
        User userWithEmail = userWithNewEmail();
        Response responsePatch = responsePatchUserWithoutToken(userWithEmail);

        responseCodeAssert(responsePatch, 401);
        responseBodyLoginIncorrectUser(responsePatch, "You should be authorised");
    }

    @Test
    @DisplayName("Проверка кода и наличия сообщения об ошибке при попытке изменить имя без отправки токена")
    public void updateUserNameWithoutTokenResponseBodyContainsMessage() {
        user = createFullUser();
        Response response = responseCreateUser(user);
        accessToken = extractAccessToken(response);
        User userWithName = userWithNewName();
        Response responsePatch = responsePatchUserWithoutToken(userWithName);

        responseCodeAssert(responsePatch, 401);
        responseBodyLoginIncorrectUser(responsePatch, "You should be authorised");
    }

    @Test
    @DisplayName("Проверка кода и наличия сообщения об ошибке при попытке изменить логин на уже имеющийся")
    public void updateUserUsedEmailResponseBodyContainsMessage() {
        user = createFullUser();
        Response response = responseCreateUser(user);
        accessToken = extractAccessToken(response);

        User userSecond = createFullUser();
        Response responseSecond = responseCreateUser(userSecond);
        accessTokenOfSecondUser = extractAccessToken(responseSecond);

        User userWithUsedEmail = new User();
        userWithUsedEmail.setEmail(user.getEmail());

        Response responsePatch = responsePatchUserWithToken(userWithUsedEmail, accessTokenOfSecondUser);

        responseCodeAssert(responsePatch, 403);
        responseBodyLoginIncorrectUser(responsePatch, "User with such email already exists");
    }

    @After
    @Step("Удаление пользователя после теста")
    public void deleteUser() {
        if (accessToken != null) {
            given().header("Authorization", accessToken).delete("/api/auth/user");
        }
        if (accessTokenOfSecondUser != null) {
            given().header("Authorization", accessTokenOfSecondUser).delete(API_AUTH_USER);
        }
    }
}
