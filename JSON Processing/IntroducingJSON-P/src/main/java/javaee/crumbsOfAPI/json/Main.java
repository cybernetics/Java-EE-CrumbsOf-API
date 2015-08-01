package javaee.crumbsOfAPI.json;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import static javax.json.JsonValue.ValueType.ARRAY;
import static javax.json.JsonValue.ValueType.NUMBER;
import static javax.json.JsonValue.ValueType.OBJECT;
import static javax.json.JsonValue.ValueType.STRING;
import javax.json.JsonWriter;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.json.stream.JsonParser;

/**
 *
 * @author Alin Constantin
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        String userDir = System.getProperty("user.dir");

        System.out.println("\n ##### Create JsonObject from JSON file using the Object Model API ##### ");

        JsonObject jsonObject = null;

        try {
            JsonReader reader = Json.createReader(new FileInputStream(Paths.get(userDir.concat("/files/Duke.json")).toFile()));
            jsonObject = reader.readObject();
        } catch (FileNotFoundException e) {
            // Handle exception
        }
        parseJsonObject(jsonObject);

        System.out.println("\n ##### Encode JsonObject to String using the Object Model API ##### ");

        StringWriter stringWriter = new StringWriter();

        try (JsonWriter jsonWriter = Json.createWriter(stringWriter)) {
            jsonWriter.writeObject(jsonObject);
        }

        String jsonString = stringWriter.toString();
        System.out.println(jsonString);

        System.out.println("\n ##### Encode List<Player> to JsonObject using the Object Model API ##### ");

        List<Player> playersList = new ArrayList<>(Arrays.asList(
                new Player(true, "Novak Djokovic", 27, new Date()),
                new Player(false, "Rafael Nadal", 28, new Date()),
                new Player(true, "Andy Murray", 27, new Date())));

        JsonObjectBuilder builder = Json.createObjectBuilder();

        int i = 0;
        for (Player p : playersList) {
            i++;
            builder.add(String.valueOf(i), Json.createArrayBuilder()
                    .add(p.getRighthanded())
                    .add(p.getName())
                    .add(p.getAge())
                    .add(new SimpleDateFormat("yyyy-MM-dd").format(p.getBirthdate()))
            );
        }

        jsonObject = builder.build();
        parseJsonObject(jsonObject);

        System.out.println("\n ##### Encode Map<String, Player> to JsonObject using the Object Model API ##### ");

        Map<String, Player> playersMap = new HashMap<>();
        playersMap.put("I", new Player(false, "Rafael Nadal", 28, new Date()));
        playersMap.put("II", new Player(true, "Novak Djokovic", 27, new Date()));
        playersMap.put("III", new Player(true, "Andy Murray", 27, new Date()));

        builder = Json.createObjectBuilder();

        for (Map.Entry<String, Player> entrySet : playersMap.entrySet()) {
            Player value = entrySet.getValue();
            builder.add(entrySet.getKey(), Json.createObjectBuilder()
                    .add("righthanded", value.getRighthanded())
                    .add("name", value.getName())
                    .add("age", value.getAge())
                    .add("birthdate", new SimpleDateFormat("yyyy-MM-dd").format(value.getBirthdate()))
            );
        }

        jsonObject = builder.build();
        parseJsonObject(jsonObject);

        System.out.println("\n ##### Write a hardcoded JsonObject to file using the Object Model API ##### ");

        JsonObject createdObject = Json.createObjectBuilder()
                .add("firstName", "Duke")
                .add("lastName", "Java")
                .add("age", 25)
                .add("streetAddress", "100 Internet Dr")
                .add("city", "JavaTown")
                .add("state", "JA")
                .add("postalCode", "12345")
                .add("phoneNumbers", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                                .add("type", "mobile")
                                .add("number", "111-111-1111"))
                        .add(Json.createObjectBuilder()
                                .add("type", "home")
                                .add("number", "222-222-2222")))
                .add("neighbours", Json.createArrayBuilder()
                        .add("C++")
                        .add("C"))
                .add("bike", true)
                .add("car", false)
                .add("girlfriend", JsonValue.NULL)
                .build();

        Path filePath = Paths.get(userDir.concat("/files/ObjectModel-genFile.json"));

        try {
            if (!Files.exists(filePath, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
                Files.createFile(filePath);
            }

            try (JsonWriter writer = Json.createWriter(new FileOutputStream(filePath.toFile()))) {
                writer.writeObject(createdObject);
            }

            System.out.println("Done..");

        } catch (IOException e) {
            // Handle exception
        }

        System.out.println("\n ##### Parse and prints a JsonObject to console using the Streaming API ##### ");

        JsonParser parser = Json.createParser(new StringReader(jsonString));
        parseJson(parser);

        System.out.println("\n ##### Parse and prints a JSON file to console using the Streaming API ##### ");

        try {
            parser = Json.createParser(new FileInputStream(filePath.toFile()));
            parseJson(parser);
        } catch (FileNotFoundException e) {
            // Handle exception
        }

        System.out.println("\n ##### Write a hardcoded JsonObject to file using the Streaming API ##### ");

        Path filePath2 = Paths.get(userDir.concat("/files/StreamingModel-genFile.json"));

        Map<String, Object> properties = new HashMap<>(1);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonGeneratorFactory jsonGenFactory = Json.createGeneratorFactory(properties);

        try {
            if (!Files.exists(filePath2, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
                Files.createFile(filePath2);
            }

            FileWriter writer = new FileWriter(filePath2.toFile());

            try (JsonGenerator gen = jsonGenFactory.createGenerator(writer)) {
                gen.writeStartObject()
                        .write("firstName", "Duke")
                        .write("lastName", "Java")
                        .write("streetAddress", "100 Internet Dr")
                        .write("city", "JavaTown")
                        .write("state", "JA")
                        .writeStartArray("phoneNumbers")
                        .writeStartObject()
                        .write("type", "mobile")
                        .write("number", "111-111-1111")
                        .writeEnd()
                        .writeStartObject()
                        .write("type", "home")
                        .write("number", "222-222-2222")
                        .writeEnd()
                        .writeEnd()
                        .writeEnd();
            }

            System.out.println("Done..");

        } catch (IOException e) {
            // Handle exception
        }
    }

    // Parse and prints JSON data to console using the Object Model API
    private static void parseJsonObject(JsonObject object) {

        for (String name : object.keySet()) {
            parseJSON(object.get(name), name);
        }
    }

    // Navigating an Object Model.
    private static void parseJSON(JsonValue obj, String key) {

        if (key != null) {
            System.out.print("KEY_NAME " + key + ": ");
        }

        switch (obj.getValueType()) {
            case OBJECT:
                System.out.println("OBJECT");
                JsonObject object = (JsonObject) obj;
                for (String name : object.keySet()) {
                    parseJSON(object.get(name), name);
                }
                break;
            case ARRAY:
                System.out.println("VALUE_ARRAY");
                JsonArray array = (JsonArray) obj;
                for (JsonValue val : array) {
                    parseJSON(val, null);
                }
                break;
            case STRING:
                JsonString st = (JsonString) obj;
                System.out.println("VALUE_STRING " + st.getString());
                break;
            case NUMBER:
                JsonNumber num = (JsonNumber) obj;
                System.out.println("VALUE_NUMBER " + num.toString());
                break;
            default:
                System.out.println(obj.getValueType().toString());
                break;
        }
    }

    // Parse and prints JSON data to console using the Streaming API
    private static void parseJson(JsonParser parser) {

        while (parser.hasNext()) {
            JsonParser.Event event = parser.next();
            switch (event) {
                //case START_ARRAY:
                //case END_ARRAY:
                //case START_OBJECT:
                //case END_OBJECT:
                case VALUE_FALSE:
                //case VALUE_NULL:
                case VALUE_TRUE:
                    System.out.println(event.toString());
                    break;
                case KEY_NAME:
                    System.out.print(event.toString() + " " + parser.getString() + ": ");
                    break;
                case VALUE_STRING:
                case VALUE_NUMBER:
                    System.out.println(event.toString() + " " + parser.getString());
                    break;
            }
        }
    }

}
