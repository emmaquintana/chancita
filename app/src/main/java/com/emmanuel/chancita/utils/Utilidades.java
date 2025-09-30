package com.emmanuel.chancita.utils;

import android.util.Log;

import androidx.room.TypeConverter;

import com.emmanuel.chancita.data.model.MetodoEleccionGanador;
import com.emmanuel.chancita.data.model.NumeroComprado;
import com.emmanuel.chancita.data.model.Rifa;
import com.emmanuel.chancita.data.model.RifaEntityRoom;
import com.emmanuel.chancita.data.model.RifaEstado;
import com.emmanuel.chancita.data.model.RifaPremio;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Utilidades {

    public static String capitalizar(String str) {
        str = str.toLowerCase();
        char primerCaracter = Character.toUpperCase(str.charAt(0));
        str = primerCaracter + str.substring(1);
        return str;
    }

    public static String formatearFechaHora(LocalDateTime fecha, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, new Locale("es", "ES"));
        return fecha.format(formatter);
    }

    public static int calcularEdad(LocalDate fechaNacimiento) {
        LocalDate fechaActual = LocalDate.now();

        Period periodo = Period.between(fechaNacimiento, fechaActual);

        return periodo.getYears();
    }

    /** Genera un código de 'x' letras y 'y' números
     *
     * @param cantLetras La cantidad de letras que ha de tener el código
     * @param cantNumeros La cantidad de números que ha de tener el código
     * @return un código con 'x' letras y 'y' números con el siguiente formato (suponiendo 4 letras y 3 números): "xxxx000"
     *
     **/
    public static String generarCodigo(int cantLetras, int cantNumeros) {
        final String LETRAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String NUMEROS = "0123456789";
        final SecureRandom random = new SecureRandom();
        StringBuilder codigo = new StringBuilder();

        // Generar 3 letras aleatorias
        for (int i = 0; i < cantLetras; i++) {
            int index = random.nextInt(LETRAS.length());
            codigo.append(LETRAS.charAt(index));
        }

        // Generar 3 números aleatorios
        for (int i = 0; i < cantNumeros; i++) {
            int index = random.nextInt(NUMEROS.length());
            codigo.append(NUMEROS.charAt(index));
        }

        return codigo.toString();
    }

    public static <T> String listaAString(List<T> lista) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < lista.size(); i++) {
            if (!(i == lista.size() - 1)) {
                sb.append(lista.get(i) + ", ");
            }
            else {
                sb.append(lista.get(i));
            }
        }

        return sb.toString();
    }

    public static LocalDateTime parsearFechaHora(String fechaHoraStr, String patron) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(patron);
        return LocalDateTime.parse(fechaHoraStr, formatter);
    }

    public static class Converters {
        private static final Gson gson = new Gson();

        @TypeConverter
        public static String fromList(List<?> list) {
            return list == null ? null : gson.toJson(list);
        }

        @TypeConverter
        public static String fromStringList(List<String> list) {
            return list == null ? null : gson.toJson(list);
        }

        @TypeConverter
        public static List<String> toStringList(String json) {
            if (json == null) return new ArrayList<>();
            Type type = new TypeToken<List<String>>() {}.getType();
            return gson.fromJson(json, type);
        }

        @TypeConverter
        public static String fromLocalDateTime(LocalDateTime dateTime) {
            return dateTime == null ? null : dateTime.toString();
        }

        // Para List<RifaPremio>
        @TypeConverter
        public static String fromRifaPremioList(List<RifaPremio> premios) {
            return gson.toJson(premios);
        }

        @TypeConverter
        public static List<RifaPremio> toRifaPremioList(String json) {
            if (json == null) return new ArrayList<>();
            Type type = new TypeToken<List<RifaPremio>>(){}.getType();
            return gson.fromJson(json, type);
        }

        // Para List<NumeroComprado>
        @TypeConverter
        public static String fromNumeroCompradoList(List<NumeroComprado> numeros) {
            return gson.toJson(numeros);
        }

        @TypeConverter
        public static List<NumeroComprado> toNumeroCompradoList(String json) {
            if (json == null) return new ArrayList<>();
            Type type = new TypeToken<List<NumeroComprado>>(){}.getType();
            return gson.fromJson(json, type);
        }

        @TypeConverter
        public static LocalDateTime toLocalDateTime(String value) {
            return value == null ? null : LocalDateTime.parse(value);
        }

        public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        public static RifaEntityRoom toEntity(Rifa rifa) {
            RifaEntityRoom entity = new RifaEntityRoom();
            Log.d("TOENTITY", "Rifa con ESTADO " + rifa.getEstado());
            entity.setId(rifa.getId());
            entity.setTitulo(rifa.getTitulo());
            entity.setEstado(rifa.getEstado().name());
            entity.setCreadoEn(rifa.getCreadoEn() != null ? rifa.getCreadoEn().format(formatter) : null);
            entity.setCodigo(rifa.getCodigo());
            entity.setFechaSorteo(rifa.getFechaSorteo() != null ? rifa.getFechaSorteo().format(formatter) : null);

            return entity;
        }

        public static List<RifaEntityRoom> toEntityList(List<Rifa> rifas) {
            List<RifaEntityRoom> list = new ArrayList<>();
            for (Rifa r : rifas) {
                list.add(toEntity(r));
            }
            return list;
        }

        public static List<Rifa> toRifa(List<RifaEntityRoom> entities) {
            List<Rifa> list = new ArrayList<>();
            for (RifaEntityRoom e : entities) {
                list.add(toRifa(e));
            }
            return list;
        }

        public static Rifa toRifa(RifaEntityRoom e) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

            Rifa rifa = new Rifa();
            rifa.setId(e.getId());
            rifa.setTitulo(e.getTitulo());
            rifa.setEstado(RifaEstado.valueOf(e.getEstado()));
            rifa.setCreadoEn(e.getCreadoEn() != null ? LocalDateTime.parse(e.getCreadoEn(), formatter) : null);
            rifa.setCodigo(e.getCodigo());
            rifa.setFechaSorteo(e.getFechaSorteo() != null ? LocalDateTime.parse(e.getFechaSorteo(), formatter) : null);

            return rifa;
        }
    }

}