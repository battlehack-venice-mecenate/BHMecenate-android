package com.battlehack_venice.lib.utils;

import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MD5Util
{
    /**
     * Generates and returns the MD5 hash of a string.
     *
     * @param string
     * @return MD5 hash string or null on error
     */
    public static String generateHash(String string)
    {
        if (string == null) {
            return null;
        }

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(string.getBytes());
            return MD5Util._toHex(md5.digest());
        } catch (NoSuchAlgorithmException exception) {
            LoggerFactory.getLogger(MD5Util.class).error("Unable to generate MD5 hash: " + exception.getMessage(), exception);
            return null;
        }
    }

    private static String _toHex(byte[] bytes)
    {
        StringBuilder string = new StringBuilder(bytes.length * 2);

        for (byte b : bytes) {
            string.append(Character.forDigit((b & 0xf0) >> 4, 16));
            string.append(Character.forDigit(b & 0x0f, 16));
        }

        return string.toString();
    }
}
