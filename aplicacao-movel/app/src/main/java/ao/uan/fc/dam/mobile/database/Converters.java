package ao.uan.fc.dam.mobile.database;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/* JADX INFO: loaded from: classes8.dex */
public class Converters {
    public static LocalDateTime fromTimestamp(String value) {
        if (value == null) {
            return null;
        }
        return LocalDateTime.parse(value);
    }

    public static String dateTimeToTimestamp(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return date.toString();
    }

    public static LocalDate fromDateTimestamp(String value) {
        if (value == null) {
            return null;
        }
        return LocalDate.parse(value);
    }

    public static String dateToTimestamp(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.toString();
    }

    public static UUID fromString(String value) {
        if (value == null) {
            return null;
        }
        return UUID.fromString(value);
    }

    public static String uuidToString(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return uuid.toString();
    }
}