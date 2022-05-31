import clients.UserClient;
import clients.OrderClient;
import data.UserDataGenerator;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.Order;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.*;


@DisplayName("Create order")
public class CreateOrderTest {

    private final OrderClient orderClient = new OrderClient();
    private  Order orderIngredientsIdList;
    private final UserClient userClient = new UserClient();
    private String accessToken;
    private User user;

    @Before
    @DisplayName("Generating user data and registration user, get access token")
    public void setUp(){
        orderIngredientsIdList = new Order(orderClient.getIngredientsId());
        user = UserDataGenerator.getGeneratedUser();
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
        Response response1 = userClient.createUser(user);
        accessToken = response1.path("accessToken");

        Response response2 = orderClient.createOrder(orderIngredientsIdList, accessToken);
        response2
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
