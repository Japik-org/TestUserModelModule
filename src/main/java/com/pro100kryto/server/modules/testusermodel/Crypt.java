package com.pro100kryto.server.modules.testusermodel;

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

}
