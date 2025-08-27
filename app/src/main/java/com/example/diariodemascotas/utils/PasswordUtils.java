package com.example.diariodemascotas.utils;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import android.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtils {


    private PasswordUtils() {
    }

    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final int ITERATIONS = 100_000;
    private static final int KEY_LENGTH = 256;


    public static byte[] generarSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    public static byte[] hash(char[] password, byte[] salt) throws GeneralSecurityException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] hash = skf.generateSecret(spec).getEncoded();
        Arrays.fill(password, '\u0000');
        return hash;
    }

    public static boolean verify(char[] password, byte[] salt, byte[] expectedHas) throws GeneralSecurityException {
        byte[] candidato = hash(password, salt);
        boolean ok = MessageDigest.isEqual(candidato, expectedHas);
        Arrays.fill(candidato, (byte) 0);
        Arrays.fill(password, '\u0000');
        return ok;
    }

    public static String toBase64(byte[] data){
        return Base64.encodeToString(data,Base64.NO_WRAP);
    }

    public static byte[] fromoBase64(String s){
        return Base64.decode(s,Base64.NO_WRAP);
    }


}
