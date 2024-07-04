package com.xg7plugins.discordbot.ticket;

import com.xg7plugins.discordbot.Main;
import com.xg7plugins.discordbot.commands.ticket.temp.DMTempManager;
import com.xg7plugins.discordbot.data.JSONManager;
import com.xg7plugins.discordbot.data.SQLManager;
import kotlin.Pair;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TicketManager {
    @Getter
    @Setter
    private static List<Ticket> tickets = new ArrayList<>();
    private static TextChannel channel;
    @Getter
    private static List<Pair<Long, Long>> createdTicket = new ArrayList<>();

    public synchronized static boolean containsCooldown(long userid) {
        for (Pair<Long, Long> pair : createdTicket) {
            if (pair.getSecond().equals(userid)) {
                return true;
            }
        }
        return false;
    }
    public synchronized static Pair<Long, Long> getCooldown(Member member) {
        return createdTicket.stream().filter(pair -> pair.getSecond().equals(member.getIdLong())).findFirst().orElse(null);
    }

    public synchronized static Ticket getTicketById(long id) {
        return tickets.stream().filter(ticket -> ticket.getTicketChannel().getIdLong() == id).findFirst().orElse(null);
    }
    public synchronized static void setChannel(long id) {
        channel = Main.guild.getTextChannelById(id);
        JSONManager.setDefaults("ticketChannelId", id);
        try {
            JSONManager.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public synchronized static void addTicket(Member owner, TipoTicket tipoTicket) {
        Ticket ticket = new Ticket(owner, tipoTicket);
        tickets.add(ticket);
        createdTicket.add(new Pair<>(System.currentTimeMillis(), owner.getIdLong()));
        SQLManager.update("INSERT INTO tickets(ownerid,channelid,tickettype,creationtime,isclosed) VALUES (?, ?, ?, ?, ?)", ticket.getOwner().getIdLong(), ticket.getTicketChannel().getIdLong(), ticket.getTipoTicket().name(), ticket.getCreationTime(), ticket.isClosed());
    }
    public synchronized static boolean containsUser(Member member) {
        return tickets.stream().anyMatch(ticket -> ticket.getOwner().getId().equals(member.getId()) && !ticket.isClosed());
    }
    public synchronized static boolean closeTicket(Member owner, long id) {
        for (Ticket ticket : tickets) {
            if (ticket == null) break;
            if (ticket.getTicketChannel().getIdLong() == id) {
                if (!ticket.getOwner().getId().equals(owner.getId()) && !owner.getPermissions().contains(Permission.ADMINISTRATOR)) break;
                ticket.getTicketChannel().upsertPermissionOverride(owner).deny(Permission.MESSAGE_SEND).queue();
                ticket.getMembers().forEach(member -> {
                    ticket.getTicketChannel().upsertPermissionOverride(member).deny(Permission.MESSAGE_SEND).queue();
                });
                ticket.setClosed(true);

                EmbedBuilder builder = new EmbedBuilder();

                builder.setTitle("Como foi o nosso atendimento?");
                builder.setDescription("Digite um número de 1-10");

                DMTempManager.addUser(ticket.getOwner().getUser());
                ticket.getOwner().getUser().openPrivateChannel().queue(privateChannel -> {
                   privateChannel.sendMessageEmbeds(builder.build()).queue();
                });
                return true;
            }
        }
        return false;
    }
    public synchronized static void deleteTicket(Ticket ticket) {
        ticket.close();
        tickets.remove(ticket);
        SQLManager.delete("DELETE FROM tickets WHERE channelid = ?", ticket.getTicketChannel().getIdLong());
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
