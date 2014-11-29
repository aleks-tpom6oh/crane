/**
 * Created on 29.11.14
 * @author alexey@plainvanillagames.com
 */
package com.tpom6oh.crane;

import se.simbio.encryption.Encryption;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptUtils
{
    public String hash(String text)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(text.getBytes("iso-8859-1"), 0, text.length());
            byte[] sha1hash = md.digest();
            return convertToHex(sha1hash);
        }
        catch (UnsupportedEncodingException e)
        {
            return md5(text);
        }
        catch (NoSuchAlgorithmException e)
        {
            return md5(text);
        }
    }

    public static String md5(String input)
    {
        String md5 = null;

        try {

            //Create MessageDigest object for MD5
            MessageDigest digest = MessageDigest.getInstance("MD5");

            //Update input string in message digest
            digest.update(input.getBytes(), 0, input.length());

            //Converts message digest value in base 16 (hex)
            md5 = new BigInteger(1, digest.digest()).toString(16);

        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();
        }
        return md5;
    }

    public String encrypt(String key, String text)
    {
        return new Encryption().encrypt(key, text);
    }

    public String decrypt(String key, String encrypted)
    {
        return new Encryption().decrypt(key, encrypted);
    }

    private String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfByte = (b >>> 4) & 0x0F;
            int twoHalf = 0;
            do {
                buf.append((0 <= halfByte) && (halfByte <= 9) ? (char) ('0' + halfByte) : (char) ('a' + (halfByte - 10)));
                halfByte = b & 0x0F;
            } while (twoHalf++ < 1);
        }
        return buf.toString();
    }
}
