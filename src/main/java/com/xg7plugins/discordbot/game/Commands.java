package com.xg7plugins.discordbot.game;

import com.xg7plugins.discordbot.Main;
import com.xg7plugins.discordbot.commands.Command;
import com.xg7plugins.discordbot.data.JSONManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Commands implements Command {

    private List<Command> subcommands;

    public Commands() {
        subcommands = new ArrayList<>();
        subcommands.add(new Iniciar());
        subcommands.add(new ColocarPadrao());
        subcommands.add(new Rank());
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
            return "criarjogo";
        }

        @Override
        public String getDescription() {
            return "Cria uma instância do jogo digit";
        }

        @Override
        public void onSlashCommandEvent(SlashCommandInteractionEvent event) {
            if (event.getOption("canalprivado").getAsBoolean()) {
                if (GameManager.getGame(event.getMember().getIdLong()) != null) {
                    event.reply("Você já criou o canal privado do jogo!").setEphemeral(true).queue();
                    return;
                }
                GameManager.addGame(event.getMember(),
                        Main.guild.getCategoryById(1257913583896105071L).createTextChannel("digit-" + event.getMember().getUser().getName())
                                .addPermissionOverride(Main.guild.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                                .addPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND), null).complete()
                );

                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Digit");
                builder.setDescription("Digit é um jogo cujo objetivo é digitar a palavra primeiro que todos!");
                builder.addField("Aviso", "Os pontos adiquiridos nesse jogo não vão contar ao ranking global!", false);
                builder.addField("Comando para começar", "`d!comecar`", true);
                builder.addField("Adicione um membro clicando abaixo", "`coloque o nome do usuário (não o apelido)`", true);
                builder.setColor(0x00FFFF);
                builder.setFooter("Bora começar!", "https://xg7plugins.com/imgs/logo.png");

                Button button = Button.primary("modalgame", "Adicione um membro");

                GameManager.getGame(event.getMember().getIdLong()).getChannel().sendMessageEmbeds(builder.build()).setActionRow(button).queue();
                event.reply("Canal privado criado!").setEphemeral(true).queue();
                return;
            }
            if (GameManager.getDefaultGame() != null) {
                event.reply("Já tem um jogo acontecendo no canal de jogos!").setEphemeral(true).queue();
                return;
            }
            event.reply("Jogo iniciado!").setEphemeral(true).queue();
            GameManager.initDefault();
        }

        @Override
        public List<OptionData> getOptions() {
            return List.of(new OptionData(OptionType.BOOLEAN, "canalprivado", "Você pode criar um jogo privado para seus amigos").setRequired(true));
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
    static class Rank implements Command {

        @Override
        public String getName() {
            return "rankglobal";
        }

        @Override
        public String getDescription() {
            return "Vê os ranks globais";
        }

        @Override
        public void onSlashCommandEvent(SlashCommandInteractionEvent event) {

            if (event.getOption("usuário") != null) {

                User user = event.getOption("usuário").getAsUser();

                if (GameManager.getRankingGlobal().get(user.getIdLong()) == null) {
                    event.reply("Este usuário nunca jogou!").setEphemeral(true).queue();
                    return;
                }

                EmbedBuilder builder = new EmbedBuilder();


                builder.setTitle("Rank do " + user.getEffectiveName());
                builder.addField("Pontos", GameManager.getRankingGlobal().get(user.getIdLong()).toString(), true);
                builder.addField("Posição no rank global", String.valueOf(GameManager.getRankingGlobal().keySet().stream().toList().indexOf(user.getIdLong()) + 1),  true);
                builder.setFooter("Rank global do digit game");
                builder.setColor(0x00ffff);

                event.replyEmbeds(builder.build()).queue();

                return;

            }

            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Ranking global");

            StringBuilder sb = new StringBuilder();
            AtomicInteger index = new AtomicInteger();
            GameManager.getRankingGlobal().entrySet().stream()
                    .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                    .limit(10).forEach(e -> {
                        index.getAndIncrement();
                        sb.append(index.get()).append(". ").append(Main.guild.retrieveMemberById(e.getKey()).complete().getAsMention()).append(" ").append(e.getValue()).append("\n");
                    });

            builder.addField("Pontos", sb.toString(), true);
            builder.setFooter("Ranking global do digit game");
            event.replyEmbeds(builder.build()).queue();


        }

        @Override
        public List<OptionData> getOptions() {
            return List.of(new OptionData(OptionType.USER, "usuário", "rank por usuário", false));
        }
    }


}
