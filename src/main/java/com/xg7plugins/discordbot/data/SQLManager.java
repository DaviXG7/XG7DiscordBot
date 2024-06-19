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
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/discordbot", "root", "");
        connection.setAutoCommit(true);
        System.out.println(connection);
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
                members.add(Main.guild.getMemberById(usersResult.getLong("memberid")));
            }

            tickets.add(new Ticket(resultSet.getLong("ownerid"), resultSet.getLong("channelid"), TipoTicket.valueOf(resultSet.getString("tickettype")), resultSet.getLong("creationtime"), members));
        }

        return tickets;
    }
    public static void setTickets(List<Ticket> tickets) throws SQLException {
        connection.prepareStatement("DELETE FROM ticketmembers").executeUpdate();
        connection.prepareStatement("DELETE FROM tickets").executeUpdate();

        for (Ticket ticket : tickets) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO tickets(ownerid,channelid,tickettype,creationtime) VALUES (?, ?, ?, ?)");
            statement.setLong(1, ticket.getOwner().getIdLong());
            statement.setLong(2, ticket.getTicketChannel().getIdLong());
            statement.setString(3, ticket.getTipoTicket().name());
            statement.setLong(4, ticket.getCreationTime());
            statement.executeUpdate();

            for (Member member : ticket.getMembers()) {
                PreparedStatement users = connection.prepareStatement("INSERT INTO ticketmembers(ticketid, memberid) VALUES (?, ?)");
                users.setLong(1, ticket.getTicketChannel().getIdLong());
                users.setLong(2, member.getIdLong());
                users.executeUpdate();
            }
        }
    }

    public static void archiveTicket(String text, Ticket ticket) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO archvedtickets(owner,date,text) VALUES (?, ?, ?)");
        statement.setLong(1, ticket.getOwner().getIdLong());
        statement.setDate(2, new java.sql.Date(ticket.getCreationTime()));
        statement.setString(3, text);
        statement.executeUpdate();

    }


}
