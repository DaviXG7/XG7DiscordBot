package com.xg7plugins.discordbot.listeners;

import com.xg7plugins.discordbot.Main;
import com.xg7plugins.discordbot.commands.CommandsManager;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildReady extends ListenerAdapter {


    @Override
    public void onGuildReady(GuildReadyEvent event) {
        if (event.getGuild().getIdLong() != 1206355714893815808L) return;
        CommandsManager.init(event.getGuild());
    }

}
