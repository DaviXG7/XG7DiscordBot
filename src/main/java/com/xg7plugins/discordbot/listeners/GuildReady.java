package com.xg7plugins.discordbot.listeners;

import com.xg7plugins.discordbot.Main;
import com.xg7plugins.discordbot.data.JSONManager;
import com.xg7plugins.discordbot.data.SQLManager;
import com.xg7plugins.discordbot.commands.CommandsManager;
import com.xg7plugins.discordbot.ticket.TicketManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

public class GuildReady extends ListenerAdapter {

    void load() throws IOException, SQLException, ClassNotFoundException {

        JSONManager.load();
        SQLManager.load();

        TicketManager.setChannel(JSONManager.getDefaults().getLong("ticketChannelId"));
        TicketManager.setTickets(SQLManager.getTickets());



    }



    @Override
    public void onGuildReady(GuildReadyEvent event) {
        if (event.getGuild().getIdLong() != 1206355714893815808L) return;
        if (Main.guild == null) Main.guild = event.getGuild();
        CommandsManager.init(event.getGuild());

        new Thread(() -> {
            try {
                load();
            } catch (IOException | SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

}
