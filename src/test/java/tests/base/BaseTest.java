package tests.base;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import framework.core.configs.Configuration;
import framework.elements.factory.ElementFactory;
import utils.DriverUtils;

public class BaseTest {
    protected String baseUrl;
    protected ElementFactory elementFactory;
    
    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        baseUrl = Configuration.driverConfiguration().getBaseUrl();
        DriverUtils.maximizeWindow();

        elementFactory = ElementFactory.defaultFactory();
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        DriverUtils.disposeDriver();
    }
}
