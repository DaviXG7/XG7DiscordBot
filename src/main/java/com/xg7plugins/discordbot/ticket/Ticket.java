package com.xg7plugins.discordbot.ticket;

import com.xg7plugins.discordbot.Main;
import com.xg7plugins.discordbot.data.SQLManager;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

@Getter
public class Ticket {

    private Member owner;
    private TextChannel ticketChannel;
    private List<Member> members;
    private TipoTicket tipoTicket;
    private long creationTime;
    private boolean isClosed;

    public Ticket(Member owner, TipoTicket tipoTicket) {
        this.owner = owner;
        this.tipoTicket = tipoTicket;
        this.creationTime = System.currentTimeMillis();
        this.ticketChannel = Main.guild.getCategoryById("1247163361783840869").createTextChannel("ticket-" + owner.getUser().getName())
                .addPermissionOverride(Main.guild.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                .addMemberPermissionOverride(Long.parseLong(owner.getId()), EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND), null).complete();
        this.members = new ArrayList<>();

        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Ticket criado por: " + owner.getEffectiveName());
        builder.setColor(0x00FFFF);

        builder.addField("Motivo: ", tipoTicket.getDescricao(), false);

        builder.addField("O ticket fecha ", "<t:" + (creationTime + 43200000) / 1000 + ":R>", true);

        builder.setFooter("Aguarde ao atendimento", Main.guild.getIconUrl());

        Button fechar = Button.danger("fechar", "Fechar ticket");
        this.isClosed = false;


        ticketChannel.sendMessage(owner.getAsMention()).addEmbeds(builder.build()).setActionRow(fechar).queue();

    }

    public Ticket(long ownerid, long channelId, TipoTicket tipoTicket, long creationTime, List<Member> members) {

        this.owner = Main.guild.retrieveMemberById(ownerid).complete();
        this.ticketChannel = Main.guild.getTextChannelById(channelId);
        this.tipoTicket = tipoTicket;
        this.creationTime = creationTime;
        this.members = members;

    }

    public void close() {
        this.ticketChannel.delete().queue();
    }

    public void addMember(Member member) {
        this.ticketChannel.upsertPermissionOverride(member).grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND).queue();
        this.members.add(member);
        try {
            SQLManager.addTicketMember(member, this);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void removeMember(Member member) {
        this.ticketChannel.upsertPermissionOverride(member).deny(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND).queue();
        this.members.remove(member);
        try {
            SQLManager.removeTicketMember(member, this);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



}
