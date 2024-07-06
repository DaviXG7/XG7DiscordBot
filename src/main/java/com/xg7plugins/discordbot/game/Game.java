package com.xg7plugins.discordbot.game;

import com.xg7plugins.discordbot.Main;
import com.xg7plugins.discordbot.data.SQLManager;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.FileUpload;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class Game {

    private final Member owner;
    @Setter
    private TextChannel channel;
    @Setter
    private String palavra;
    private final HashMap<Long, Integer> points = new HashMap<>();
    private boolean isRunning = false;
    private boolean isReading = false;

    public Game(Member owner, TextChannel channel) {
        this.owner = owner;
        this.channel = channel;
    }

    public synchronized void run() {
        this.isRunning = true;
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Digit game");
        builder.addField("Objetivo", "O objetivo é simples, quem digitar a palavra que aparecerá primeiro ganha um ponto, quem tiver mais pontos ganha!", true);
        if (owner != null) builder.addField("Dono do jogo:", owner.getAsMention(), true);
        builder.setColor(0x00FFFF);
        builder.setFooter("Próxima palavra daqui a 10 segundos...");
        channel.sendMessageEmbeds(builder.build()).queue();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        changeWord();
    }

    @SneakyThrows
    public void changeWord() {

        URL url = new URL("https://api.dicionario-aberto.net/random");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        StringBuilder sb = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            sb.append(output);
        }
        conn.disconnect();

        JSONObject jsonID = new JSONObject(sb.toString());
        String text = jsonID.getString("word").toUpperCase();
        this.palavra = text;

        BufferedImage image = new BufferedImage(300, 40, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = image.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, 300, 40);

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 20));

        FontMetrics fm = g2d.getFontMetrics();
        int lineHeight = fm.getHeight();
        int y = lineHeight;
        for (String line : text.split("\n")) {
            g2d.drawString(line, 10, y);
            y += lineHeight;
        }
        g2d.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);

        InputStream images = new ByteArrayInputStream(baos.toByteArray());

        EmbedBuilder builder2 = new EmbedBuilder();
        builder2.setTitle("Digite a palavra acima");
        builder2.setColor(0xFF000F);
        channel.sendMessageEmbeds(builder2.build()).addFiles(FileUpload.fromData(images, "palavra.png")).queue();
        isReading = true;
    }

    public void point(Member member) {
        isReading = false;
        if (this.getOwner() == null) GameManager.acrescentRankGlobal(member);
        this.points.putIfAbsent(member.getIdLong(), 0);
        this.points.put(member.getIdLong(), this.points.get(member.getIdLong()) + 1);

        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle(member.getEffectiveName() + " Digitou mais rápido!");

        StringBuilder sb = new StringBuilder();
        AtomicInteger index = new AtomicInteger();
        points.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(10).forEach(e -> {
                    index.getAndIncrement();
                    sb.append(index.get()).append(". ").append(Main.guild.retrieveMemberById(e.getKey()).complete().getAsMention()).append(" ").append(e.getValue()).append("\n");
        });


        builder.addField("Rank da partida", sb.toString(), true);
        builder.setColor(0x00FFFF);
        builder.setFooter("Próxima palavra em 10 segundos... Seja mais rápido na próxima :D");

        this.channel.sendMessageEmbeds(builder.build()).queue();
    }

    public void stop() {
        this.isRunning = false;
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("O jogo acabou!");

        StringBuilder sb = new StringBuilder();
        AtomicInteger index = new AtomicInteger();
        points.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(10).forEach(e -> {
                    index.getAndIncrement();
                    sb.append(index.get()).append(". ").append(Main.guild.retrieveMemberById(e.getKey()).complete().getAsMention()).append(" ").append(e.getValue()).append("\n");
                });


        builder.addField("Rank da partida", sb.toString(), true);
        builder.setColor(0x00FFFF);
        builder.setFooter(this.equals(GameManager.getDefaultGame()) ? "" : "Deletando o canal em 10 segundos...");
        this.channel.sendMessageEmbeds(builder.build()).queue();

        if (this.owner != null) {
            GameManager.removeGame(owner.getIdLong());
            new Thread(() -> {
                try {
                    Thread.sleep(10000);
                    this.channel.delete().queue();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }).start();
            return;
        }

        GameManager.stopDefault();


    }






}
