import clients.UserClient;
import clients.OrderClient;
import data.UserDataGenerator;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.*;


@DisplayName("Create order")
public class CreateOrderTest {

    private final OrderClient orderClient = new OrderClient();
    private  Order orderIngredientsIdList;
    private final UserClient userClient = new UserClient();
    private String accessToken;

    @Before
    @DisplayName("Generating user data and registration user, get access token")
    public void setUp() throws InterruptedException {
        orderIngredientsIdList = new Order(orderClient.getIngredientsId());
        Map<String, String> userData = UserDataGenerator.generateUserData();
        Response response = userClient.createUser(userData);
        accessToken = response.path("accessToken");
        // Для избежания ответа 429 Too Many Requests
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    @DisplayName("Creating order without authorization")
    public void createOrderWithoutAuthorizationTest() {
        Response response = orderClient.createOrder(orderIngredientsIdList, "");
        response
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", notNullValue());
    }

    @Test
    @DisplayName("Creating order with authorization")
    public void createOrderWithAuthorizationTest() {
        Response response = orderClient.createOrder(orderIngredientsIdList, accessToken);
        response
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", notNullValue());
    }
    @Test
    @DisplayName("Creating order without ingredients")
    public void createOrderWithoutIngredientsTest() {
        Response response = orderClient.createOrder(new Order(), "");
        response
                .then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Creating order with invalid id ingredient (hash)")
    public void createOrderWithInvalidIdIngredientTest() {
        ArrayList<String> listWrongId = new ArrayList<>();
        listWrongId.add("000000000");

        Response response = orderClient.createOrder(new Order(listWrongId), "");
        response
                .then()
                .statusCode(500)
                .body("$", hasItem("Internal Server Error"));
    }

    @After
    @DisplayName("Delete user")
    public void tearDown() {
        if (accessToken != null) userClient.deleteUser(accessToken);
    }
}
