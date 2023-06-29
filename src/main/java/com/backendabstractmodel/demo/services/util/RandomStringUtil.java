package com.backendabstractmodel.demo.services.util;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomStringUtil {

    private RandomStringUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String generate(int length) {
        return RandomStringUtils.random(length, true, true);
    }

}
