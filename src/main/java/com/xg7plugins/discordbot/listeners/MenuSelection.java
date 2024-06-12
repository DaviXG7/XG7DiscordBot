package com.xg7plugins.discordbot.listeners;

import com.xg7plugins.discordbot.ticket.Ticket;
import com.xg7plugins.discordbot.ticket.TicketManager;
import com.xg7plugins.discordbot.ticket.TipoTicket;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MenuSelection extends ListenerAdapter {
    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals("menu:TipoDeTicket")) {
            String selected = event.getValues().get(0);

            if (TicketManager.containsUser(event.getMember())) {
                event.reply("Você já está com um ticket aberto!").setEphemeral(true).queue();
                return;
            }

            TicketManager.addTicket(event.getMember(), TipoTicket.valueOf(selected.toUpperCase()));
            event.reply("Ticket criado!").setEphemeral(true).queue();
        }
    }
}
