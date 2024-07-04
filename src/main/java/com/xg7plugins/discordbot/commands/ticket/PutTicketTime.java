package com.xg7plugins.discordbot.commands.ticket;

import com.xg7plugins.discordbot.commands.Command;
import com.xg7plugins.discordbot.data.JSONManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.IOException;
import java.util.List;

public class PutTicketTime implements Command {


    @Override
    public String getName() {
        return "puttickettime";
    }

    @Override
    public String getDescription() {
        return "Coloca o cooldown do ticket";
    }

    @Override
    public void onSlashCommandEvent(SlashCommandInteractionEvent event) {
        if (!event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
            event.reply("Você não tem permissão para fazer isso!").setEphemeral(true).queue();
            return;
        }
        JSONManager.setDefaults("ticketcooldown", event.getOption("horas").getAsLong() * 3600000);
        try {
            JSONManager.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        event.reply("Horas do ticket colocada com sucesso!").setEphemeral(true).queue();
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.INTEGER, "horas", "Coloca as horas", true));
    }
}
