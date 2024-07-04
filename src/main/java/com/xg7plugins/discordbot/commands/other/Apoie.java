package com.xg7plugins.discordbot.commands.other;

import com.xg7plugins.discordbot.commands.Command;
import com.xg7plugins.discordbot.data.JSONManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Apoie implements Command {
    @Override
    public String getName() {
        return "apoie";
    }

    @Override
    public String getDescription() {
        return "Apoie a gente com uma doação";
    }

    @Override
    public void onSlashCommandEvent(SlashCommandInteractionEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(0x00FFFF);
        eb.setTitle("Seja um apoiador!");
        eb.setDescription("Apoie nosso trabalho para melhorarmos o nosso trabalho e continuarmos a trazer as melhores opções de plugin");
        eb.addField("Links de doação",
                "Abra um ticket no canal <#%s> e fale qual a forma de pagamente que deseja;\nAcesse nosso [Ko-fi](https://ko-fi.com/davixg7) e page com o paypal;\nOu pague com pix usando o email davisonic0102@gmail.com ou use o qr code abaixo:".formatted(JSONManager.getDefaults().getLong("ticketChannelId")),
                false);
        eb.setFooter("Agraderecemos muito sua doação <3. Não esqueça de ver os termos de doação em nosso site");
        eb.setImage("https://xg7plugins.com/imgs/qrcodepix.png");
        event.replyEmbeds(eb.build()).setEphemeral(true).queue();
    }

}
