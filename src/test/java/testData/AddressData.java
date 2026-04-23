package testData;

import com.github.javafaker.Faker;

import lombok.Getter;

@Getter
public class AddressData {
    private static final Faker FAKER = new Faker();
    private final String firstName;
    private final String lastName;
    private final String company;
    private final String address;
    private final String address2;
    private final String country;
    private final String state;
    private final String city;
    private final String zipcode;
    private final String mobileNumber;

    public AddressData() {
        this.firstName = FAKER.name().firstName();
        this.lastName = FAKER.name().lastName();
        this.company = FAKER.company().name();
        this.address = FAKER.address().streetAddress();
        this.address2 = FAKER.address().secondaryAddress();
        this.country = FAKER.options().
        option("India", "United States", "Canada", "Australia", "New Zealand", "Israel", "Singapore");
        this.state = FAKER.address().state();
        this.city = FAKER.address().city();
        this.zipcode = FAKER.address().zipCode();
        this.mobileNumber = FAKER.phoneNumber().cellPhone();
    }
}
