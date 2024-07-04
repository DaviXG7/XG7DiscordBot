package com.xg7plugins.discordbot.commands.ticket.temp;

import com.xg7plugins.discordbot.data.SQLManager;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class DMTempManager extends ListenerAdapter {

    private static List<TempMessagesInDM> tempMessagesInDMs;

    public static void load(List<TempMessagesInDM> users) {
        tempMessagesInDMs = users;
    }

    public static void addUser(User user) {
        TempMessagesInDM tempMessagesInDM = new TempMessagesInDM(user);
        tempMessagesInDMs.add(tempMessagesInDM);
        SQLManager.update("INSERT INTO dmrate(userid, points, step, note) VALUES (?, ?, ?, ?)",
                tempMessagesInDM.getUser().getIdLong(),
                tempMessagesInDM.getPoints(),
                tempMessagesInDM.getStep(),
                tempMessagesInDM.getNote());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (!event.isFromType(ChannelType.PRIVATE)) return;
        TempMessagesInDM tempMessagesInDM1 = tempMessagesInDMs.stream().filter(user -> event.getAuthor().getId().equals(user.getUser().getId())).findFirst().orElse(null);
        if (tempMessagesInDM1 == null) return;

        tempMessagesInDM1.nextStep(event.getMessage().getContentRaw(), event);

    }


}
