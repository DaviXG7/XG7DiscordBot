package com.xg7plugins.discordbot.game;

import com.xg7plugins.discordbot.Main;
import com.xg7plugins.discordbot.data.JSONManager;
import com.xg7plugins.discordbot.data.SQLManager;
import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;


public class GameManager {

    private static final List<Game> games = new ArrayList<>();
    @Getter
    private static HashMap<Long, Integer> rankingGlobal = new HashMap<>();

    @SneakyThrows
    public static void init() {

        Future<List<Map<String, Object>>> ranking = SQLManager.select("SELECT * FROM rankdigitgame");

        while (!ranking.isDone()) {
            System.out.println("Ranking carregando...");
            Thread.sleep(50);
        }

        List<Map<String, Object>> rankings = ranking.get();

        for (Map<String, Object> map : rankings) {
            rankingGlobal.put((Long) map.get("memberid"), (Integer) map.get("pontos"));
        }
        System.out.println("Ranking carregado com sucesso!");

    }
    public static Game getGame(long ownerid) {
        return games.stream().filter(game -> game.getOwner() != null && game.getOwner().getIdLong() == ownerid).findFirst().orElse(null);
    }
    public static Game getGameByChannelId(long channelid) {
        return games.stream().filter(game -> game.getChannel().getIdLong() == channelid).findFirst().orElse(null);
    }
    public static void addGame(Member owner, TextChannel channel) {
        games.add(new Game(owner, channel));
    }
    public static void removeGame(long ownerid) {
        games.removeIf(game -> game.getOwner().getIdLong() == ownerid);
    }
    public static void initDefault() {
        Game game = new Game(null, Main.guild.getTextChannelById(JSONManager.getDefaults().getLong("gamechannel")));
        game.run();
        games.add(game);
    }
    public static Game getDefaultGame() {
        for (Game game : games) {
            if (game.getChannel().getIdLong() == JSONManager.getDefaults().getLong("gamechannel")) return game;
        }
        return null;
    }
    public static void stopDefault() {
        games.removeIf(game -> game.getChannel().getIdLong() == JSONManager.getDefaults().getLong("gamechannel"));
    }
    public static void acrescentRankGlobal(Member member) {
        rankingGlobal.putIfAbsent(member.getIdLong(), 0);
        rankingGlobal.put(member.getIdLong(), rankingGlobal.get(member.getIdLong()) + 1);
        SQLManager.update("INSERT INTO `rankdigitgame` (`memberid`, `pontos`)" +
                "VALUES (?, ?)" +
                "ON DUPLICATE KEY UPDATE" +
                "    `memberid` = ?," +
                "    `pontos` = ?" +
                ";", member.getIdLong(), rankingGlobal.get(member.getIdLong()), member.getIdLong(), rankingGlobal.get(member.getIdLong()));
    }

}
