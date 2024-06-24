package com.xg7plugins.discordbot.commands.ticket;

import com.xg7plugins.discordbot.commands.Command;
import com.xg7plugins.discordbot.ticket.Ticket;
import com.xg7plugins.discordbot.ticket.TicketManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class FecharTicket implements Command {
    @Override
    public String getName() {
        return "ticketfechar";
    }

    @Override
    public String getDescription() {
        return "Fecha o ticket";
    }

    @Override
    public void onSlashCommandEvent(SlashCommandInteractionEvent event) {
        Ticket ticket = TicketManager.getTicketById(event.getChannelIdLong());
        if (ticket == null) {
            event.reply("Por favor faça isso no canal do seu ticket!").setEphemeral(true).queue();
            return;
        }
        if (event.getMember().getId().equals(ticket.getOwner().getId()) || event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {

            TicketManager.closeTicket(ticket.getOwner(), ticket.getTicketChannel().getIdLong());
            event.reply("Ticket fecahdo!").setEphemeral(true).queue();

        }
    }
}
