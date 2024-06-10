package com.xg7plugins.discordbot;

import com.xg7plugins.discordbot.ticket.TicketManager;

import java.util.Timer;
import java.util.TimerTask;

public class MainThread {

    public static TimerTask mainThread;
    public static Timer timer = new Timer();

    public static void start() {
        mainThread = new TimerTask() {
            @Override
            public void run() {
                TicketManager.getTickets().forEach(ticket -> {

                    if (ticket.getCreationTime() + 43200000 < System.currentTimeMillis()) {
                        TicketManager.removeTicket(ticket);
                    }
                });
            }
        };

        timer.schedule(mainThread, 0, 100);

    }
    public static void stop() {
        mainThread.cancel();
        timer.cancel();

    }

}
