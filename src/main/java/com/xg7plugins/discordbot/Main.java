package com.xg7plugins.discordbot;

import com.xg7plugins.discordbot.commands.CommandsManager;
import com.xg7plugins.discordbot.commands.ticket.temp.DMTempManager;
import com.xg7plugins.discordbot.game.Listener;
import com.xg7plugins.discordbot.listeners.ButtonClick;
import com.xg7plugins.discordbot.listeners.GuildReady;
import com.xg7plugins.discordbot.listeners.MenuSelection;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {

    public static JDA jda;
    public static Guild guild;

    public static void main(String[] args) {
        JDA jada = JDABuilder.createDefault("")
                .setActivity(Activity.playing("XG7Plugins"))

                .enableIntents(
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MEMBERS
                )

                .addEventListeners(
                        new GuildReady(),
                        new CommandsManager(),
                        new MenuSelection(),
                        new ButtonClick(),
                        new DMTempManager(),
                        new Listener()
                )


                .build();

        MainThread.start();
        MainThread.in().start();

        jda = jada;

    }
}
