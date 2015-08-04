package com.javaee.crumbsOfAPI.json;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonObject;

/**
 *
 * @author Constantin Alin
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        String fileName = "Duke.json";
        String userDir = System.getProperty("user.dir");

        addDescription("Create JsonObject from file");
        JsonObject jsonObject = null;
        try {
            jsonObject = ObjectModelHelper.createJsonObjectFromFile(Paths.get(userDir.concat("/files/").concat(fileName)));
        } catch (FileNotFoundException e) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
        }

        addDescription("Encode JsonObject to String");
        String fromJsonObject = ObjectModelHelper.encodeJsonObjectToString(jsonObject);

        addDescription("Encode String to JsonObject");
        JsonObject fromString = ObjectModelHelper.encodeStringToJsonObject(fromJsonObject);

        addDescription("Parse and prints JsonObject to console");
        ObjectModelHelper.parseJsonObject(fromString);

        addDescription("Encode JsonObject to Map<String, Object>");
        Map<String, Object> mapFromJsonObject = ObjectModelHelper.encodeJsonObjectToMap(jsonObject);

        addDescription("Encode Map<String, Object> to JsonObject");
        JsonObject fromMap = ObjectModelHelper.encodeMapToJsonObject(mapFromJsonObject);

        addDescription("Write JsonObject to file");
        try {
            ObjectModelHelper.writeJsonObjectToFile(Paths.get(userDir.concat("/files/generated/").concat(fileName)), fromMap);
        } catch (IOException e) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    public static void addDescription(String description) {
        System.out.println("\n### " + description + " ###");
    }

}
