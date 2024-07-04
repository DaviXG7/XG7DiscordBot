package com.xg7plugins.discordbot.listeners;

import com.xg7plugins.discordbot.Main;
import com.xg7plugins.discordbot.commands.ticket.temp.DMTempManager;
import com.xg7plugins.discordbot.data.JSONManager;
import com.xg7plugins.discordbot.data.SQLManager;
import com.xg7plugins.discordbot.commands.CommandsManager;
import com.xg7plugins.discordbot.ticket.Ticket;
import com.xg7plugins.discordbot.ticket.TicketManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class GuildReady extends ListenerAdapter {

    void load() throws IOException, SQLException, ClassNotFoundException, InterruptedException, ExecutionException {
        SQLManager.load();

        Future<List<List<Object>>> tickets = SQLManager.select("SELECT * FROM tickets");

        DMTempManager.load(new ArrayList<>());

        JSONManager.load();
        try {
            TicketManager.setChannel(JSONManager.getDefaults().getLong("ticketChannelId"));
        } catch (Exception ignored) {}

        while (!tickets.isDone()) {
            Thread.sleep(50);
            System.out.println("Banco de dados carregando");
        }
        List<List<Object>> ticketsComplete = tickets.get();

        for (List<Object> ticket : ticketsComplete) {
            Future<List<List<Object>>> members = SQLManager.select("SELECT * FROM ticketmembers WHERE ticketid = ?");

        }


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
