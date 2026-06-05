package de.ravaqor.mcdiscordintegration.config;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static de.ravaqor.mcdiscordintegration.MCDiscordIntegration.MOD_ID;

public class ModConfig {

    private static final Properties PROPS = new Properties();

    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("mcDiscordIntegration.properties");

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (var writer = Files.newBufferedWriter(CONFIG_PATH)) {
                PROPS.store(writer, "mcDiscordIntegration mod-config");
            } catch (IOException e) {
                System.err.println( MOD_ID + ": Failed to save config: " + e.getMessage());
            }
        } else {
            save();
        }
    }

    public static void save() {
        try (var writer = Files.newBufferedWriter(CONFIG_PATH)) {
            PROPS.store(writer, "MyDiscordMod config");
        } catch (IOException e) {
            System.err.println(MOD_ID + " Failed to save config: " + e.getMessage());
        }
    }

    public static String getWebhookUrl() {
        return PROPS.getProperty("webhookUrl", "");
    }

    public static void setWebhookUrl(String url) {
        PROPS.setProperty("webhookUrl", url);
    }

    public static boolean getEnabled() {
        return Boolean.parseBoolean(PROPS.getProperty("enabled", "true"));
    }

    public static void setEnabled(boolean enabled) {
        PROPS.setProperty("enabled", String.valueOf(enabled));
    }

    public static boolean getServerMessagesEnabled() {
        return Boolean.parseBoolean(PROPS.getProperty("serverMessagesEnabled", "true"));
    }

    public static void setServerMessagesEnabled(boolean serverMessagesEnabled) {
        PROPS.setProperty("serverMessagesEnabled", String.valueOf(serverMessagesEnabled));
    }
}
