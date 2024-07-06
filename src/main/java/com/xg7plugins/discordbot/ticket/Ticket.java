package com.xg7plugins.discordbot.ticket;

import com.xg7plugins.discordbot.Main;
import com.xg7plugins.discordbot.data.JSONManager;
import com.xg7plugins.discordbot.data.SQLManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@AllArgsConstructor
@Getter
public class Ticket {

    private Member owner;
    private TextChannel ticketChannel;
    @Setter
    private List<Member> members;
    private TipoTicket tipoTicket;
    private long creationTime;
    @Setter
    private boolean isClosed;

    public Ticket(Member owner, TipoTicket tipoTicket) {
        this.owner = owner;
        this.tipoTicket = tipoTicket;
        this.creationTime = System.currentTimeMillis();
        this.ticketChannel = Main.guild.getCategoryById("1247163361783840869").createTextChannel("ticket-" + owner.getUser().getName())
                .addPermissionOverride(Main.guild.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                .addMemberPermissionOverride(Long.parseLong(owner.getId()), EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.USE_APPLICATION_COMMANDS), null).complete();
        this.members = new ArrayList<>();

        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Ticket criado por: " + owner.getEffectiveName());
        builder.setColor(0x00FFFF);

        builder.addField("Motivo: ", tipoTicket.getDescricao(), false);

        try {
            builder.addField("O ticket fecha ", "<t:" + (creationTime + JSONManager.getDefaults().getLong("ticketcooldown")) / 1000 + ":R>", true);
        } catch (Exception ignored) {}

        builder.setFooter("Aguarde ao atendimento", Main.guild.getIconUrl());

        Button fechar = Button.danger("fechar", "Fechar ticket");
        this.isClosed = false;


        ticketChannel.sendMessage(owner.getAsMention()).addEmbeds(builder.build()).setActionRow(fechar).queue();

    }

    public void close() {
        this.ticketChannel.delete().queue();
    }

    public void addMember(Member member) {
        this.ticketChannel.upsertPermissionOverride(member).grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND).queue();
        this.members.add(member);
        SQLManager.update("INSERT INTO ticketmembers(ticketid, memberid) VALUES (?, ?)", ticketChannel.getIdLong(), member.getIdLong());
    }
    public void removeMember(Member member) {
        this.ticketChannel.upsertPermissionOverride(member).deny(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND).queue();
        this.members.remove(member);
        SQLManager.update("DELETE FROM ticketmembers WHERE ticketid = ? AND memberid = ?", ticketChannel.getIdLong(), member.getIdLong());
    }

}
