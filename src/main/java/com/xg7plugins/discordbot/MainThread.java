package com.xg7plugins.discordbot;

import com.xg7plugins.discordbot.data.JSONManager;
import com.xg7plugins.discordbot.data.SQLManager;
import com.xg7plugins.discordbot.ticket.TicketManager;
import kotlin.Pair;
import net.dv8tion.jda.api.entities.Member;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Scanner;
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
                    try {
                    if (ticket.getCreationTime() + JSONManager.getDefaults().getLong("ticketcooldown") < System.currentTimeMillis()) {
                        if (!ticket.isClosed())TicketManager.deleteTicket(ticket);
                    }
                    } catch (Exception ignored) {}
                });

                TicketManager.getCreatedTicket().removeIf(cooldown -> cooldown.getFirst() + 1200000 < System.currentTimeMillis());


            }
        };

        timer.schedule(mainThread, 0, 100);

    }
    public static void stop() {
        mainThread.cancel();
        timer.cancel();

    }

    public static Thread in() {
        return new Thread(() -> {
            while (true) {
                String response = new Scanner(System.in).nextLine();

                switch (response) {
                    case "stop" -> {
                        System.out.println("Parando o sistema...");
                        try {
                            JSONManager.save();
                            SQLManager.close();
                        } catch (IOException | SQLException e) {
                            System.exit(2);
                            throw new RuntimeException(e);
                        }
                        System.exit(0);
                    }
                    case "reload" -> {
                        System.out.println("Recarregando...");
                        try {
                            JSONManager.load();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    default -> {
                        System.out.println("Comando não encontrado: " + response);
                    }
                }


            }
        });
    }

}
