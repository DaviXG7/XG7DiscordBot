package com.xg7plugins.discordbot.data;

import lombok.Getter;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JSONManager {
    @Getter
    private static JSONObject defaults;
    private static final String path = "/root/XG7DiscordBot/default.json";
    public static void load() throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
            FileWriter fw = new FileWriter(path);
            fw.write("{}");
            fw.flush();
            fw.close();
        }
        defaults = new JSONObject(new String(Files.readAllBytes(Paths.get(path))));
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
