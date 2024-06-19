package com.xg7plugins.discordbot.ticket;

import com.xg7plugins.discordbot.Main;
import com.xg7plugins.discordbot.data.JSONManager;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.ArrayList;
import java.util.List;

public class TicketManager {
    @Getter
    @Setter
    private static List<Ticket> tickets = new ArrayList<Ticket>();
    private static TextChannel channel;

    public synchronized static void setChannel(long id) {
        channel = Main.guild.getTextChannelById(id);
        JSONManager.setDefaults("ticketChannelId", id);
    }
    public synchronized static void addTicket(Member owner, TipoTicket tipoTicket) {
        tickets.add(new Ticket(owner, tipoTicket));
    }
    public synchronized static boolean containsUser(Member member) {
        return tickets.stream().anyMatch(ticket -> ticket.getOwner().getId().equals(member.getId()));
    }
    public synchronized static boolean closeTicket(Member owner, long id) {
        for (Ticket ticket : tickets) {
            if (ticket.getTicketChannel().getIdLong() == id) {
                if (ticket.getOwner().getId().equals(owner.getId()) || owner.getPermissions().contains(Permission.ADMINISTRATOR)) {
                    ticket.getTicketChannel().upsertPermissionOverride(owner).deny(Permission.MESSAGE_SEND).queue();
                    ticket.getMembers().forEach(member -> {
                        ticket.getTicketChannel().upsertPermissionOverride(member).deny(Permission.MESSAGE_SEND).queue();
                    });
                    return true;
                }
            }

        }
        return false;
    }
    public synchronized static void deleteTicket(Ticket ticket) {
        ticket.close();
        tickets.remove(ticket);
    }
    public synchronized static boolean deleteTicket(Member owner, long id) {
        for (Ticket ticket : tickets) {
            if (ticket.getTicketChannel().getIdLong() == id) {
                if (owner.getPermissions().contains(Permission.ADMINISTRATOR)) {
                    ticket.close();
                    tickets.remove(ticket);
                    return true;
                }
            }

        }
        return false;
    }
}
