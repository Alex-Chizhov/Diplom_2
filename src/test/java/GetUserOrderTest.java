import clients.OrderClient;
import clients.UserClient;
import data.UserDataGenerator;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;


@DisplayName("Get users order")
public class GetUserOrderTest {

    private final OrderClient orderClient = new OrderClient();
    private final UserClient userClient = new UserClient();
    private String accessToken;

    @Before
    @DisplayName("Generating")
    public void setUp() throws InterruptedException {
        Map<String, String> userData = UserDataGenerator.generateUserData();
        Response response = userClient.createUser(userData);
        accessToken = response.path("accessToken");
        // Для избежания ответа 429 Too Many Requests
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    @DisplayName("Get user's orders with authorization")
    public void getUserOrdersWithAuthorizationTest() {
        Response response = orderClient.getUserOrders(accessToken);
        response
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders", notNullValue());
    }

    @Test
    @DisplayName("Get user's orders without authorization")
    public void getUserOrdersWithoutAuthorizationTest() {
        Response response = orderClient.getUserOrders("");
        response
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @After
    @DisplayName("Delete user")
    public void tearDown() {
        if (accessToken != null) userClient.deleteUser(accessToken);
    }

}
