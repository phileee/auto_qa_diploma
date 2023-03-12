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

public class OrderGettingTest {

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
    public Response responseCreateUser(User user){
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(API_AUTH_REGISTER);
    }

    @Step("Отправка запроса на создание заказа с авторизацией \"/api/orders\"")
    public void responseCreateOrderWithAuth() {
            given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .and()
                .body(ingredientsForSerialization)
                .when()
                .post(API_ORDERS);
    }

    @Step("Отправка запроса на получение списка заказов конкретного пользователя с авторизацией")
    public Response responseGetOrderWithAuth() {
        return given()
                .header("Authorization", accessToken)
                .when()
                .get(API_ORDERS);
    }

    @Step("Отправка запроса на получение списка заказов без авторизации")
    public Response responseGetOrderWithoutAuth() {
        return given()
                .get(API_ORDERS);
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

    @Step("Проверка тела ответа с авторизацией")
    public void responseBodyWithAuthAssert(Response response) {
        List<String> ing = ingredientsForSerialization.getIngredients();
        response.then()
                .assertThat()
                .body("success", equalTo(true))
                .body("orders[0].ingredients[0]", anyOf(equalTo(ing.get(0)), equalTo(ing.get(1)), equalTo(ing.get(2))))
                .body("orders[0].ingredients[1]", anyOf(equalTo(ing.get(0)), equalTo(ing.get(1)), equalTo(ing.get(2))))
                .body("orders[0].ingredients[2]", anyOf(equalTo(ing.get(0)), equalTo(ing.get(1)), equalTo(ing.get(2))))
                .body("orders[0]._id", notNullValue())
                .body("orders[0].status", notNullValue())
                .body("orders[0].number", notNullValue())
                .body("orders[0].createdAt", notNullValue())
                .body("orders[0].updatedAt", notNullValue())
                .body("total", notNullValue())
                .body("totalToday", notNullValue());
    }

    @Step("Проверка тела ответа без авторизации")
    public void responseBodyErrorMessageAssert(Response response, String message) {
        response.then()
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo(message));
    }

    @Test
    @DisplayName("Получение списка с авторизацией с проверкой кода и тела ответа")
    public void getOrdersWithAuthResponseBodyContainsOrderList() {
        user = createFullUser();
        Response response = responseCreateUser(user);
        accessToken = extractAccessToken(response);
        getIdOfIngredients();
        responseCreateOrderWithAuth();
        Response responseOrderList = responseGetOrderWithAuth();

        responseCodeAssert(responseOrderList, 200);
        responseBodyWithAuthAssert(responseOrderList);
    }

    @Test
    @DisplayName("Получение списка без авторизации с проверкой тела ответа")
    public void getOrdersWithoutAuthResponseBodyContainsMessage() {
        Response responseOrderList = responseGetOrderWithoutAuth();

        responseCodeAssert(responseOrderList, 401);
        responseBodyErrorMessageAssert(responseOrderList, "You should be authorised");
    }

    @After
    @Step("Удаление пользователя после теста")
    public void deleteUser() {
        if (accessToken != null) {
            given().header("Authorization", accessToken).delete(API_AUTH_USER);
        }
    }
}
