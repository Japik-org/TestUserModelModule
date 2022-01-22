package com.pro100kryto.server.modules.testusermodel;

import com.google.common.hash.Hashing;

import java.util.Arrays;
import java.util.Random;

public final class Crypt {
    private static final Random random = new Random();

    public static byte[] mergeBytes(byte[] a, byte[] b){
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static byte[] newRandomSalt(int len){
        final byte[] bytes = new byte[len];
        random.nextBytes(bytes);
        return bytes;
    }

    public static byte[] hashPass(byte[] pass, byte[] salt){
        return Hashing.sha512().hashBytes(
                mergeBytes(pass, salt)
        ).asBytes();
    }

    public static byte[] composeSalt(byte[] saltA, byte[] saltB){
        return Hashing.sha256().hashBytes(
                mergeBytes(saltA, saltB)
        ).asBytes();
    }

    public static boolean checkUserSign(byte[] sign, byte[] secret, byte[] salt){
        return Arrays.equals(
                Hashing.sha256().hashBytes(
                    composeSalt(
                            secret, salt
                    )
        ).asBytes(), sign);
    }

    public static byte[] createUserPass(byte[] pass, byte[] userSalt, byte[] localSalt){
        return Hashing.sha256().hashBytes(
                        mergeBytes(
                                pass,
                                composeSalt(
                                        userSalt,
                                        localSalt
                                )
                        )
        ).asBytes();
    }
}
