package clients;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.Map;
import static io.restassured.RestAssured.given;


public class UserClient extends BaseClient {

    private final String ENDPOINT = "/api/auth";

    @Step("Create user")
    public Response createUser(Map<String, String> data) {
        return given()
                .header("Content-type", JSON)
                .and()
                .body(data)
                .when()
                .post(BASEURL + ENDPOINT + "/register");
    }

    @Step("Delete user")
    public Response deleteUser(String accessToken) {
        return given()
                .header("Content-type", JSON)
                .header("Authorization", accessToken)
                .delete(BASEURL + ENDPOINT + "/user");
    }


    @Step("User login")
    public Response loginUser(Map<String, String> data) {
        return given()
                .header("Content-type", JSON)
                .and()
                .body(data)
                .when()
                .post(BASEURL + ENDPOINT + "/login");
    }

    @Step("Change user data")
    public Response changeUserData(Map<String, String> data, String  accessToken) {
        return given()
                .header("Content-type", JSON)
                .header("Authorization", accessToken)
                .and()
                .body(data)
                .when()
                .patch(BASEURL + ENDPOINT + "/user");
    }

}
