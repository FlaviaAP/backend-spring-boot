package com.backendabstractmodel.demo.services.util;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UUIDUtil {

    private UUIDUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static UUID bytesToUUID(byte[] bytes) {
        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            long high = byteBuffer.getLong();
            long low = byteBuffer.getLong();
            return new UUID(high, low);
        }
        catch (NullPointerException e) {
            return null;
        }
    }
}
