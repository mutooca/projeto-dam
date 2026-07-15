package ao.uan.fc.dam.mobile.data.converter;

import androidx.room.TypeConverter;

import java.time.LocalDateTime;

public class LocalDateTimeConverter {

    @TypeConverter
    public static LocalDateTime fromString(String value){

        if(value==null)
            return null;

        return LocalDateTime.parse(value);

    }

    @TypeConverter
    public static String fromLocalDateTime(LocalDateTime date){

        if(date==null)
            return null;

        return date.toString();

    }

}
