package com.backendabstractmodel.demo.services.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final String PT_BR_PATTERN = "dd/MM/yyyy";

    private DateUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String formatAsPtBR(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern(PT_BR_PATTERN));
    }

}
