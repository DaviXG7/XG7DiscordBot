package com.xg7plugins.discordbot;

import com.xg7plugins.discordbot.commands.CommandsManager;
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
                .setActivity(Activity.watching("NADA"))

                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .enableIntents(GatewayIntent.GUILD_MESSAGES)

                .addEventListeners(
                        new GuildReady(),
                        new CommandsManager(),
                        new MenuSelection(),
                        new ButtonClick()
                )


                .build();

        MainThread.start();

        jda = jada;

    }
}
