package com.xg7plugins.discordbot.data;

import lombok.Getter;
import lombok.SneakyThrows;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        System.out.println("Conexão com o banco de dados fechado");
    }

    @SneakyThrows
    public static Future<List<Map<String, Object>>> select(String sql, Object... args) {
        return executorService.submit(() -> {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {

                for (int i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    List<Map<String, Object>> list = new ArrayList<>();
                    while (rs.next()) {
                        Map<String, Object> map = new HashMap<>();

                        for (int i = 1; i < rs.getMetaData().getColumnCount() + 1; i++){
                            map.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
                        }
                        list.add(map);
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





}
