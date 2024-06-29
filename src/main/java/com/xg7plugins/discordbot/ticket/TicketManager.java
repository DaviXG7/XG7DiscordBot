package com.xg7plugins.discordbot.ticket;

import com.xg7plugins.discordbot.Main;
import com.xg7plugins.discordbot.data.JSONManager;
import com.xg7plugins.discordbot.data.SQLManager;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TicketManager {
    @Getter
    @Setter
    private static List<Ticket> tickets = new ArrayList<>();
    private static TextChannel channel;

    public synchronized static Ticket getTicketById(long id) {
        return tickets.stream().filter(ticket -> ticket.getTicketChannel().getIdLong() == id).findFirst().orElse(null);
    }
    public synchronized static void setChannel(long id) {
        channel = Main.guild.getTextChannelById(id);
        JSONManager.setDefaults("ticketChannelId", id);
    }
    public synchronized static void addTicket(Member owner, TipoTicket tipoTicket) {
        Ticket ticket = new Ticket(owner, tipoTicket);
        tickets.add(ticket);
        try {
            SQLManager.addTicket(ticket);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public synchronized static boolean containsUser(Member member) {
        return tickets.stream().anyMatch(ticket -> ticket.getOwner().getId().equals(member.getId()));
    }
    public synchronized static Ticket closeTicket(Member owner, long id) {
        for (Ticket ticket : tickets) {
            if (ticket == null) break;
            if (ticket.getTicketChannel().getIdLong() == id) {
                if (!(ticket.getOwner().getId().equals(owner.getId()) || owner.getPermissions().contains(Permission.ADMINISTRATOR))) break;
                ticket.getTicketChannel().upsertPermissionOverride(owner).deny(Permission.MESSAGE_SEND).queue();
                ticket.getMembers().forEach(member -> {
                    ticket.getTicketChannel().upsertPermissionOverride(member).deny(Permission.MESSAGE_SEND).queue();
                });
                ticket.setClosed(true);

                EmbedBuilder builder = new EmbedBuilder();

                builder.setTitle("Como foi o nosso atendimento?");
                builder.setDescription("Digite um número de 1-10");

                ticket.getOwner().getUser().openPrivateChannel().queue(privateChannel -> {
                   privateChannel.sendMessageEmbeds(builder.build()).queue();
                });
                return ticket;
            }
        }
        return null;
    }
    public synchronized static void deleteTicket(Ticket ticket) {
        ticket.close();
        tickets.remove(ticket);
        try {
            SQLManager.deleteTicket(ticket);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public synchronized static boolean deleteTicket(Member owner, long id) {
        for (Ticket ticket : tickets) {
            if (ticket.getTicketChannel().getIdLong() == id) {
                if (owner.getPermissions().contains(Permission.ADMINISTRATOR)) {
                    deleteTicket(ticket);
                    return true;
                }
            }

        }
        return false;
    }
}
