import clients.UserClient;
import data.UserDataGenerator;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;


@DisplayName("Creating user")
public class CreateUserTest {

    private final UserClient userClient = new UserClient();
    private User user;
    private String accessToken;

    @Before
    @DisplayName("Generating user data")
    public void setUp() {
        user = UserDataGenerator.getGeneratedUser();
    }

    @Test
    @DisplayName("Creating user")
    public void createUserTest() {
        Response response = userClient.createUser(user);
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
    public void createUserAlreadyExistsTest(){
        User oldUser = UserDataGenerator.getGeneratedUser();
        userClient.createUser(oldUser);
        
        Response response2 = userClient.createUser(oldUser);
        response2
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message",equalTo("User already exists"));
    }

    @Test
    @DisplayName("Creating user without data")
    public void createUserWithoutData() {
        User user = new User();
        Response response = userClient.createUser(user);
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
