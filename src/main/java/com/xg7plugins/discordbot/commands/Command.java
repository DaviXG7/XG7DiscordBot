package com.xg7plugins.discordbot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public interface Command {

    String getName();
    String getDescription();
    void onSlashCommandEvent(SlashCommandInteractionEvent event);
    default List<OptionData> getOptions() {
        return new ArrayList<>();
    }
}
