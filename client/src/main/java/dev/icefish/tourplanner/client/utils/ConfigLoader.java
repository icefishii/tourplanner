package dev.icefish.tourplanner.client.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private final static Logger logger = LogManager.getLogger(ConfigLoader.class);
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                logger.warn("config.properties not found in classpath. Will fall back to system properties.");
            }
        } catch (IOException e) {
            logger.error("Error loading config.properties: {}", e.getMessage());
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static String get(String key) {
        // Prefer config.properties, fallback to system property
        String value = properties.getProperty(key);
        if (value == null) {
            value = System.getProperty(key);
        }
        return value;
    }
}
