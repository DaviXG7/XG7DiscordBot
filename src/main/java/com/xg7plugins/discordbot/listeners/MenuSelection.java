package com.xg7plugins.discordbot.listeners;

import com.xg7plugins.discordbot.ticket.Ticket;
import com.xg7plugins.discordbot.ticket.TicketManager;
import com.xg7plugins.discordbot.ticket.TipoTicket;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MenuSelection extends ListenerAdapter {
    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        System.out.println(event.getComponentId());
        if (event.getComponentId().equals("menu:TipoDeTicket")) {
            String selected = event.getValues().get(0);
            System.out.println("a");

            TicketManager.addTicket(event.getMember(), TipoTicket.valueOf(selected.toUpperCase()));
            event.reply("Ticket criado!").queue();
        }
    }
}
