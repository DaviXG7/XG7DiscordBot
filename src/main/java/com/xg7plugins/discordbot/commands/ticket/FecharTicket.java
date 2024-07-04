package com.xg7plugins.discordbot.commands.ticket;

import com.xg7plugins.discordbot.commands.Command;
import com.xg7plugins.discordbot.commands.ticket.temp.DMTempManager;
import com.xg7plugins.discordbot.ticket.Ticket;
import com.xg7plugins.discordbot.ticket.TicketManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class FecharTicket implements Command {
    @Override
    public String getName() {
        return "ticketfechar";
    }

    @Override
    public String getDescription() {
        return "Fecha o ticket";
    }

    @Override
    public void onSlashCommandEvent(SlashCommandInteractionEvent event) {
        Ticket ticket = TicketManager.getTicketById(event.getChannelIdLong());
        if (ticket == null) {
            event.reply("Por favor faça isso no canal do seu ticket!").setEphemeral(true).queue();
            return;
        }
        if (event.getMember().getId().equals(ticket.getOwner().getId()) || event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {

            if (ticket.isClosed()) {
                event.reply("O ticket já está fechado!").setEphemeral(true).queue();
                return;
            }

            TicketManager.closeTicket(ticket.getOwner(), ticket.getTicketChannel().getIdLong());
            EmbedBuilder builder = new EmbedBuilder();

            builder.setTitle("Ticket fechado!");
            builder.setColor(0x00FFFF);

            builder.setDescription("Clique abaixo para arquivar ou deletar o ticket");

            Button deletar = Button.danger("deletar", "Deletar ticket");
            Button arquivar = Button.primary("arquivar", "Arquivar ticket");

            deletar.withDisabled(!event.getMember().getPermissions().contains(Permission.ADMINISTRATOR));
            arquivar.withDisabled(!event.getMember().getPermissions().contains(Permission.ADMINISTRATOR));

            DMTempManager.addUser(ticket.getOwner().getUser());

            event.replyEmbeds(builder.build()).setActionRow(deletar, arquivar).queue();

        }
    }
}
