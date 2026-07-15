package ao.uan.fc.dam.mobile.data.converter;

import androidx.room.TypeConverter;

import ao.uan.fc.dam.mobile.data.enums.Categoria;
import ao.uan.fc.dam.mobile.data.enums.EstadoAnuncio;
import ao.uan.fc.dam.mobile.data.enums.ModoEntrega;
import ao.uan.fc.dam.mobile.data.enums.TipoCoordenada;
import ao.uan.fc.dam.mobile.data.enums.Visibilidade;

public class EnumConverter {

    // TipoCoordenada

    @TypeConverter
    public static TipoCoordenada toTipoCoordenada(String value){
        return value == null ? null : TipoCoordenada.valueOf(value);
    }

    @TypeConverter
    public static String fromTipoCoordenada(TipoCoordenada value){
        return value == null ? null : value.name();
    }


    //Visibilidade
    @TypeConverter
    public static Visibilidade toVisibilidade(String value){
        return value == null ? null : Visibilidade.valueOf(value);
    }

    @TypeConverter
    public static String fromVisibilidade(Visibilidade value){
        return value == null ? null : value.name();
    }

    // Estado
    @TypeConverter
    public static EstadoAnuncio toEstado(String value){
        return value == null ? null : EstadoAnuncio.valueOf(value);
    }

    @TypeConverter
    public static String fromEstado(EstadoAnuncio value){
        return value == null ? null : value.name();
    }

    // Entrega

    @TypeConverter
    public static ModoEntrega toEntrega(String value){
        return value == null ? null : ModoEntrega.valueOf(value);
    }

    @TypeConverter
    public static String fromEntrega(ModoEntrega value){
        return value == null ? null : value.name();
    }

}