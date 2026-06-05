# MC-Discord-Integration (Fabric)

A Fabric mod that bridges your Minecraft server chat with a Discord channel via webhooks. Player chat messages, join/leave notifications, and advancement announcements are forwarded to Discord.

## Setup

1. Install the mod in your `mods/` folder
2. Start the server and join your world
3. Set your Discord webhook URL using the command below (requires operator permissions)
   To create a webhook, go to your Discord channel settings → **Integrations** → **Webhooks** → **New Webhook**, then copy the URL.

## Commands

All commands require **operator permissions**.

| Command | Description |
|---|---|
| `/mcdiscordintegration setwebhook <url>` | Sets the Discord webhook URL |
| `/mcdiscordintegration getwebhook` | Shows the currently configured webhook URL |
| `/mcdiscordintegration setEnabled <true/false>` | Enables or disables all message forwarding |
| `/mcdiscordintegration getEnabled` | Shows whether message forwarding is enabled |
| `/mcdiscordintegration setServerMessagesEnabled <true/false>` | Enables or disables server messages (join/leave, advancements) |
| `/mcdiscordintegration getServerMessagesEnabled` | Shows whether server messages are enabled |


## Configuration
The config is stored per-world at <world>/mcDiscordIntegration.properties and persists across restarts. It can be edited manually or updated at runtime via the commands above.