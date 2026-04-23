package framework.core.configs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

public final class PropertyReader {

    private final Properties props = new Properties();
    private final String propertiesFileName;

    private PropertyReader() {
        this.propertiesFileName = determinePropertiesFilename();
        load();
    }

    private String determinePropertiesFilename() {
        String env = System.getenv("CONFIG_FILE");
        if (env != null && !env.trim().isEmpty()) return env.trim();

        return "config.properties";
    }

    private void load() {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(propertiesFileName)) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + propertiesFileName, e);
        }
    }

    private static class Holder {
        private static final PropertyReader INSTANCE = new PropertyReader();
    }

    public static PropertyReader getInstance() {
        return Holder.INSTANCE;
    }

    public static String getParam(String key) {
        return getInstance().getRawValue(key);
    }


    private static String get(String key) {
        return getInstance().props.getProperty(key);
    }

    private String getRawValue(String key) {
        String environmentValue = readEnvironmentValue(key);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue.trim();
        }

        String propertyValue = get(key);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue.trim();
        }

        return null;
    }

    private String readEnvironmentValue(String key) {
        String environmentKey = key
            .replaceAll("([a-z0-9])([A-Z])", "$1_$2")
            .replace('.', '_')
            .replace('-', '_')
            .toUpperCase(Locale.ROOT);
        return System.getenv(environmentKey);
    }
}
