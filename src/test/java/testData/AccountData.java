package testData;

import com.github.javafaker.Faker;

import lombok.Getter;

@Getter
public class AccountData {
    private static final Faker faker = new Faker();
    private final String title;
    private final String name;
    private final String email;
    private final String password;
    private final String day;
    private final String month;
    private final String year;


    public AccountData() {
        this.title = faker.options().option("Mr.", "Mrs.");
        this.name = faker.name().fullName();
        this.email = faker.internet().emailAddress();
        this.password = faker.internet().password();
        this.day = String.valueOf(faker.number().numberBetween(1, 31));
        this.month = String.valueOf(faker.number().numberBetween(1, 12));
        this.year = String.valueOf(faker.number().numberBetween(1900, 2021));
    }
}
