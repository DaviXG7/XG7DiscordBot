package com.xg7plugins.discordbot.game;

import com.xg7plugins.discordbot.Main;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Listener extends ListenerAdapter {

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (!event.getModalId().equals("game")) return;
        Game game = GameManager.getGame(event.getMember().getIdLong());

        if (game == null) {
            event.reply("Você não é o dono deste jogo!").setEphemeral(true).queue();
            return;
        }




        Main.guild.loadMembers().onSuccess(members -> {

            for (Member member : members) {
                if (member.getUser().getName().equals(event.getValue("membro").getAsString())) {
                    game.getChannel().upsertPermissionOverride(member).grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND).queue();

                    event.reply(member.getAsMention() + " foi adicionado com sucesso!").queue();
                    return;
                }
            }
            event.reply("Membro não encontrado!").setEphemeral(true).queue();


        });
    }

    @SneakyThrows
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        switch (event.getMessage().getContentRaw()) {
            case "d!comecar" -> {
                Game game = GameManager.getGame(event.getMember().getIdLong());
                if (game == null) {
                    event.getMessage().reply("Você não é o dono desse jogo para começar").queue();
                    return;
                }
                if (game.isRunning()) {
                    event.getMessage().reply("O jogo já começou!").queue();
                    return;
                }
                game.run();
                return;

            }
            case "d!rank" -> {
                Game game = GameManager.getGameByChannelId(event.getChannel().getIdLong());

                if (game == null) {
                    event.getMessage().reply("Não tem nenhum jogo acontecendo neste canal").queue();
                    return;
                }


                EmbedBuilder builder = new EmbedBuilder();

                StringBuilder sb = new StringBuilder();
                AtomicInteger index = new AtomicInteger();
                builder.setTitle("Ranking desta partida");
                game.getPoints().entrySet().stream()
                        .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                        .limit(10).forEach(e -> {
                            index.getAndIncrement();
                            sb.append(index.get()).append(". ").append(Main.guild.retrieveMemberById(e.getKey()).complete().getAsMention()).append(" ").append(e.getValue()).append("\n");
                        });

                builder.addField("Ranks:", sb.toString(), true);
                builder.setColor(0xFFFF00);

                event.getMessage().replyEmbeds(builder.build()).queue();

                return;

            }
            case "d!parar" -> {
                Game game = GameManager.getGameByChannelId(event.getChannel().getIdLong());
                if (game.equals(GameManager.getDefaultGame())) {
                    if (!event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
                        event.getMessage().reply("Você não tem permissão para parar o jogo!");
                        return;
                    }
                }
                game.stop();
                return;
            }
            case "d!help" -> {

                EmbedBuilder builder = new EmbedBuilder();

                builder.setTitle("Comandos do digit game!");
                builder.addField("Comandos",
                        "`d!rank` -> Ve os ranks da partida\n" +
                                "`d!parar` -> Para o jogo que está rodando\n" +
                                "`d!comecar` -> Começa o jogo no canal privado", false);
                builder.setColor(0xFFFF00);

                event.getMessage().replyEmbeds(builder.build()).queue();

                return;
            }
            default -> {

                Game game = GameManager.getGameByChannelId(event.getChannel().getIdLong());

                if (game == null) return;
                if (!game.isRunning()) return;

                if (game.isReading() && event.getMessage().getContentRaw().toUpperCase().startsWith(game.getPalavra())) {
                    game.point(event.getMember());
                    new Thread(() -> {
                        try {
                            Thread.sleep(10000);
                            if (GameManager.getGameByChannelId(event.getChannel().getIdLong()) != null)game.changeWord();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }).start();
                }

            }
        }

        if (event.getMessage().getContentRaw().startsWith("d!")) {
            event.getMessage().reply("Comando desconhecido! Digite `d!help`para saber os comandos").queue();
        }
    }

}
