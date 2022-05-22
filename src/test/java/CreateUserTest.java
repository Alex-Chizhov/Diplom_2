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


@DisplayName("Creating user")
public class CreateUserTest {

    private final UserClient userClient = new UserClient();
    private Map<String, String> userData;
    private String accessToken;

    @Before
    @DisplayName("Generating user data")
    public void setUp() throws InterruptedException {
        userData = UserDataGenerator.generateUserData();
        // Для избежания ответа 429 Too Many Requests
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    @DisplayName("Creating user")
    public void createUserTest() {
        Response response = userClient.createUser(userData);
        accessToken = response.path("accessToken");
        response
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Creating existing user")
    public void createUserAlreadyExistsTest() {
        userClient.createUser(userData);
        Response response = userClient.createUser(userData);
        response
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message",equalTo("User already exists"));
    }

    @Test
    @DisplayName("Creating user without data")
    public void createUserWithoutData() {
        userData.clear();
        Response response = userClient.createUser(userData);
        response
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message",equalTo("Email, password and name are required fields"));
    }

    @After
    @DisplayName("Delete user")
    public void tearDown() {
        if (accessToken != null) userClient.deleteUser(accessToken);
    }

}
