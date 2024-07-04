package com.xg7plugins.discordbot.commands.other;

import com.xg7plugins.discordbot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public class Plugin implements Command {
    @Override
    public String getName() {
        return "plugininfo";
    }

    @Override
    public String getDescription() {
        return "Opçes de Plugins";
    }

    @Override
    public void onSlashCommandEvent(SlashCommandInteractionEvent event) {

        try {
            URL url = new URL("https://xg7plugins.com/api/plugin?plugin=" + event.getOption("name").getAsString());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");


            if (conn.getResponseCode() != 200) {
                event.reply("O plugin " + event.getOption("name").getAsString() + " não foi encontrado! Verifique sua digitação").setEphemeral(true).queue();
                return;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }

            conn.disconnect();

            JSONObject jsonObject = new JSONObject(sb.toString());

            EmbedBuilder eb = new EmbedBuilder();

            String  s1 = !Objects.equals(jsonObject.getString("github"), "") ? "[Github](" + jsonObject.get("github") + ")\n" : "";
            String s2 = !Objects.equals(jsonObject.getString("linkYoutube"), "") ? "[Video](" + jsonObject.get("linkYoutube") + ")\n" : "";

            eb.setColor(0x000FFF);
            eb.setTitle("Plugin " + jsonObject.getString("nome"));
            eb.setDescription("**Descrição:**\n" + jsonObject.getString("descricao"));
            eb.setThumbnail("https://cdn.discordapp.com/attachments/1216765840381313146/1257686150814896179/electric-plugin-icon.png?ex=66854f01&is=6683fd81&hm=c8aafacaa08dc443b475249b58ffe82a3752bca2808c2874560326b74d5beeae&");
            eb.addField("Downloads", String.valueOf(jsonObject.getInt("downloads")), true);
            eb.addField("Versão mínima", jsonObject.getString("versao"), true);
            eb.addField("Recursos", jsonObject.getJSONArray("recursos").join("\n").replace("\"", ""), false);
            eb.addField("Links", s1 + s2, false);
            eb.setFooter("XG7Plugins - Saiba mais em www.xg7plugins.com", "https://xg7plugins.com/imgs/logo.png");

            Button button = Button.link(jsonObject.getDouble("preco") == 0 ? "https://xg7plugins.com/download?plugin=" + jsonObject.get("nome") + "&type=plugin" : "https://xg7plugins.com", jsonObject.getDouble("preco") == 0 ? "Baixar" : "Comprar");

            event.replyEmbeds(eb.build()).addActionRow(button).queue();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.STRING, "name", "O nome do plugin", true));
    }
}
