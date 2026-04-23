package framework.core.configs;

public class LocatorConfiguration {
    private final String testIdAttribute;

    public LocatorConfiguration(String testIdAttribute) {
        this.testIdAttribute = testIdAttribute;
    }

    public String getTestIdAttribute() {
        return testIdAttribute;
    }
}
