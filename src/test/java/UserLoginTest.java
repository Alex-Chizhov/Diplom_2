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



@DisplayName("Login user")
public class UserLoginTest {

    private final UserClient userClient = new UserClient();
    private User user;
    private String accessToken;

    @Before
    @DisplayName("Generating user data and create user")
    public void setUp(){
        user = UserDataGenerator.getGeneratedUser();
        Response response = userClient.createUser(user);
        accessToken = response.path("accessToken");
    }

    @Test
    @DisplayName("User login")
    public void userLoginTest() {
        Response response = userClient.loginUser(user);
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
        User newUser = UserDataGenerator.getGeneratedUser();
        newUser.name = "";
        Response response = userClient.loginUser(newUser);
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
