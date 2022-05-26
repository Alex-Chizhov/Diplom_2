package data;

import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import model.User;


public class UserDataGenerator {

    @Step("User generating")
    public static User getGeneratedUser() {
        Faker faker = new Faker();
        return new User(
                faker.internet().emailAddress(),
                faker.internet().password(),
                faker.name().firstName());
    }
}
