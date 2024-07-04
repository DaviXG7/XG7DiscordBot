package com.xg7plugins.discordbot.commands.ticket.temp;

import com.xg7plugins.discordbot.Main;
import com.xg7plugins.discordbot.data.SQLManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.SQLException;

@AllArgsConstructor
@Getter
public class TempMessagesInDM {

    private User user;
    private int step;

    private int points;
    private String note;

    public TempMessagesInDM(User user) {
        this.user = user;
        this.step = 1;
    }

    public void nextStep(String response, MessageReceivedEvent event) {

        switch (step) {
            case 1 -> {
                try {
                    points = Integer.parseInt(response);
                    step++;
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("Deseja compartilhar sua avaliação?");
                    eb.setDescription("Você quer deixar uma avaliação pública para outras pessoas? \nDigite sim ou não");
                    eb.setFooter("Lembre-se de seguir as regras e não aceitaremos abaixo de 5 pontos, por favor se você deu menos de 5 entre em contato com a gente de novo");

                    event.getChannel().sendMessageEmbeds(eb.build()).queue();
                        SQLManager.update("UPDATE dmrate SET step = ?, note = ? WHERE userid = ?",
                                this.step,this.note,this.user.getId());
                } catch (RuntimeException e) {
                    event.getChannel().sendMessage("Por favor digite um número").queue();
                }

            }
            case 2 -> {
                if (points <= 4) {
                    event.getChannel().sendMessage("Desculpe mas sua avaliação é menor que 5").queue();
                    return;
                }
                if (response.toLowerCase().equals("sim")) {
                    event.getChannel().sendMessage("Envie sua avaliação agora:").queue();
                    step++;
                    SQLManager.update("UPDATE dmrate SET step = ?, note = ? WHERE userid = ?",
                            this.step,this.note,this.user.getId());
                    return;
                } else if (response.toLowerCase().equals("não")) {
                    SQLManager.delete("DELETE FROM dmrate WHERE userid = ?",
                            this.user.getId());
                    return;
                }


                event.getChannel().sendMessage("Tente de novo, digite \"sim\" ou \"não\"").queue();
            }
            case 3 -> {
                Main.guild.getTextChannelById(1256799583577575464L).sendMessage("## Usuário " + event.getAuthor().getName() + " fez uma avaliação\n" + response).queue();
                event.getChannel().sendMessage("Obrigado por nos avaliar!").queue();
                SQLManager.delete("DELETE FROM dmrate WHERE userid = ?", this.user.getId());
            }
        }

    }


}
