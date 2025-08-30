package com.emmanuel.chancita.utils;

import java.time.LocalDate;
import java.time.Period;

public class Edad {
    public static int calcularEdad(LocalDate fechaNacimiento) {
        LocalDate fechaActual = LocalDate.now();

        Period periodo = Period.between(fechaNacimiento, fechaActual);

        return periodo.getYears();
    }
}
