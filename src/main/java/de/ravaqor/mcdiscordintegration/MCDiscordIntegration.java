package de.ravaqor.mcdiscordintegration;

import de.ravaqor.mcdiscordintegration.config.ConfigCommand;
import de.ravaqor.mcdiscordintegration.config.ModConfig;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

public class MCDiscordIntegration implements ModInitializer {
	public static final String MOD_ID = "mc-discord-integration";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final HttpClient HTTP = HttpClient.newHttpClient();

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            Path worldConfig = server.getWorldPath(LevelResource.ROOT).resolve("mcDiscordIntegration.properties");
            ModConfig.load(worldConfig);
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            ModConfig.save();
        });

        ConfigCommand.register();

        ServerMessageEvents.CHAT_MESSAGE.register(MCDiscordIntegration::onChatMessage);
        ServerMessageEvents.GAME_MESSAGE.register(MCDiscordIntegration::onServerMessage);


    }

    private static void onChatMessage(
            PlayerChatMessage message,
            ServerPlayer sender,
            ChatType.Bound params) {
        String playerName = sender.getName().getString();
        String content = message.decoratedContent().getString();
        send(playerName, content);
    }

    private static void onServerMessage(
            MinecraftServer server,
            Component text,
            boolean b) {
        if (ModConfig.getServerMessagesEnabled()) {
            send("Server", text.getString());
        }
    }

    private static void send(String username, String message) {
        if (!ModConfig.getEnabled()) {
            return;
        }

        String webhookURL = ModConfig.getWebhookUrl();
        if (!webhookURL.isEmpty()) {

            String safeUsername = username.replace("\\", "\\\\").replace("\"", "\\\"");
            String safeContent  = message.replace("\\", "\\\\").replace("\"", "\\\"");

            String json = """
                {
                  "username": "%s",
                  "content": "%s"
                }
                """.formatted(safeUsername, safeContent);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(webhookURL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HTTP.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                    .exceptionally(e -> {
                        LOGGER.error("Discord Webhook failed: " + e.getMessage());
                        return null;
                    });
        }
    }
}