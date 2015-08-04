package com.javaee.crumbsOfAPI.json;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import static javax.json.JsonValue.FALSE;
import static javax.json.JsonValue.NULL;
import static javax.json.JsonValue.TRUE;
import static javax.json.JsonValue.ValueType.ARRAY;
import static javax.json.JsonValue.ValueType.NUMBER;
import static javax.json.JsonValue.ValueType.OBJECT;
import static javax.json.JsonValue.ValueType.STRING;
import javax.json.JsonWriter;
import javax.json.stream.JsonParsingException;

/**
 * Helper class for using JSON-P Object Model API.
 *
 * @author Alin Constantin
 */
public class ObjectModelHelper {

    /**
     * Parse and prints JsonObject to console.
     *
     * @param object JsonObject to be printed to console
     */
    public static void parseJsonObject(JsonObject object) {

        for (String name : object.keySet()) {
            parseJsonObject(object.get(name), name);
        }
    }

    /**
     * Navigating an Object Model.
     *
     * @param obj JsonValue
     * @param key String
     */
    private static void parseJsonObject(JsonValue obj, String key) {

        if (key != null) {
            System.out.print("KEY_NAME " + key + ": ");
        }
        switch (obj.getValueType()) {
            case OBJECT:
                System.out.println("OBJECT");
                JsonObject jsonObject = (JsonObject) obj;
                for (String value : jsonObject.keySet()) {
                    parseJsonObject(jsonObject.get(value), value);
                }
                break;
            case ARRAY:
                System.out.println("VALUE_ARRAY");
                JsonArray jsonArray = (JsonArray) obj;
                for (JsonValue value : jsonArray) {
                    parseJsonObject(value, null);
                }
                break;
            case STRING:
                JsonString jsonString = (JsonString) obj;
                System.out.println("VALUE_STRING " + jsonString.getString());
                break;
            case NUMBER:
                JsonNumber jsonNumber = (JsonNumber) obj;
                System.out.println("VALUE_NUMBER " + jsonNumber.toString());
                break;
            default:
                System.out.println(obj.getValueType().toString());
                break;
        }
    }

    /**
     * Encode JsonObject to String.
     *
     * @param jsonObject JsonObject to be encoded
     * @return String representation of JsonObject
     */
    public static String encodeJsonObjectToString(JsonObject jsonObject) {

        StringWriter stringWriter = new StringWriter();
        try {
            JsonWriter jsonWriter = Json.createWriter(stringWriter);
            jsonWriter.writeObject(jsonObject);

            return stringWriter.toString();
        } catch (JsonException e) {
            throw e;
        }
    }

    /**
     * Encode String to JsonObject.
     *
     * @param string String representation of JsonObject
     * @return JsonObject obtained from String
     */
    public static JsonObject encodeStringToJsonObject(String string) {

        try {
            JsonReader reader = Json.createReader(new StringReader(string));
            return reader.readObject();
        } catch (JsonParsingException e) {
            throw e;
        } catch (JsonException e) {
            throw e;
        }
    }

    /**
     * Create JsonObject from text file.
     *
     * @param filePath Path to file
     * @return JsonObject created from file content
     * @throws java.io.FileNotFoundException
     */
    public static JsonObject createJsonObjectFromFile(Path filePath) throws FileNotFoundException {

        JsonObject jsonObject = null;
        try {
            JsonReader reader = Json.createReader(new FileInputStream(filePath.toFile()));
            jsonObject = reader.readObject();
        } catch (FileNotFoundException e) {
            throw e;
        } catch (JsonParsingException e) {
            throw e;
        } catch (JsonException e) {
            throw e;
        }
        return jsonObject;
    }

