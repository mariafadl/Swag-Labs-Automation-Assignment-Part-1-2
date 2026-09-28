package utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;
import java.io.FileReader;
import java.io.IOException;

public class DataDriven {

    private static final String JSON_PATH =
            System.getProperty("user.dir") + "/src/test/resources/testData.json";

    /**
     * Reads a value from testData.json
     * @param userType key of the object (e.g. "validUser")
     * @param field    "username" or "password"
     */
    public static String jsonReader(String userType, String field) {
        try (FileReader reader = new FileReader(JSON_PATH)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            return root.getAsJsonObject(userType).get(field).getAsString();
        } catch (IOException e) {
            throw new RuntimeException("Could not read JSON file: " + JSON_PATH, e);
        }
    }

    public static List<String> jsonReaderList(String key) {
        try (FileReader reader = new FileReader(JSON_PATH)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray array = root.getAsJsonArray(key);
            List<String> list = new ArrayList<>();
            for (JsonElement e : array) {
                list.add(e.getAsString());
            }
            return list;
        } catch (IOException e) {
            throw new RuntimeException("Could not read JSON file: " + JSON_PATH, e);
        }
    }
}