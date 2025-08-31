package com.emmanuel.chancita.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
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
}