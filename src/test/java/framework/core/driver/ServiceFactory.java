package framework.core.driver;

import org.openqa.selenium.chrome.ChromeDriverService;

import java.nio.file.Path;

public class ServiceFactory {

    private ServiceFactory() {}

    public static ChromeDriverService createChromeService(Path artifactsDir) {
        return new ChromeDriverService.Builder()
                .withLogFile(artifactsDir.resolve("chromedriver.log").toFile())
                .usingAnyFreePort()
                .build();
    }
}
