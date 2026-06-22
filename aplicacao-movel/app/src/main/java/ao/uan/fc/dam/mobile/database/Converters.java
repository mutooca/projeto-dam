package ao.uan.fc.dam.mobile.database;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Converters {
    @TypeConverter
    public static LocalDateTime fromString(String value) {
        return value == null ? null : LocalDateTime.parse(value);
    }

    @TypeConverter
    public static String dateToString(LocalDateTime date) {
        return date == null ? null : date.toString();
    }

    @TypeConverter
    public static UUID fromUuidString(String value) {
        return value == null ? null : UUID.fromString(value);
    }

    @TypeConverter
    public static String uuidToString(UUID uuid) {
        return uuid == null ? null : uuid.toString();
    }

    @TypeConverter
    public static Map<String, String> fromMapString(String value) {
        return new Gson().fromJson(value, new TypeToken<Map<String, String>>() {}.getType());
    }

    @TypeConverter
    public static String fromMap(Map<String, String> map) {
        return new Gson().toJson(map);
    }

    @TypeConverter
    public static List<String> fromListString(String value) {
        return new Gson().fromJson(value, new TypeToken<List<String>>() {}.getType());
    }

    @TypeConverter
    public static String fromList(List<String> list) {
        return new Gson().toJson(list);
    }
}
