package com.xg7plugins.discordbot.commands.ticket;

import com.xg7plugins.discordbot.commands.Command;
import com.xg7plugins.discordbot.ticket.TicketManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

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

        TicketManager.setChannel(event.getChannelId());

        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle("Suporte :hammer:");
        embedBuilder.setColor(0x00FFFF);

        embedBuilder.addField("\uD83C\uDF9F️ Abra um ticket", "Abra um ticket se houver dúvidas sobre como usar um plugin ou para reportar um bug", true);
        embedBuilder.addField("\uD83D\uDC68\u200D\uD83D\uDD27 Selecione um assunto", "• Reportar um bug\n • Tirar uma dúvida de um plugin\n• Outros problemas (Use para problemas moderados)", true);
        embedBuilder.addField("\uD83D\uDC68\u200D\uD83D\uDD27 Onde posso reportar um bug ou sugerir alguma coisa para um plugin?", "Para deixar um bug  para fazermos a manutenção do plugin vá em <#1206358490126487573> \n Para sugerir algum recurso para um plugin vá em <#1216708560587587615>", true);

        embedBuilder.setFooter("Você pode abrir um ticket a cada 20 minutos");

        StringSelectMenu.Builder builder = StringSelectMenu.create("TipoDeTicket");
        builder.addOption("Bugs", "bugs");
        builder.addOption("Dúvida de plugin", "duvida");
        builder.addOption("Outros", "outro");

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).setActionRow(builder.build()).queue();
        event.reply("").queue();
    }

}
