package com.xg7plugins.discordbot.ticket;

import com.xg7plugins.discordbot.Main;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Getter
public class Ticket {

    private Member owner;
    private TextChannel ticketChannel;
    private List<Member> members;
    private int index;
    private TipoTicket tipoTicket;

    public Ticket(Member owner, int index, TipoTicket tipoTicket) {
        this.owner = owner;
        this.tipoTicket = tipoTicket;
        this.index = index;
        ticketChannel = Main.guild.getCategoryById("1247163361783840869").createTextChannel("ticket-" + index)
                .addPermissionOverride(Main.guild.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                .addMemberPermissionOverride(Long.parseLong(owner.getId()), EnumSet.of(Permission.VIEW_CHANNEL), null).complete();
        members = new ArrayList<>();

        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Ticket criado por: " + owner.getNickname());
        builder.setColor(0x00FFFF);

        builder.addField("Motivo: ", tipoTicket.name(), true);

        builder.setFooter("Esqueci o que eu ia colocar ;-");
        ticketChannel.sendMessageEmbeds(builder.build()).queue();

    }



}
