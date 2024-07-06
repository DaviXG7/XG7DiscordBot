package com.xg7plugins.discordbot.commands;

import com.xg7plugins.discordbot.commands.other.*;
import com.xg7plugins.discordbot.commands.ticket.PutTicketTime;
import com.xg7plugins.discordbot.commands.ticket.AddUserOnTicket;
import com.xg7plugins.discordbot.commands.ticket.FecharTicket;
import com.xg7plugins.discordbot.commands.ticket.RemoveUserOnTicket;
import com.xg7plugins.discordbot.commands.ticket.RequestChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CommandsManager extends ListenerAdapter {

    private static final HashMap<String, Command> commands = new HashMap<String, Command>();

    public static void init(Guild guild) {
        commands.put(new AddUserOnTicket().getName(), new AddUserOnTicket());
        commands.put(new RemoveUserOnTicket().getName(), new RemoveUserOnTicket());
        commands.put(new FecharTicket().getName(), new FecharTicket());
        commands.put(new RequestChannel().getName(), new RequestChannel());
        commands.put(new ChatGPT().getName(), new ChatGPT());
        commands.put(new PutTicketTime().getName(), new PutTicketTime());
        commands.put(new Plugin().getName(), new Plugin());
        commands.put(new Apoie().getName(), new Apoie());
        commands.put(new Site().getName(), new Site());
        commands.put(new MinePlayer().getName(), new MinePlayer());

        List<CommandData> data = commands.keySet().stream().map(k -> Commands.slash(k, commands.get(k).getDescription()).addOptions(commands.get(k).getOptions())).collect(Collectors.toList());

        com.xg7plugins.discordbot.game.Commands gameCommands = new com.xg7plugins.discordbot.game.Commands();
        data.add(Commands.slash(gameCommands.getName(), gameCommands.getDescription()).addSubcommands(gameCommands.getSubCommandData()));
        commands.put(gameCommands.getName(), gameCommands);

        guild.updateCommands().addCommands(data).queue();


    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        Command slash = commands.get(event.getName());

        slash.onSlashCommandEvent(event);
    }


}
