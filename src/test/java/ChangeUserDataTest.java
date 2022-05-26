import clients.UserClient;
import data.UserDataGenerator;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;


@DisplayName("Change user data")
public class ChangeUserDataTest {

    private final UserClient userClient = new UserClient();
    private String accessToken;

    @Before
    @DisplayName("Generating user data and create user, get access token")
    public void setUp(){
        User user = UserDataGenerator.getGeneratedUser();
        Response response = userClient.createUser(user);
        accessToken = response.path("accessToken");
    }

    @Test
    @DisplayName("Change user data with authorization")
    public void changeUserDataWithAuthorizationTest() {
        User newUser = UserDataGenerator.getGeneratedUser();
        Response responseChangedData = userClient.changeUserData(newUser, accessToken);
        responseChangedData
                        .then()
                        .statusCode(200)
                        .body("success", equalTo(true))
                        .body("user.email", equalTo(newUser.email))
                        .body("user.name", equalTo(newUser.name));

    }

    @Test
    @DisplayName("Change user data without authorization")
    public void changeUserDataWithoutAuthorizationTest() {
        User newUser = UserDataGenerator.getGeneratedUser();
        Response responseChangedData = userClient.changeUserData(newUser, "");
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
