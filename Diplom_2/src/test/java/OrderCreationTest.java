import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.pojo.Ingredient;
import org.pojo.Ingredients;
import org.pojo.IngredientsForSerialization;
import org.pojo.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.equalTo;

public class OrderCreationTest {

    private User user;
    private String accessToken;
    private IngredientsForSerialization ingredientsForSerialization;
    private ArrayList<String> ingredientsIdList = new ArrayList<>();

    private final String API_ORDERS = "/api/orders";
    private final String API_AUTH_REGISTER = "/api/auth/register";
    private final String API_AUTH_USER = "/api/auth/user";

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Step("Получение списка ингредиентов и создание объекта со списком из случайных id")
    public void getIdOfIngredients() {
        Response response = given().get("/api/ingredients");
        Ingredients ingredients = response.body().as(Ingredients.class);

        Random random = new Random();
        List<Ingredient> ing = ingredients.getData();
        for (int i = 0; i < 3; i++) {
            ingredientsIdList.add(ing.get(random.nextInt(ing.size())).get_id());
        }
        ingredientsForSerialization = new IngredientsForSerialization(ingredientsIdList);
    }

    @Step("Создание полного объекта уникального пользователя")
    public User createFullUser() {
        Random random = new Random();
        return new User("phil13@bk.ru" + random.nextInt(10000), "password" + random.nextInt(10000), "phil" + random.nextInt(10000));
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

    @Step("Отправка запроса на создание заказа с авторизацией \"/api/orders\"")
    public Response responseCreateOrderWithAuth() {
        return given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .and()
                .body(ingredientsForSerialization)
                .when()
                .post(API_ORDERS);
    }

    @Step("Отправка запроса на создание заказа без авторизации \"/api/orders\"")
    public Response responseCreateOrderWithoutAuth() {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(ingredientsForSerialization)
                .when()
                .post(API_ORDERS);
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

    @Step("Проверка тела ответа при создании заказа с авторизацией")
    public void responseBodyWithAuthAssert(Response response) {
        List<String> ing = ingredientsForSerialization.getIngredients();
        response.then()
                .assertThat()
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.ingredients[0]._id", anyOf(equalTo(ing.get(0)), equalTo(ing.get(1)), equalTo(ing.get(2))))
                .body("order.ingredients[1]._id", anyOf(equalTo(ing.get(0)), equalTo(ing.get(1)), equalTo(ing.get(2))))
                .body("order.ingredients[2]._id", anyOf(equalTo(ing.get(0)), equalTo(ing.get(1)), equalTo(ing.get(2))))
                .body("order._id", notNullValue())
                .body("order.owner.name", equalTo(user.getName()))
                .body("order.owner.email", equalTo(user.getEmail()))
                .body("order.status", equalTo("done"))
                .body("order.name", notNullValue())
                .body("order.number", notNullValue())
                .body("order.price", notNullValue());
    }

    @Step("Проверка тела ответа при создании заказа без авторизации")
    public void responseBodyWithoutAuthAssert(Response response) {
        response.then()
                .assertThat()
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.ingredients", nullValue())
                .body("order._id", nullValue())
                .body("order.owner.name", nullValue())
                .body("order.owner.email", nullValue())
                .body("order.status", nullValue())
                .body("order.name", nullValue())
                .body("order.number", notNullValue())
                .body("order.price", nullValue());
    }

    @Step("Проверка тела ответа при создании заказа без авторизации")
    public void responseBodyErrorMessageAssert(Response response, String message) {
        response.then()
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo(message));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией с проверкой кода и тела ответа")
    public void createOrderWithAuthResponseContainOrder() {
        user = createFullUser();
        Response response = responseCreateUser(user);
        accessToken = extractAccessToken(response);
        getIdOfIngredients();
        Response responseOrder = responseCreateOrderWithAuth();
        responseCodeAssert(responseOrder, 200);
        responseBodyWithAuthAssert(responseOrder);
    }

    @Test
    @DisplayName("Создание заказа без авторизации с проверкой кода и тела ответа")
    public void createOrderWithoutAuthResponseContainOrder() {
        getIdOfIngredients();
        Response responseOrder = responseCreateOrderWithoutAuth();
        responseCodeAssert(responseOrder, 200);
        responseBodyWithoutAuthAssert(responseOrder);
    }

    @Test
    @DisplayName("Создание заказа c невалидным id ингредиента")
    public void createOrderWithIncorrectIngredientResponseCodeIs500() {
        user = createFullUser();
        Response response = responseCreateUser(user);
        accessToken = extractAccessToken(response);

        getIdOfIngredients();
        Random random = new Random();
        ingredientsForSerialization.addIngredients(ingredientsForSerialization.getIngredients().get(0) + random.nextInt(100));

        Response responseOrder = responseCreateOrderWithoutAuth();
        responseCodeAssert(responseOrder, 500);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов и проверка кода и тела ответа")
    public void createOrderWithoutIngredientsResponseBodyContainMessage() {
        user = createFullUser();
        Response response = responseCreateUser(user);
        accessToken = extractAccessToken(response);

        ingredientsForSerialization = new IngredientsForSerialization();
        Response responseOrder = responseCreateOrderWithAuth();

        responseCodeAssert(responseOrder, 400);
        responseBodyErrorMessageAssert(responseOrder, "Ingredient ids must be provided");
    }

    @After
    @Step("Удаление пользователя после теста")
    public void deleteUser() {
        if (accessToken != null) {
            given().header("Authorization", accessToken).delete(API_AUTH_USER);
        }
    }
}
