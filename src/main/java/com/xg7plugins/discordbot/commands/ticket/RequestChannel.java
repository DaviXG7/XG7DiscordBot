package com.xg7plugins.discordbot.commands.ticket;

import com.xg7plugins.discordbot.Main;
import com.xg7plugins.discordbot.commands.Command;
import com.xg7plugins.discordbot.ticket.TicketManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
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

        if (!event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
            event.reply("Você não tem permissão para usar este comando!").queue();
            return;
        }

        TicketManager.setChannel(event.getIdLong());

        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle("\uD83D\uDCE9 Abra um ticket");
        embedBuilder.setDescription("Se você tiver dúvidas sobre como usar um plugin ou quiser reportar um bug, abra um ticket.");
        embedBuilder.setColor(0x00FFFF);

        embedBuilder.addField("Selecione um assunto:", """
                • \uD83D\uDC1E **Reportar um bug** \s
                • ❓ **Tirar uma dúvida de um plugin** \s
                • \uD83D\uDEA8 **Denunciar um usuário** \s
                • ⚙\uFE0F **Outros problemas** (Use para problemas moderados)""", false);

        embedBuilder.addField("Onde posso reportar um bug ou sugerir algo para um plugin?", "Para reportar um bug e ajudarmos na manutenção do plugin, vá em **<#1252029589413298226>**.  \n" +
                "Para sugerir um recurso para um plugin, vá em **<#1252029623177318541>**.", false);

        embedBuilder.setFooter("Nota: Você pode abrir um ticket a cada 20 minutos. Tickets desnecessários podem resultar em punição.", Main.guild.getIconUrl());

        StringSelectMenu.Builder builder = StringSelectMenu.create("menu:TipoDeTicket");
        builder.addOption("Bugs", "bugs");
        builder.addOption("Dúvida de plugin", "duvida");
        builder.addOption("Denuncia de usuário", "denuncia");
        builder.addOption("Outros", "outro");

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).setActionRow(builder.build()).queue();

        event.reply("Canal colocado com sucesso!").setEphemeral(true).queue();
    }

}
