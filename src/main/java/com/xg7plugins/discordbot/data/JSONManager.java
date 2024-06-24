package com.xg7plugins.discordbot.data;

import lombok.Getter;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JSONManager {
    @Getter
    private static JSONObject defaults;
    private static final String path = "C:\\Users\\davis\\Documents\\XG7Plugins\\PastaSite\\bot\\src\\main\\java\\com\\xg7plugins\\discordbot\\data\\defaults.json";
    public static void load() throws IOException {
        String string = new String(Files.readAllBytes(Paths.get(path)));
        defaults = new JSONObject(string);
    }
    public static void setDefaults(String column, Object value) {
        defaults.put(column, value);
    }
    public static void save() throws IOException {
        FileWriter file = new FileWriter(path);
        file.write(defaults.toString(4));
        file.flush();
    }
}
