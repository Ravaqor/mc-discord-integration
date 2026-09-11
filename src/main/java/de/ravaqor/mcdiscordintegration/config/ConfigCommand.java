package de.ravaqor.mcdiscordintegration.config;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;

import java.net.URI;
import java.net.URISyntaxException;

public class ConfigCommand {

    public static void register() {
        registerWebhookCommands();
    }

    private static void registerWebhookCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    Commands.literal("mcdiscordintegration")
                            .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_OWNER))
                            .then(Commands.literal("setwebhook")
                                    .then(Commands.argument("url", StringArgumentType.greedyString())
                                            .executes(ctx -> {
                                                String url = StringArgumentType.getString(ctx, "url");
                                                if (!isValidUrl(url)) {
                                                    ctx.getSource().sendSuccess(
                                                            () -> Component.literal("§cInvalid URL!"),
                                                            false
                                                    );
                                                    return 0;
                                                }

                                                ModConfig.setWebhookUrl(url);
                                                ModConfig.save();
                                                ctx.getSource().sendSuccess(
                                                        () -> Component.literal("§aWebhook URL updated and saved!"),
                                                        true
                                                );
                                                return 1;
                                            })
                                    )
                            )
                            .then(Commands.literal("getwebhook")
                                    .executes(ctx -> {
                                        String url = ModConfig.getWebhookUrl();
                                        String display = url.isEmpty() ? "§cnot set" : "§a" + url;
                                        ctx.getSource().sendSuccess(
                                                () -> Component.literal("Webhook URL: " + display),
                                                false
                                        );
                                        return 1;
                                    })
                            )
                            .then(Commands.literal("setEnabled")
                                    .then(Commands.argument("value", BoolArgumentType.bool())
                                            .executes(ctx -> {
                                                boolean enabled = BoolArgumentType.getBool(ctx, "value");

                                                ModConfig.setEnabled(enabled);
                                                ModConfig.save();
                                                ctx.getSource().sendSuccess(
                                                        () -> Component.literal(enabled
                                                                ? "§aSending messages is now enabled!" : "§aSending messages is now disabled"),
                                                        true
                                                );
                                                return 1;
                                            })
                                    )
                            )
                            .then(Commands.literal("getEnabled")
                                    .executes(ctx -> {
                                        boolean enabled = ModConfig.getEnabled();
                                        ctx.getSource().sendSuccess(
                                                () -> Component.literal("Mc-Discord-Integration: " + enabled),
                                                false
                                        );
                                        return 1;
                                    })
                            )
                            .then(Commands.literal("setServerMessagesEnabled")
                                    .then(Commands.argument("value", BoolArgumentType.bool())
                                            .executes(ctx -> {
                                                boolean enabled = BoolArgumentType.getBool(ctx, "value");
                                                ModConfig.setServerMessagesEnabled(enabled);
                                                ModConfig.save();
                                                ctx.getSource().sendSuccess(
                                                        () -> Component.literal(enabled
                                                                ? "§aSending server messages is now enabled!" : "§aSending server messages is now disabled"),
                                                        true
                                                );
                                                return 1;
                                            })
                                    )
                            )
                            .then(Commands.literal("getServerMessagesEnabled")
                                    .executes(ctx -> {
                                        boolean enabled = ModConfig.getServerMessagesEnabled();
                                        ctx.getSource().sendSuccess(
                                                () -> Component.literal("Server messages enabled: " + enabled),
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
