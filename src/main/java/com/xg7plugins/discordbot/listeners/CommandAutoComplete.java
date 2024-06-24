package com.xg7plugins.discordbot.listeners;

import com.xg7plugins.discordbot.Main;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.List;
import java.util.stream.Collectors;

public class CommandAutoComplete extends ListenerAdapter {
    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals("ticketadd") && event.getFocusedOption().getName().equals("membro")) {
            List<String> options = Main.guild.getMembers().stream()
                    .map(Member::getEffectiveName)
                    .filter(name -> name.toLowerCase().startsWith(event.getFocusedOption().getValue().toLowerCase()))
                    .collect(Collectors.toList());
            event.replyChoices((Command.Choice) options).queue();

        }
    }
}
