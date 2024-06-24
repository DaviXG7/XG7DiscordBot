package com.xg7plugins.discordbot.commands.ticket;

import com.xg7plugins.discordbot.Main;
import com.xg7plugins.discordbot.commands.Command;
import com.xg7plugins.discordbot.ticket.Ticket;
import com.xg7plugins.discordbot.ticket.TicketManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.List;

public class RemoveUserOnTicket implements Command {
    @Override
    public String getName() {
        return "ticketremove";
    }

    @Override
    public List<OptionData> getOptions() {
        return Collections.singletonList(new OptionData(OptionType.STRING, "membro", "Adiconar um membro (Pelo nome)", true));
    }

    @Override
    public String getDescription() {
        return "Remover um usuário do ticket";
    }

    @Override
    public void onSlashCommandEvent(SlashCommandInteractionEvent event) {
        Ticket ticket = TicketManager.getTicketById(event.getChannelIdLong());
        String id = event.getOption("membro").getAsString().replace("\n", "");
        if (ticket == null) {
            event.reply("Por favor faça isso no canal do seu ticket!").setEphemeral(true).queue();
            return;
        }
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("Você não tem permissão para user este comando!");
            return;
        }
        if (event.getOption("membro") == null) {
            event.reply("Você não escolheu um usuário!").setEphemeral(true).queue();
            return;
        }
        if (Main.jda.retrieveUserById(id).complete() == null) {
            event.reply("Esse usuário não existe!").setEphemeral(true).queue();
            return;
        }
        if (Main.jda.retrieveUserById(id).complete().isBot()) {
            event.reply("Você não pode remover um bot").setEphemeral(true).queue();
            return;
        }

        Member member = Main.guild.retrieveMemberById(id).complete();

        if (member == null) {
            event.reply("Algo deu errado!").setEphemeral(true).queue();
            return;
        }
        if (member.getPermissions().contains(Permission.ADMINISTRATOR)) {
            event.reply("Você não pode remover um administrador!").setEphemeral(true).queue();
            return;
        }

        ticket.removeMember(member);
        event.reply("Membro " + member.getAsMention() + " removido com sucesso!").queue();
    }
}
