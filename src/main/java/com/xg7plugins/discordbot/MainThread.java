package com.xg7plugins.discordbot;

import com.xg7plugins.discordbot.ticket.TicketManager;

public class MainThread {

    public static Thread mainThread;

    public static void start() {
        mainThread = new Thread(() -> {
            while (true) {

                //Tickets

                TicketManager.getTickets().forEach(ticket -> {
                    if (ticket.getCreationTime() + 43200000 < System.currentTimeMillis()) {
                        TicketManager.removeTicket(ticket);
                    }
                });

            }
        });
    }
    public static void stop() {
        mainThread.interrupt();
        mainThread = null;
    }

}
