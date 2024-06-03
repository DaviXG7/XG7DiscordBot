package com.xg7plugins.discordbot.ticket;

import com.xg7plugins.discordbot.Main;
import lombok.Getter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

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
        ticketChannel = Main.guild.createTextChannel("ticket-" + index)
                .addPermissionOverride(Main.guild.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                .addMemberPermissionOverride(Long.parseLong(owner.getId()), EnumSet.of(Permission.VIEW_CHANNEL), null).complete();
        members = new ArrayList<>();
    }



}
