package model.io;

import com.google.gson.*;

import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * This class contains method to read a json file with simple configurations.
 * <p>
 * The root element of json as to be a json object.
 * <p>
 * For example:
 * <p>
 * <pre>
 *  {
 *      "name" = "Mutation",
 *      "values" = [0,1,2,3,4,5],
 *      "matrix" = [[0,1,2,3],[4,5,6,7]]
 *  }
 * </pre>
 * <p>
 * For more advanced parse of json file use Gson.
 */
public class JsonSimpleReader {
    private JsonObject jsonObject;

    private JsonSimpleReader(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    /**
     * Read the json file.
     * @param filename the file path.
     * @return a instance of this object.
     * @throws JsonIOException If you couldn't read an input stream or write in one.
     * @throws JsonSyntaxException if the file you are trying to read (or write) is a malformed JSON element.
     * @throws FileNotFoundException if the named file does not exist, is a directory rather than a regular file,
     * or for some other reason cannot be opened for reading.
     */
    public static JsonSimpleReader read(String filename) throws FileNotFoundException {
        JsonElement jsonElement = JsonParser.parseReader(new FileReader(filename));
        return new JsonSimpleReader(jsonElement.getAsJsonObject());
    }

    /**
     * Read the json from the string passed has argument.
     *
     * @param jsonString a json string object.
     * @return this reader.
     */
    public static JsonSimpleReader readJsonString(String jsonString){
        JsonElement jsonElement = JsonParser.parseString(jsonString);
        return new JsonSimpleReader(jsonElement.getAsJsonObject());
    }

    /**
     * Get as int the element that use the specific key.
     * @param key the key to search in the json.
     * @return an integer.
     */
    public int getInt(String key) {
        JsonElement element = this.jsonObject.get(key);
        return element.getAsInt();
    }

    /**
     * Get as double the element that use the specific key.
     * @param key the key to search in the json.
     * @return a double.
     */
    public double getDouble(String key) {
        JsonElement element = this.jsonObject.get(key);
        return element.getAsDouble();
    }

    /**
     * Get as boolean the element that use the specific key.
     * @param key the key to search in the json.
     * @return a boolean.
     */
    public boolean getBoolean(String key) {
        JsonElement element = this.jsonObject.get(key);
        return element.getAsBoolean();
    }

    /**
     * Get as int[] the element that use the specific key.
     * @param key the key to search in the json.
     * @return a array of integers.
     */
    public int[] getIntegerArray(String key) {
        JsonElement element = this.jsonObject.get(key);
        JsonArray array = element.getAsJsonArray();
        int[]  ints = new int[array.size()];
        int i = 0;
        for (JsonElement jsonElement : array) {
            ints[i++] = jsonElement.getAsInt();
        }
        return ints;
    }

    /**
     * Get as double[] the element that use the specific key.
     * @param key the key to search in the json.
     * @return a array of doubles.
     */
    public double[] getDoubleArray(String key) {
        JsonElement element = this.jsonObject.get(key);
        JsonArray array = element.getAsJsonArray();
        double[]  doubles = new double[array.size()];
        int i = 0;
        for (JsonElement jsonElement : array) {
            doubles[i++] = jsonElement.getAsDouble();
        }
        return doubles;
    }

    /**
     * Get as int[][] the element that use the specific key.
     * @param key the key to search in the json.
     * @return a matrix of integer elements.
     */
    public int[][] getIntegerMatrix(String key) {
        JsonElement element = this.jsonObject.get(key);
        JsonArray array = element.getAsJsonArray();
        int[][]  ints = new int[array.size()][];
        int i = 0;
        for (JsonElement jsonElement : array) {
            JsonArray supposedArray = jsonElement.getAsJsonArray();
            int[] arrayList = new int[supposedArray.size()];
            int j = 0;
            for (JsonElement supposedInt : supposedArray) {
                arrayList[j++] = supposedInt.getAsInt();
            }
            ints[i++] = arrayList;
        }
        return ints;
    }

    /**
     * Get as double[][] the element that use the specific key.
     * @param key the key to search in the json.
     * @return a matrix of double elements.
     */
    public double[][] getDoublerMatrix(String key) {
        JsonElement element = this.jsonObject.get(key);
        JsonArray array = element.getAsJsonArray();
        double[][]  doubles = new double[array.size()][];
        int i = 0;
        for (JsonElement jsonElement : array) {
            JsonArray supposedArray = jsonElement.getAsJsonArray();
            double[] arrayList = new double[supposedArray.size()];
            int j = 0;
            for (JsonElement supposedDouble : supposedArray) {
                arrayList[j++] = supposedDouble.getAsDouble();
            }
            doubles[i++] = arrayList;		}
        return doubles;
    }

    /**
     * Get as string the element that use the specific key.
     * @param key the key to search in the json.
     * @return a string or null if the key don't exist
     */
    public String getString(String key) {
        JsonElement element = this.jsonObject.get(key);
        return element.getAsString();
    }

    /**
     * Get as a string the json readed.
     * @return a json string.
     */
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create(); // pretty print
        String prettyJson = gson.toJson(jsonObject);
        return prettyJson;
    }
}
