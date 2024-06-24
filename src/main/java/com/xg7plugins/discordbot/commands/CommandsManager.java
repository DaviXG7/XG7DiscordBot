package com.xg7plugins.discordbot.commands;

import com.xg7plugins.discordbot.commands.ticket.CreateTicket;
import com.xg7plugins.discordbot.commands.ticket.RequestChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CommandsManager extends ListenerAdapter {

    private static final HashMap<String, Command> commands = new HashMap<String, Command>();

    public static void init(Guild guild) {
        commands.put(new CreateTicket().getName(), new CreateTicket());
        commands.put(new RequestChannel().getName(), new RequestChannel());

        List<CommandData> data = commands.keySet().stream().map(k -> Commands.slash(k, commands.get(k).getDescription()).addOptions(commands.get(k).getOptions())).collect(Collectors.toList());


        guild.updateCommands().addCommands(data).queue();


    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        Command slash = commands.get(event.getName());

        slash.onSlashCommandEvent(event);
    }


}
