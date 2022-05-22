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



@DisplayName("Login user")
public class UserLoginTest {

    private final UserClient userClient = new UserClient();
    private Map<String, String> userData;
    private String accessToken;

    @Before
    @DisplayName("Generating user data and create user")
    public void setUp() throws InterruptedException {
        userData = UserDataGenerator.generateUserData();
        Response response = userClient.createUser(userData);
        accessToken = response.path("accessToken");
        // Для избежания ответа 429 Too Many Requests
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    @DisplayName("User login")
    public void userLoginTest() {
        userData.remove("name");
        Response response = userClient.loginUser(userData);
        response
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("user", notNullValue());
    }

    @Test
    @DisplayName("User login with incorrect login and password")
    public void userLoginIncorrectLoginPasswordTest() {
        Map<String, String> newIncorrectUserData = UserDataGenerator.generateUserData();
        newIncorrectUserData.remove("name");
        Response response = userClient.loginUser(newIncorrectUserData);
        response
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @After
    @DisplayName("Delete user")
    public void tearDown() {
        userClient.deleteUser(accessToken);
    }
}
