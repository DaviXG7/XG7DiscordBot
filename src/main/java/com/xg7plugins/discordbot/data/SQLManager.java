package com.xg7plugins.discordbot.data;

import com.xg7plugins.discordbot.Main;
import com.xg7plugins.discordbot.ticket.Ticket;
import com.xg7plugins.discordbot.ticket.TipoTicket;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLManager {

    @Getter
    private static Connection connection;

    public static void load() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://u148_XIs3okey1E:AaEd.GrXqfSw%40Lv!IrQN3!uS@170.231.121.12:3306/s148_discord");
        connection.setAutoCommit(true);
    }

    public static List<Ticket> getTickets() throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM tickets");
        ResultSet resultSet = statement.executeQuery();


        while (resultSet.next()) {

            PreparedStatement users = connection.prepareStatement("SELECT * FROM ticketmembers WHERE ticketid = ?");
            users.setString(1, resultSet.getString("channelid"));
            ResultSet usersResult = users.executeQuery();

            List<Member> members = new ArrayList<>();

            while (usersResult.next()) {
                members.add(Main.guild.retrieveMemberById(usersResult.getLong("memberid")).complete());
            }

            tickets.add(new Ticket(resultSet.getLong("ownerid"), resultSet.getLong("channelid"), TipoTicket.valueOf(resultSet.getString("tickettype")), resultSet.getLong("creationtime"), members, resultSet.getBoolean("isclosed")));
        }

        return tickets;
    }

    public static void deleteTicket(Ticket ticket) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("DELETE FROM tickets WHERE channelid = ?");
        statement.setLong(1, ticket.getTicketChannel().getIdLong());
        statement.executeUpdate();

        PreparedStatement users = connection.prepareStatement("DELETE FROM ticketmembers WHERE ticketid = ?");
        users.setLong(1, ticket.getTicketChannel().getIdLong());
        users.executeUpdate();
    }
    public static void addTicketMember(Member member, Ticket ticket) throws SQLException {
        PreparedStatement users = connection.prepareStatement("INSERT INTO ticketmembers(ticketid, memberid) VALUES (?, ?)");
        users.setLong(1, ticket.getTicketChannel().getIdLong());
        users.setLong(2, member.getIdLong());
        users.executeUpdate();
    }
    public static void removeTicketMember(Member member, Ticket ticket) throws SQLException {
        PreparedStatement users = connection.prepareStatement("DELETE FROM ticketmembers WHERE ticketid = ? AND memberid = ?");
        users.setLong(1, ticket.getTicketChannel().getIdLong());
        users.setLong(2, member.getIdLong());
        users.executeUpdate();
    }
    public static void addTicket(Ticket ticket) throws SQLException {

            PreparedStatement statement = connection.prepareStatement("INSERT INTO tickets(ownerid,channelid,tickettype,creationtime,isclosed) VALUES (?, ?, ?, ?, ?)");
            statement.setLong(1, ticket.getOwner().getIdLong());
            statement.setLong(2, ticket.getTicketChannel().getIdLong());
            statement.setString(3, ticket.getTipoTicket().name());
            statement.setLong(4, ticket.getCreationTime());
            statement.setBoolean(5, ticket.isClosed());
            statement.executeUpdate();

    }

    public static void archiveTicket(String text, Ticket ticket) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO archivedtickets(owner,date,text) VALUES (?, ?, ?)");
        statement.setLong(1, ticket.getOwner().getIdLong());
        statement.setTimestamp(2, new java.sql.Timestamp(ticket.getCreationTime()));
        statement.setString(3, text);
        statement.executeUpdate();

    }


}
