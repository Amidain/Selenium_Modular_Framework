package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import framework.core.driver.DriverManager;

public class DriverUtils {

    private static WebDriver getWebDriver() {
        return DriverManager.getWebDriver();
    }

    public static void maximizeWindow(){
        try {
            getWebDriver().manage().window().maximize();
        } catch (Exception e) {
            // Some driver/CDP combinations (especially in CI) may not support the maximize command.
            // Fallback to setting a sensible window size to mimic maximize.
            try {
                getWebDriver().manage().window().setSize(new Dimension(1920, 1080));
            } catch (Exception ex) {
                System.err.println("Failed to maximize or set window size: " + ex.getMessage());
            }
        }
    }

    public static void navigateToBackPage(){
        try {
            getWebDriver().navigate().back();
        }catch (Exception e){
            // silent
        }
    }

    public static void navigateToPage(String pageUrl){
        try {
            getWebDriver().navigate().to(pageUrl);
        }catch (Exception e){
            // silent
        }
    }


    public static void clearCache() {
        getWebDriver().manage().deleteAllCookies();
        JavascriptExecutor js = (JavascriptExecutor) getWebDriver();
        js.executeScript("window.localStorage.clear();");
    }

    public static String getWindowState() {
        return getWebDriver().getWindowHandle();
    }

    public static void switchToWindow(String name) {
        getWebDriver().switchTo().window(name);
    }
    public static String getCurrentUrl() {
        return getWebDriver().getCurrentUrl();
    }

    public static String getTitle() {
        return getWebDriver().getTitle();
    }

    public static ArrayList<String> getWindowHandles() {
        return new ArrayList<>(getWebDriver().getWindowHandles());
    }

    public static void refreshPage() {
        getWebDriver().navigate().refresh();
    }

    public static void logBrowserConsole() {
        LogEntries logEntries = getWebDriver().manage().logs().get(LogType.BROWSER);
        Path logDir = Paths.get("browser-logs");
        Path logFile = logDir.resolve("console.log");
        
        try {
            // Create directory if it doesn't exist
            Files.createDirectories(logDir);
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile.toFile(), true))) {
                for (LogEntry entry : logEntries) {
                    writer.write("[Browser Console Log] : [" + entry.getLevel() + "] " + entry.getMessage());
                    writer.newLine();
                }
                System.out.println("Browser console logs saved to: " + logFile);
            }
        } catch (IOException e) {
            System.err.println("Failed to write browser console logs to file: " + e.getMessage());
        }
    }

    public static void closeCurrentTabAndSwitchToMainView() {
        ArrayList<String> tab = new ArrayList<>(getWebDriver().getWindowHandles());
        getWebDriver().close();
        getWebDriver().switchTo().window(tab.get(0));
    }

    public static void disposeDriver() {
        DriverManager.disposeDriver();
    }

    public static void switchToDefaultContent() {
        getWebDriver().switchTo().defaultContent();
    }

    public static void switchToFrame(WebElement frame) {
        getWebDriver().switchTo().frame(frame);
    }

    public static void switchToActiveElement() {
        getWebDriver().switchTo().activeElement();
    }

     
    public static boolean isDriverAvailable() {
        try {
            return getWebDriver() != null;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public static void writeBrowserConsole(Path outputFile) {
        writeLogEntries(LogType.BROWSER, outputFile, "Browser Console");
    }

    public static void writePerformanceLog(Path outputFile) {
        writeLogEntries(LogType.PERFORMANCE, outputFile, "Performance");
    }

    private static void writeLogEntries(String logType, Path outputFile, String header) {
        try {
            LogEntries logEntries = getWebDriver().manage().logs().get(logType);
            Files.createDirectories(outputFile.getParent());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile.toFile(), false))) {
                for (LogEntry entry : logEntries) {
                    writer.write("[" + header + "] [" + entry.getLevel() + "] " + entry.getMessage());
                    writer.newLine();
                }
            }
        } catch (Exception ignored) {
            // silent
        }
    }
}
