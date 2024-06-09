package com.xg7plugins.discordbot.ticket;

import com.xg7plugins.discordbot.Main;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Getter
public class Ticket {

    private Member owner;
    private TextChannel ticketChannel;
    private List<Member> members;
    private String name;
    private TipoTicket tipoTicket;
    private long creationTime;

    public Ticket(Member owner, String name, TipoTicket tipoTicket) {
        this.owner = owner;
        this.tipoTicket = tipoTicket;
        this.name = name;
        this.creationTime = System.currentTimeMillis();
        ticketChannel = Main.guild.getCategoryById("1247163361783840869").createTextChannel("ticket-" + name)
                .addPermissionOverride(Main.guild.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                .addMemberPermissionOverride(Long.parseLong(owner.getId()), EnumSet.of(Permission.VIEW_CHANNEL), null).complete();
        members = new ArrayList<>();

        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Ticket criado por: " + owner.getNickname());
        builder.setColor(0x00FFFF);

        builder.addField("Motivo: ", tipoTicket.getDescricao(), true);

        builder.setFooter("O ticket fecha em 12h");

        Button fechar = Button.danger("fechar", "Fechar ticket");
        Button add = Button.primary("adcicionar", "Adicionar membro");


        ticketChannel.sendMessageEmbeds(builder.build()).setActionRow(fechar,add).queue();

    }

    public void close() {
        this.ticketChannel.delete().queue();
    }



}
