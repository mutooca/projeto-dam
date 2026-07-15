package ao.uan.fc.dam.mobile.network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;

public class JsonConverter {

    private static final Gson gson = new Gson();

    public static String toJson(Object object){
        return gson.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> classe){
        return gson.fromJson(json, classe);
    }

    public static Map<String, String> toMap(String json) {
        Type tipo = new TypeToken<Map<String, String>>() {}.getType();
        return gson.fromJson(json, tipo);
    }

}
