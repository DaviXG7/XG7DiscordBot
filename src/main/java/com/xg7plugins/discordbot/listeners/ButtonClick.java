package com.xg7plugins.discordbot.listeners;

import com.xg7plugins.discordbot.Main;
import com.xg7plugins.discordbot.ticket.TicketManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class ButtonClick extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        switch (event.getButton().getId()) {
            case "fechar" -> {
                if (!TicketManager.closeTicket(event.getMember(), event.getChannelIdLong())) {
                    event.reply("Você foi adicionado a este ticket, não pode fechá-lo!").setEphemeral(true).queue();
                    return;
                }
                EmbedBuilder builder = new EmbedBuilder();

                builder.setTitle("Ticket fechado!");
                builder.setColor(0x00FFFF);

                builder.setDescription("Clique abaixo para arquivar ou deletar o ticket");

                Button deletar = Button.danger("deletar", "Deletar ticket");
                Button arquivar = Button.primary("fechar", "Fechar ticket");
                event.replyEmbeds(builder.build()).setActionRow(deletar, arquivar).queue();
            }
            case "deletar" -> {
                if (!TicketManager.deleteTicket(event.getMember(), event.getChannelIdLong())) {
                    event.reply("Você não tem permissão para deletar o ticket!").setEphemeral(true).queue();
                }
                event.reply("Deletado com sucesso").setEphemeral(true).queue();
            }
        }

}
}