    /**
     * Write JsonObject to text file.
     *
     * @param filePath Location where the JSON file will be saved
     * @param jsonObject JsonObject to be written to file
     * @throws java.io.IOException
     */
    public static void writeJsonObjectToFile(Path filePath, JsonObject jsonObject) throws IOException {

        try {
            if (!Files.exists(filePath, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
                Files.createFile(filePath);
            }
            try (JsonWriter writer = Json.createWriter(new FileOutputStream(filePath.toFile()))) {
                writer.writeObject(jsonObject);
            } catch (JsonException e) {
                throw e;
            }
        } catch (IOException | SecurityException e) {
            throw e;
        }
    }

    /**
     * Encode JsonObject to Map<String, Object>.
     *
     * @param jsonObject JsonObject to be encoded
     * @return Map<String, Object> containing JsonObject values
     */
    public static Map<String, Object> encodeJsonObjectToMap(JsonObject jsonObject) {

        Map<String, Object> map = new HashMap<>();
        for (String name : jsonObject.keySet()) {
            encodeJsonObjectToMap(jsonObject.get(name), name, map);
        }
        return map;
    }

    /**
     * Add values to Map<String, Object>.
     *
     * @param obj Value found in JsonObject
     * @param key Map key
     * @param map Map<String, Object>
     */
    private static void encodeJsonObjectToMap(JsonValue obj, String key, Map<String, Object> map) {

        switch (obj.getValueType()) {
            case OBJECT:
                JsonObject jsonObject = (JsonObject) obj;
                Map<String, Object> jsonObjectToMap = new HashMap<>();
                for (String value : jsonObject.keySet()) {
                    encodeJsonObjectToMap(jsonObject.get(value), value, jsonObjectToMap);
                }
                map.put(key, jsonObjectToMap);
                break;
            case ARRAY:
                JsonArray jsonArray = (JsonArray) obj;
                List<Object> jsonArrayToList = new ArrayList<>();
                for (JsonValue value : jsonArray) {
                    encodeJsonObjectToList(value, jsonArrayToList);
                }
                map.put(key, jsonArrayToList);
                break;
            case STRING:
                JsonString stringValue = (JsonString) obj;
                map.put(key, stringValue);
                break;
            case NUMBER:
                JsonNumber numberValue = (JsonNumber) obj;
                map.put(key, numberValue);
                break;
            case TRUE:
                map.put(key, TRUE);
                break;
            case FALSE:
                map.put(key, FALSE);
                break;
            default:
                map.put(key, NULL);
                break;
        }
    }

    /**
     * Add values to List<Object>.
     *
     * @param obj Value found in JsonObject
     * @param list List<Object> containing JsonObject values
     */
    private static void encodeJsonObjectToList(JsonValue obj, List<Object> list) {

        switch (obj.getValueType()) {
            case OBJECT:
                JsonObject jsonObject = (JsonObject) obj;
                Map<String, Object> jsonObjectToMap = new HashMap<>();
                for (String value : jsonObject.keySet()) {
                    encodeJsonObjectToMap(jsonObject.get(value), value, jsonObjectToMap);
                }
                list.add(jsonObjectToMap);
                break;
            case ARRAY:
                JsonArray jsonArray = (JsonArray) obj;
                List<Object> jsonArrayToList = new ArrayList<>();
                for (JsonValue value : jsonArray) {
                    encodeJsonObjectToList(value, jsonArrayToList);
                }
                list.add(jsonArrayToList);
                break;
            case STRING:
                JsonString stringValue = (JsonString) obj;
                list.add(stringValue);
                break;
            case NUMBER:
                JsonNumber numberValue = (JsonNumber) obj;
                list.add(numberValue);
                break;
            case TRUE:
                list.add(TRUE);
                break;
            case FALSE:
                list.add(FALSE);
                break;
            default:
                list.add(NULL);
                break;
        }
    }

    /**
     * Encode Map<String, Object> to JsonObject.
     *
     * @param map Map to be encoded
     * @return JsonObject containing Map values
     */
    public static JsonObject encodeMapToJsonObject(Map<String, Object> map) {

        JsonObjectBuilder builder = Json.createObjectBuilder();
        for (Map.Entry<String, Object> entrySet : map.entrySet()) {
            encodeMapToJsonObject(entrySet.getKey(), entrySet.getValue(), builder);
        }
        return builder.build();
    }

    /**
     * Add Map values to JsonObject.
     *
     * @param key Map key
     * @param value Map value
     * @param builder JsonObjectBuilder
     */
    private static void encodeMapToJsonObject(String key, Object obj, JsonObjectBuilder builder) {

        if (obj instanceof Map) {

            @SuppressWarnings("unchecked")
            Map<String, Object> objectToMap = (Map<String, Object>) obj;

            JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
            for (Map.Entry<String, Object> entrySet : objectToMap.entrySet()) {
                encodeMapToJsonObject(entrySet.getKey(), entrySet.getValue(), jsonObjectBuilder);
            }
            builder.add(key, jsonObjectBuilder);

        } else if (obj instanceof List) {

            @SuppressWarnings("unchecked")
            List<Object> objectToList = (List<Object>) obj;

            JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
            for (Object value : objectToList) {
                if (value instanceof Map) {
                    mapToObjectBuilder(value, jsonArrayBuilder);
                } else if (value instanceof List) {
                    listToArrayBuilder(value, jsonArrayBuilder);
                } else {
                    jsonArrayBuilder.add((JsonValue) value);
                }
            }
            builder.add(key, jsonArrayBuilder);

        } else {
            builder.add(key, (JsonValue) obj);
        }
    }

    /**
     * Add Map values to JsonArrayBuilder.
     *
     * @param obj Object containing a Map
     * @param builder JsonArrayBuilder
     */
    private static void mapToObjectBuilder(Object obj, JsonArrayBuilder builder) {

        @SuppressWarnings("unchecked")
        Map<String, Object> objectToMap = (Map<String, Object>) obj;

        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        for (Map.Entry<String, Object> entrySet : objectToMap.entrySet()) {
            encodeMapToJsonObject(entrySet.getKey(), entrySet.getValue(), jsonObjectBuilder);
        }
        builder.add(jsonObjectBuilder);
    }

    /**
     * Add List values to JsonArrayBuilder.
     *
     * @param obj Object containing a List
     * @param builder JsonArrayBuilder
     */
    private static void listToArrayBuilder(Object obj, JsonArrayBuilder builder) {

        @SuppressWarnings("unchecked")
        List<Object> objectToList = (List<Object>) obj;

        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        for (Object value : objectToList) {
            if (value instanceof Map) {
                mapToObjectBuilder(value, jsonArrayBuilder);
            } else if (value instanceof List) {
                listToArrayBuilder(value, jsonArrayBuilder);
            } else {
                jsonArrayBuilder.add((JsonValue) value);
            }
        }
        builder.add(jsonArrayBuilder);
    }

    public ObjectModelHelper() {
    }

}
