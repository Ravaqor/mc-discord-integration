package de.ravaqor.mcdiscordintegration.config;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

import java.net.URI;
import java.net.URISyntaxException;

public class ConfigCommand {

    public static void register() {
        registerWebhookCommands();
    }

    private static void registerWebhookCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("mcdiscordintegration")
                            .requires(source -> source.hasPermissionLevel(4))
                            .then(CommandManager.literal("setwebhook")
                                    .then(CommandManager.argument("url", StringArgumentType.greedyString())
                                            .executes(ctx -> {
                                                String url = StringArgumentType.getString(ctx, "url");
                                                if (!isValidUrl(url)) {
                                                    ctx.getSource().sendFeedback(
                                                            () -> Text.literal("§cInvalid URL!"),
                                                            false
                                                    );
                                                    return 0;
                                                }

                                                ModConfig.setWebhookUrl(url);
                                                ModConfig.save();
                                                ctx.getSource().sendFeedback(
                                                        () -> Text.literal("§aWebhook URL updated and saved!"),
                                                        true
                                                );
                                                return 1;
                                            })
                                    )
                            )
                            .then(CommandManager.literal("getwebhook")
                                    .executes(ctx -> {
                                        String url = ModConfig.getWebhookUrl();
                                        String display = url.isEmpty() ? "§cnot set" : "§a" + url;
                                        ctx.getSource().sendFeedback(
                                                () -> Text.literal("Webhook URL: " + display),
                                                false
                                        );
                                        return 1;
                                    })
                            )
                            .then(CommandManager.literal("setEnabled")
                                    .then(CommandManager.argument("value", BoolArgumentType.bool())
                                            .executes(ctx -> {
                                                boolean enabled = BoolArgumentType.getBool(ctx, "value");

                                                ModConfig.setEnabled(enabled);
                                                ModConfig.save();
                                                ctx.getSource().sendFeedback(
                                                        () -> Text.literal(enabled
                                                                ? "§aSending messages is now enabled!" : "§aSending messages is now disabled"),
                                                        true
                                                );
                                                return 1;
                                            })
                                    )
                            )
                            .then(CommandManager.literal("getEnabled")
                                    .executes(ctx -> {
                                        boolean enabled = ModConfig.getEnabled();
                                        ctx.getSource().sendFeedback(
                                                () -> Text.literal("Mc-Discord-Integration: " + enabled),
                                                false
                                        );
                                        return 1;
                                    })
                            )
                            .then(CommandManager.literal("setServerMessagesEnabled")
                                    .then(CommandManager.argument("value", BoolArgumentType.bool())
                                            .executes(ctx -> {
                                                boolean enabled = BoolArgumentType.getBool(ctx, "value");
                                                ModConfig.setServerMessagesEnabled(enabled);
                                                ModConfig.save();
                                                ctx.getSource().sendFeedback(
                                                        () -> Text.literal(enabled
                                                                ? "§aSending server messages is now enabled!" : "§aSending server messages is now disabled"),
                                                        true
                                                );
                                                return 1;
                                            })
                                    )
                            )
                            .then(CommandManager.literal("getServerMessagesEnabled")
                                    .executes(ctx -> {
                                        boolean enabled = ModConfig.getServerMessagesEnabled();
                                        ctx.getSource().sendFeedback(
                                                () -> Text.literal("Server messages enabled: " + enabled),
                                                false
                                        );
                                        return 1;
                                    })
                            )

            );
        });
    }

    private static boolean isValidUrl(String url) {
        try {
            var uri = new URI(url);
            return uri.getScheme() != null
                    && uri.getHost() != null
                    && (uri.getScheme().equals("https") || uri.getScheme().equals("http"));
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
