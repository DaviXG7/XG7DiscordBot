package com.xg7plugins.discordbot.commands.other;

import com.xg7plugins.discordbot.commands.Command;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Site implements Command {
    @Override
    public String getName() {
        return "site";
    }

    @Override
    public String getDescription() {
        return "Link do site";
    }

    @Override
    public void onSlashCommandEvent(SlashCommandInteractionEvent event) {
        event.reply("Acesse nosso site! https://xg7plugins.com").setEphemeral(true).queue();
    }
}
