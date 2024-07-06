package com.xg7plugins.discordbot.listeners;

import com.xg7plugins.discordbot.Main;
import com.xg7plugins.discordbot.commands.ticket.temp.DMTempManager;
import com.xg7plugins.discordbot.data.JSONManager;
import com.xg7plugins.discordbot.data.SQLManager;
import com.xg7plugins.discordbot.commands.CommandsManager;
import com.xg7plugins.discordbot.game.GameManager;
import com.xg7plugins.discordbot.ticket.Ticket;
import com.xg7plugins.discordbot.ticket.TicketManager;
import com.xg7plugins.discordbot.ticket.TipoTicket;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class GuildReady extends ListenerAdapter {

    void load() throws IOException, SQLException, ClassNotFoundException, InterruptedException, ExecutionException {
        SQLManager.load();

        Future<List<Map<String, Object>>> tickets = SQLManager.select("SELECT * FROM tickets");
        Future<List<Map<String, Object>>> members = SQLManager.select("SELECT * FROM ticketmembers");

        DMTempManager.load(new ArrayList<>());

        JSONManager.load();
        try {
            TicketManager.setChannel(JSONManager.getDefaults().getLong("ticketChannelId"));
        } catch (Exception ignored) {}
        GameManager.init();

        while (!tickets.isDone() && !members.isDone()) {
            System.out.println("Tickets carregando");
            Thread.sleep(50);
        }
        List<Map<String, Object>> ticketsComplete = tickets.get();
        List<Map<String, Object>> membersComplete = members.get();

        List<Ticket> tickets1 = new ArrayList<>();

        for (Map<String, Object> stringObjectMap : ticketsComplete) {
            List<Member> members1 = new ArrayList<>();
            for (Map<String, Object> objectMap : membersComplete) {
                if (objectMap.get("ownerid") == stringObjectMap.get("ownerid")) {
                    members1.add(Main.guild.retrieveMemberById((String) objectMap.get("ownerid")).complete());
                }
            }
            tickets1.add(
                    new Ticket(
                            Main.guild.retrieveMemberById((Long) stringObjectMap.get("ownerid")).complete(),
                            Main.guild.getTextChannelById((Long) stringObjectMap.get("channelid")),
                            members1,
                            TipoTicket.valueOf((String) stringObjectMap.get("tickettype")),
                            (Long) stringObjectMap.get("creationtime"),
                            (Boolean) stringObjectMap.get("isclosed")
                    )
            );
        }
        TicketManager.setTickets(tickets1);
        System.out.println("Tickets carregados com sucesso!");


    }



    @Override
    public void onGuildReady(GuildReadyEvent event) {
        if (event.getGuild().getIdLong() != 1206355714893815808L) return;
        if (Main.guild == null) Main.guild = event.getGuild();
        CommandsManager.init(event.getGuild());


        new Thread(() -> {
            try {
                load();
            } catch (IOException | SQLException | ClassNotFoundException | InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

}
