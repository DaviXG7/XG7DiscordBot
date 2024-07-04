package com.xg7plugins.discordbot.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.HashMap;

@Getter
public class Game {

    private Member owner;
    @Setter
    private TextChannel channel;
    @Setter
    private String palavra;
    private final HashMap<Long, Integer> points = new HashMap<>();
    private boolean isRunning = false;

    public Game(Member owner, TextChannel channel) {
        this.owner = owner;
        this.channel = channel;
    }

    public void init() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Digit");
        builder.setDescription("Digit é um jogo que o objetivo é digitar a palavra primeiro que todos!");
        builder.addField("Comando para começar", "`d!comecar`", true);
        builder.addField("Comando para adicionar um membro", "`/digitgame addmembro (usuário)`", true);
        builder.setColor(0x00FFFF);
        builder.setFooter("Bora começar!", "https://xg7plugins.com/imgs/logo.png");
    }
    public void start() {
        isRunning = true;
    }
    public void stop() {
        isRunning = false;
    }






}
