package com.xg7plugins.discordbot.commands.other;

import com.xg7plugins.discordbot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public class MinePlayer implements Command {
    @Override
    public String getName() {
        return "minecraftplayer";
    }

    @Override
    public String getDescription() {
        return "Pega as informações de um jogador no Minecraft";
    }

    @Override
    public void onSlashCommandEvent(SlashCommandInteractionEvent event) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + event.getOption("player").getAsString());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");


            if (conn.getResponseCode() != 200) {
                event.reply("O Jogador " + event.getOption("player").getAsString() + " não foi encontrado! Verifique sua digitação").setEphemeral(true).queue();
                return;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            conn.disconnect();


            JSONObject jsonID = new JSONObject(sb.toString());

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(0x00eFFF);
            embedBuilder.setTitle("Jogador " + jsonID.getString("name"));
            embedBuilder.addField("UUID", jsonID.getString("id"), false);
            embedBuilder.addField("Skin:", "", false);
            embedBuilder.setThumbnail("https://minotar.net/armor/bust/" + jsonID.getString("id") + "/100.png");
            embedBuilder.setImage("https://minotar.net/skin/" + jsonID.getString("name") + ".png");
            event.replyEmbeds(embedBuilder.build()).queue();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    @Override
    public List<OptionData> getOptions() {
        return Collections.singletonList(new OptionData(OptionType.STRING, "player", "Nome do jogador", true, false));
    }
}
