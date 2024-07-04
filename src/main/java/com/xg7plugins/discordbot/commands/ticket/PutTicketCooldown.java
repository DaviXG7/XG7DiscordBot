package com.xg7plugins.discordbot.commands.ticket;

import com.xg7plugins.discordbot.commands.Command;
import com.xg7plugins.discordbot.data.JSONManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.IOException;
import java.util.List;

public class PutTicketCooldown implements Command {

    @Override
    public String getName() {
        return "putticketcooldown";
    }

    @Override
    public String getDescription() {
        return "Colocar o cooldown do ticket";
    }

    @Override
    public void onSlashCommandEvent(SlashCommandInteractionEvent event) {
        if (!event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
            event.reply("Você não tem permissão para fazer isso!").setEphemeral(true).queue();
            return;
        }
        JSONManager.setDefaults("ticketopencooldown", event.getOption("minutos").getAsLong() * 60000);
        try {
            JSONManager.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        event.reply("Horas do ticket colocada com sucesso!").setEphemeral(true).queue();
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.INTEGER, "minutos", "Coloca os minutos", true));
    }
}
