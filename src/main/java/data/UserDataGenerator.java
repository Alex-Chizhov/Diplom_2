package data;

import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import java.util.HashMap;
import java.util.Map;


public class UserDataGenerator {

    @Step("Generating user data")
    public static Map <String, String> generateUserData() {
        HashMap <String, String> userData = new HashMap <>();
        Faker faker = new Faker();
        userData.put("email", faker.internet().emailAddress());
        userData.put("password", faker.internet().password());
        userData.put("name", faker.name().firstName());

        return userData;
    }
}
