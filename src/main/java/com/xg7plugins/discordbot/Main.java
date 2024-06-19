package com.xg7plugins.discordbot;

import com.xg7plugins.discordbot.commands.CommandsManager;
import com.xg7plugins.discordbot.data.JSONManager;
import com.xg7plugins.discordbot.data.SQLManager;
import com.xg7plugins.discordbot.listeners.ButtonClick;
import com.xg7plugins.discordbot.listeners.GuildReady;
import com.xg7plugins.discordbot.listeners.MenuSelection;
import com.xg7plugins.discordbot.ticket.Ticket;
import com.xg7plugins.discordbot.ticket.TicketManager;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.IOException;
import java.sql.SQLException;

public class Main {

    public static JDA jda;
    public static Guild guild;

    public static void main(String[] args) {

        JDA jada = JDABuilder.createDefault("MTE2Nzg5OTkyMDMzNDg2ODU0MA.GLqPNP.nE513_guIz2YxFenkr1aw79P0qgX-ezphwD4T4")
                .setActivity(Activity.watching("NADA"))

                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .enableIntents(GatewayIntent.GUILD_MESSAGES)

                .addEventListeners(new GuildReady(),
                        new CommandsManager(),
                        new MenuSelection(),
                        new ButtonClick()
                )


                .build();

        MainThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(Main::onDisable));

        jda = jada;

    }

    public static void onDisable() {
        try {
            SQLManager.setTickets(TicketManager.getTickets());
            JSONManager.save();
            jda.shutdown();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
        jda.shutdown();
    }
}