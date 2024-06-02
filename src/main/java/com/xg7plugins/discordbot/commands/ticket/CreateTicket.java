package com.xg7plugins.discordbot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.List;

public class CreateTicket implements Command {
    @Override
    public String getName() {
        return "createticket";
    }
    @Override
    public List<OptionData> getOptions() {
        return Collections.singletonList(new OptionData(OptionType.STRING, "Assunto", "Digite o que você quer resolver", true));
    }

    @Override
    public List<CommandData> getData() {
        return List.of();
    }

    @Override
    public String getDescription() {
        return "Sistema de ticket";
    }

    @Override
    public void onSlashCommandEvent(SlashCommandInteractionEvent event) {
        System.out.println("asdfsdfasdfsadfgsadfsdaf");
        event.reply("a").queue();

    }
}
