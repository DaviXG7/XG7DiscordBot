package com.xg7plugins.discordbot.commands.ticket;

import com.xg7plugins.discordbot.Main;
import com.xg7plugins.discordbot.commands.Command;
import com.xg7plugins.discordbot.data.SQLManager;
import com.xg7plugins.discordbot.ticket.Ticket;
import com.xg7plugins.discordbot.ticket.TicketManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.List;

public class CreateTicket implements Command {
    @Override
    public String getName() {
        return "ticketadd";
    }
    @Override
    public List<OptionData> getOptions() {
        return Collections.singletonList(new OptionData(OptionType.STRING, "membro", "Adiconar um membro (Pelo nome)", true));
    }

    @Override
    public String getDescription() {
        return "Sistema de ticket";
    }

    @Override
    public void onSlashCommandEvent(SlashCommandInteractionEvent event) {
        Ticket ticket = TicketManager.getTicketById(event.getChannelIdLong());
        if (ticket == null) {
            event.reply("Por favor faça isso no canal do seu ticket!").setEphemeral(true).queue();
            return;
        }

        if (event.getOption("membro") == null) {
            event.reply("Você não escolheu um usuário!").setEphemeral(true).queue();
            return;
        }
        if (event.getOption("membro").getAsUser().isBot()) {
            event.reply("Você não pode adcionar um bot").setEphemeral(true).queue();
            return;
        }

        Member member = null;

        if (member == null) {
            event.reply("Algo deu errado!").setEphemeral(true).queue();
            return;
        }

        if (ticket.getMembers().contains(member)) {
            event.reply("Este membro já está no ticket!").setEphemeral(true).queue();
            return;
        }
        if (member.getPermissions().contains(Permission.ADMINISTRATOR)) {
            event.reply("Você não pode adicionar um administrador!").setEphemeral(true).queue();
            return;
        }

        ticket.addMember(member);
        event.reply("Membro " + member.getAsMention() + " adicionado com sucesso!").queue();
    }
}
