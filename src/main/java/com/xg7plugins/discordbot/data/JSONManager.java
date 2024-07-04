package com.xg7plugins.discordbot.data;

import lombok.Getter;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JSONManager {
    @Getter
    private static JSONObject defaults;
    private static final String path = JSONManager.class.getClassLoader().getResource("defaults.json").getPath().substring(1);
    public static void load() throws IOException {
        String string = new String(Files.readAllBytes(Paths.get(path)));
        defaults = new JSONObject(string);
        System.out.println("JSON carregado com sucesso!");
    }
    public static void setDefaults(String column, Object value) {
        defaults.put(column, value);
    }
    public static void save() throws IOException {
        FileWriter file = new FileWriter(path);
        file.write(defaults.toString(4));
        file.flush();
        System.out.println("JSON salvo com sucesso!");
    }
}
