package com.xg7plugins.discordbot.ticket;

import com.xg7plugins.discordbot.Main;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.ArrayList;
import java.util.List;

public class TicketManager {
    private static List<Ticket> tickets = new ArrayList<Ticket>();
    private static TextChannel channel;

    public synchronized static void setChannel(String id) {
        channel = Main.guild.getTextChannelById(id);
    }

    public synchronized static void addTicket(Member owner, TipoTicket tipoTicket) {
        tickets.add(new Ticket(owner, tickets.size(), tipoTicket));
    }
}
