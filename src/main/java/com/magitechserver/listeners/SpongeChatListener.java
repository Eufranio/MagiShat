package com.magitechserver.listeners;

import com.magitechserver.DiscordHandler;
import com.magitechserver.MagiBridge;
import com.magitechserver.util.Config;
import com.magitechserver.util.Webhooking;
import io.github.nucleuspowered.nucleus.modules.staffchat.StaffChatMessageChannel;
import nl.riebie.mcclans.channels.AllyMessageChannelImpl;
import nl.riebie.mcclans.channels.ClanMessageChannelImpl;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.message.MessageChannelEvent;


/**
 * Created by Frani on 09/07/2017.
 */
public class SpongeChatListener {

    @Listener
    public void onSpongeMessage(MessageChannelEvent.Chat e, @Root Player p) {
        if(e.getChannel().isPresent()) {
            String content = e.getMessage().toPlain();
            String prefix = p.getOption("prefix").orElse("");
            String message = MagiBridge.getConfig().getString("messages", "server-to-discord-format")
                    .replace("%player%", p.getName())
                    .replace("%prefix%", prefix)
                    .replace("%message%", content);
            String discordChannel = MagiBridge.getConfig().getString("channel", "nucleus", "global-discord-channel");

            if(Sponge.getPluginManager().isLoaded("mcclans")) {
                if(e.getChannel().get() instanceof AllyMessageChannelImpl || e.getChannel().get() instanceof ClanMessageChannelImpl) return;
            }
            if(e.getChannel().get() instanceof StaffChatMessageChannel) {
                discordChannel = MagiBridge.getConfig().getString("channel", "nucleus", "staff-discord-channel");
            }
            if(Config.useWebhooks()) {
                Webhooking.sendWebhookMessage(MagiBridge.getConfig().getString("messages", "webhook-name")
                        .replace("%prefix%", p.getOption("prefix").isPresent() ? p.getOption("prefix").orElse(null) : "")
                        .replace("%player%", p.getName()),
                        p.getName(),
                        e.getMessage().toPlain(),
                        discordChannel);
                return;
            }
            DiscordHandler.sendMessageToChannel(discordChannel, message);
        }
    }

}