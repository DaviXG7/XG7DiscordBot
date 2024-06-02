package com.xg7plugins.discordbot.commands.ticket;

import com.xg7plugins.discordbot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class RequestChannel implements Command {
    @Override
    public String getName() {
        return "putticketchannel";
    }

    @Override
    public String getDescription() {
        return "coloca o lugar de pedir ticket";
    }

    @Override
    public void onSlashCommandEvent(SlashCommandInteractionEvent event) {

        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle("Suporte:hammer:");
        embedBuilder.setColor(0x00FFFF);

        embedBuilder.addField("Abra um ticket", "Abra um ticket se houver dúvidas sobre como usar um plugin ou para reportar um bug", true);
        embedBuilder.addField(":man_mechanic: Selecione um assunto", "Reportar um bug\n Tirar uma dúvida\nOutros problemas (Use para problemas moderados)", true);

        embedBuilder.setFooter("Você pode abrir um ticket a cada 20 minutos");

        Button button = Button.primary("duvida",":man_mechanic: Duvida");

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).setActionRow(button).queue();

    }

}
