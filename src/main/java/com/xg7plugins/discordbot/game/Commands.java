package com.xg7plugins.discordbot.game;

import com.xg7plugins.discordbot.Main;
import com.xg7plugins.discordbot.commands.Command;
import com.xg7plugins.discordbot.data.JSONManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public class Commands implements Command {

    private List<Command> subcommands;

    public Commands() {
        subcommands = new ArrayList<>();
        subcommands.add(new Iniciar());
        subcommands.add(new AdicionarMembro());
        subcommands.add(new ColocarPadrao());
    }

    public List<SubcommandData> getSubCommandData() {
        return subcommands.stream().map(command -> new SubcommandData(command.getName(), command.getDescription()).addOptions(command.getOptions())).collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return "digitgame";
    }

    @Override
    public String getDescription() {
        return "Comandos do jogo digit";
    }

    @Override
    public void onSlashCommandEvent(SlashCommandInteractionEvent event) {
        Command command = subcommands.stream().filter(subcommand -> subcommand.getName().equalsIgnoreCase(event.getSubcommandName())).findFirst().orElse(null);
        if (command == null) return;
        command.onSlashCommandEvent(event);
    }

    @Override
    public List<OptionData> getOptions() {
        return Command.super.getOptions();
    }

    static class Iniciar implements Command {

        @Override
        public String getName() {
            return "iniciar";
        }

        @Override
        public String getDescription() {
            return "Inicia o jogo digit";
        }

        @Override
        public void onSlashCommandEvent(SlashCommandInteractionEvent event) {
            if (event.getOption("CanalPrivado").getAsBoolean()) {
                GameManager.addGame(event.getMember(),
                        Main.guild.getCategoryById(1257913583896105071L).createTextChannel("digit-" + event.getMember().getUser().getName())
                                .addPermissionOverride(Main.guild.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                                .addPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND), null).complete()
                );
                event.reply("Canal criado!").setEphemeral(true).queue();
                return;
            }
            if (event.getChannel().getIdLong() != JSONManager.getDefaults().getLong("gamechannel")) {
                event.reply("Você só pode iniciar um jogo no canal do jogo ou iniciar um jogo privado!").setEphemeral(true).queue();
                return;
            }
            GameManager.addGame(event.getMember(), event.getChannel().asTextChannel());
        }

        @Override
        public List<OptionData> getOptions() {
            return List.of(new OptionData(OptionType.BOOLEAN, "canalprivado", "Você pode criar um jogo privado para seus amigos").setRequired(true));
        }
    }
    static class AdicionarMembro implements Command {

        @Override
        public String getName() {
            return "addmembro";
        }

        @Override
        public String getDescription() {
            return "Adiciona uma membro de um jogo";
        }

        @Override
        public void onSlashCommandEvent(SlashCommandInteractionEvent event) {

        }

        @Override
        public List<OptionData> getOptions() {
            return List.of(new OptionData(OptionType.USER, "adicionar", "Adiciona um membro para o jogo", true));
        }
    }
    static class ColocarPadrao implements Command {

        @Override
        public String getName() {
            return "canal";
        }

        @Override
        public String getDescription() {
            return "Coloca o canal padrão do jogo";
        }

        @Override
        public void onSlashCommandEvent(SlashCommandInteractionEvent event) {
            if (!event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
                event.reply("Você não tem permissão para usar este comando!").setEphemeral(true).queue();
                return;
            }
            JSONManager.setDefaults("gamechannel", event.getOption("canal").getAsChannel().getIdLong());
            try {
                JSONManager.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public List<OptionData> getOptions() {
            return List.of(new OptionData(OptionType.CHANNEL, "canal", "Coloca o canal padrão do jogo", true));
        }
    }


}
