package com.xg7plugins.discordbot.commands.other;

import com.xg7plugins.discordbot.commands.Command;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.List;

public class ChatGPT implements Command {
    @Override
    public String getName() {
        return "gpt";
    }

    @Override
    public String getDescription() {
        return "Chat gpt no discord";
    }

    @Override
    public void onSlashCommandEvent(SlashCommandInteractionEvent event) {
        event.reply("Infelizmente ainda não temos essa função").queue();
    }

    @Override
    public List<OptionData> getOptions() {
        return Collections.singletonList(new OptionData(OptionType.STRING, "prompt", "O que será enviado para o chatgpt", true, false));
    }

    static class RequestBodyData {
        String inputs;

        RequestBodyData(String inputs) {
            this.inputs = inputs;
        }
    }
}
