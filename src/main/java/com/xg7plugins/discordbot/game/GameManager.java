package com.xg7plugins.discordbot.game;

import com.xg7plugins.discordbot.Main;
import com.xg7plugins.discordbot.data.JSONManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class GameManager {

    private static List<Game> games = new ArrayList<>();

    public static Game getGame(long ownerid) {
        return games.stream().filter(game -> game.getOwner().getIdLong() == ownerid).findFirst().orElse(null);
    }
    public static void addGame(Member owner, TextChannel channel) {
        games.add(new Game(owner, channel));
    }
    public static void removeGame(long ownerid) {
        games.removeIf(game -> game.getOwner().getIdLong() == ownerid);
    }
    public static void initDefault() {
        games.add(new Game(null, Main.guild.getTextChannelById(JSONManager.getDefaults().getLong("gameid"))));
    }

}
