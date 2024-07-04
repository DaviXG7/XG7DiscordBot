package com.xg7plugins.discordbot.data;

import com.xg7plugins.discordbot.Main;
import com.xg7plugins.discordbot.commands.ticket.temp.TempMessagesInDM;
import com.xg7plugins.discordbot.ticket.Ticket;
import com.xg7plugins.discordbot.ticket.TipoTicket;
import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Member;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SQLManager {

    @Getter
    private static Connection connection;

    private static final ExecutorService executorService = Executors.newFixedThreadPool(2);


    public static void load() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection("");
        connection.setAutoCommit(true);
        System.out.println("Banco de dados carregados com sucesso!");
    }

    public static void close() throws SQLException {
        connection.close();
        connection = null;
        System.out.println("ConexÃ£o com o banco de dados fechado");
    }

    @SneakyThrows
    public static Future<List<List<Object>>> select(String sql, Object... args) {
        return executorService.submit(() -> {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                for (int i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    final List<List<Object>> list = new ArrayList<>();
                    int columns = rs.getMetaData().getColumnCount();
                    while (rs.next()) {
                        final List<Object> cols = new ArrayList<>();
                        for (int i = 1; i <= columns; i++) {
                            cols.add(rs.getObject(i));
                        }
                        list.add(cols);
                    }
                    return list;
                }
            }
        });
    }

    @SneakyThrows
    public static  <T> Future<List<T>> select(Class<T> clazz, String sql, Object... args) {
        return executorService.submit(() -> {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                for (int i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    final List<T> list = new ArrayList<>();
                    final ResultSetMetaData meta = rs.getMetaData();
                    final List<String> columns = new ArrayList<>();
                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                        columns.add(meta.getColumnLabel(i));
                    }
                    while (rs.next()) {
                        list.add(instanceOf(clazz, columns, rs));
                    }
                    return list;
                }
            }
        });
    }

    @SneakyThrows
    public static int update(String sql, Object... args) {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            return ps.executeUpdate();
        }
    }

    @SneakyThrows
    public static int delete(String sql, Object... args) {
        return update(sql, args);
    }

    private static <T> T instanceOf(Class<T> clazz, List<String> columns, ResultSet rs) {
        try {
            T bean = clazz.newInstance();
            int index = 0;
            for (String column : columns) {
                index++;
                Field f = clazz.getField(column);
                f.set(bean, rs.getObject(index, f.getType()));
            }
            return bean;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | SQLException | NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }



}
