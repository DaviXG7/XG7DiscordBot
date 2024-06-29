package com.xg7plugins.discordbot.commands.plugins;

import com.xg7plugins.discordbot.commands.Command;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class Plugin implements Command {
    @Override
    public String getName() {
        return "plugins";
    }

    @Override
    public String getDescription() {
        return "Opçes de Plugins";
    }

    @Override
    public void onSlashCommandEvent(SlashCommandInteractionEvent event) {

    }

    @Override
    public List<OptionData> getOptions() {
        return new OptionData(;
    }
}
