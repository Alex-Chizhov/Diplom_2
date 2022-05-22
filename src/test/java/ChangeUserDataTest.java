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


@DisplayName("Change user data")
public class ChangeUserDataTest {

    private final UserClient userClient = new UserClient();
    private String accessToken;

    @Before
    @DisplayName("Generating user data and create user, get access token")
    public void setUp() throws InterruptedException {
        Map<String, String> userData = UserDataGenerator.generateUserData();
        Response response = userClient.createUser(userData);
        accessToken = response.path("accessToken");
        // Для избежания ответа 429 Too Many Requests
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    @DisplayName("Change user data with authorization")
    public void changeUserDataWithAuthorizationTest() {
        Map<String, String> newUserData = UserDataGenerator.generateUserData();
        Response responseChangedData = userClient.changeUserData(newUserData, accessToken);
        responseChangedData
                        .then()
                        .statusCode(200)
                        .body("success", equalTo(true))
                        .body("user.email", equalTo(newUserData.get("email")))
                        .body("user.name", equalTo(newUserData.get("name")));

    }

    @Test
    @DisplayName("Change user data without authorization")
    public void changeUserDataWithoutAuthorizationTest() {
        Map<String, String> newUserData = UserDataGenerator.generateUserData();
        Response responseChangedData = userClient.changeUserData(newUserData, "");
        responseChangedData
                        .then()
                        .statusCode(401)
                        .body("success", equalTo(false))
                        .body("message", equalTo("You should be authorised"));
    }

    @After
    @DisplayName("Delete user")
    public void tearDown() {
        userClient.deleteUser(accessToken);
    }

}
