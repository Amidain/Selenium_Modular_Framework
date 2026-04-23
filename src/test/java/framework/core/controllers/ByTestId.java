package framework.core.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.openqa.selenium.By;

import framework.core.configs.Configuration;

public class ByTestId {
    
    private static final List<String> ATTRIBUTES = parseAttributes();

    public static By byTestId(String testId) {
        Objects.requireNonNull(testId, "Test ID value cannot be null!");
        return By.cssSelector(String.format(buildTemplate(), testId));
    }

    private static List<String> parseAttributes() {
        String raw = Configuration.locatorConfiguration().getTestIdAttribute();

        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private static String buildTemplate() {
    return ATTRIBUTES.stream()
            .map(attr -> "[" + attr + "='%s']")
            .collect(Collectors.joining(","));
    }
}
