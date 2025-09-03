package com.emmanuel.chancita.utils;

import java.security.SecureRandom;
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
}