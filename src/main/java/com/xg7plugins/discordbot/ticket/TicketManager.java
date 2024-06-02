package com.xg7plugins.discordbot.ticket;

import com.xg7plugins.discordbot.Main;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.ArrayList;
import java.util.List;

public class TicketManager {
    List<Ticket> tickets = new ArrayList<Ticket>();
    TextChannel channel;

    public void setChannel(String id) {
        this.channel = Main.guild.getTextChannelById(id);
    }

    public synchronized static void addTicket(Ticket ticket) {

    }
}
