package com.xg7plugins.discordbot.listeners;

import com.xg7plugins.discordbot.Main;
import com.xg7plugins.discordbot.data.SQLManager;
import com.xg7plugins.discordbot.ticket.Ticket;
import com.xg7plugins.discordbot.ticket.TicketManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.pagination.MessagePaginationAction;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class ButtonClick extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getButton().isDisabled()) return;
        switch (event.getButton().getId()) {
            case "fechar" -> {
                Ticket ticket = TicketManager.closeTicket(event.getMember(), event.getChannelIdLong());
                if (ticket == null) {
                    event.reply("Você foi adicionado a este ticket, não pode fechá-lo!").setEphemeral(true).queue();
                    return;
                }
                if (ticket.isClosed()) {
                    event.reply("O ticket já está fechado!");
                    return;
                }
                EmbedBuilder builder = new EmbedBuilder();

                builder.setTitle("Ticket fechado!");
                builder.setColor(0x00FFFF);

                builder.setDescription("Clique abaixo para arquivar ou deletar o ticket");

                Button deletar = Button.danger("deletar", "Deletar ticket");
                Button arquivar = Button.primary("arquivar", "Arquivar ticket");

                deletar.withDisabled(!event.getMember().getPermissions().contains(Permission.ADMINISTRATOR));
                arquivar.withDisabled(!event.getMember().getPermissions().contains(Permission.ADMINISTRATOR));

                event.replyEmbeds(builder.build()).setActionRow(deletar, arquivar).queue();
            }
            case "deletar" -> {
                if (!TicketManager.deleteTicket(event.getMember(), event.getChannelIdLong())) {
                    event.reply("Você não tem permissão para deletar o ticket!").setEphemeral(true).queue();
                }
            }
            case "arquivar" -> {

                if (!event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
                    event.reply("Você não tem permissão para arquivar o ticket!").setEphemeral(true).queue();
                    return;
                }

                StringBuilder builder = new StringBuilder();

                List<Message> history = new java.util.ArrayList<>(event.getChannel().getIterableHistory().stream().toList());

                Collections.reverse(history);

                for (Message message : history) {
                    builder.append(message.getAuthor().getEffectiveName()).append(" > ").append(message.getTimeCreated().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))).append(" -> ").append(message.getContentRaw()).append("\n");
                }

                TicketManager.getTickets().stream().filter(ticket -> ticket.getOwner().getIdLong() == event.getMember().getIdLong()).forEach(ticket -> {
                    try {
                        SQLManager.archiveTicket(builder.toString(), ticket);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

                byte[] bytes = builder.toString().getBytes(StandardCharsets.UTF_8);

                ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

                EmbedBuilder builder1 = new EmbedBuilder();

                builder1.setTitle("Ticket Arquivado!");
                builder1.setColor(0x00FFFF);
                builder1.setDescription("Ticket salvo no banco de dados. Aqui está a log:");


                event.replyEmbeds(builder1.build()).setFiles(FileUpload.fromData(inputStream, "log.txt")).queue();


            }
        }

}
}
