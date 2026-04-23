package utils;

import framework.core.driver.DriverManager;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class ScreenShotMaker {
    /**
     * Capture a screenshot and attach it to Allure report.
     *
     * @param testName Optional test name to include in Allure
     * @return screenshot bytes (PNG)
     */

    @Attachment(value = "Page screenshot [{testName}]", type = "image/png")
    public static byte[] makeScreenShot(String testName, String className) {
        try {
            byte[] screenshotBytes = ((TakesScreenshot) DriverManager.getWebDriver()).getScreenshotAs(OutputType.BYTES);
            Path screenshotDir = Paths.get("screenshots");
            List<String> classNames = Arrays.stream(className.split("\\.")).toList();
            Path screenshotFile = screenshotDir.resolve(String.format("[%s].%s.png", classNames.getLast(), testName));
            
            // Create directory if it doesn't exist
            Files.createDirectories(screenshotDir);
            Files.write(screenshotFile, screenshotBytes);
            
            return screenshotBytes;
        } catch (IOException e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
            return new byte[0];
        }
    }
}