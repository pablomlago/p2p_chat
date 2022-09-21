package Encriptacion;

import java.security.MessageDigest;

public class HashCryptography {

    public static String computeHash(String message) throws Exception {
        return new String(MessageDigest.getInstance("SHA-256").digest(message.getBytes()));
    }
}
