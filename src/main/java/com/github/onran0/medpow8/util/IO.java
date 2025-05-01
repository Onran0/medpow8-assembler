package com.github.onran0.medpow8.util;

import java.io.*;
import java.nio.charset.StandardCharsets;

public final class IO {

    public static String readStream(InputStream in) throws IOException{
        return readFromReader(new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)));
    }

    public static String readFromReader(Reader in) throws IOException{
        StringBuilder builder = new StringBuilder();

        int len;

        while((len = in.read()) != -1)
            builder.append((char)len);

        return builder.toString();
    }
}