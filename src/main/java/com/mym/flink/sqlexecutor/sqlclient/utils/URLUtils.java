package com.mym.flink.sqlexecutor.sqlclient.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class URLUtils {

    /**
     * 获得URL，常用于使用绝对路径时的情况
     *
     * @param files URL对应的文件对象
     * @return URL
     */
    public static URL[] getURLs(File... files) {
        final URL[] urls = new URL[files.length];
        try {
            for (int i = 0; i < files.length; i++) {
                urls[i] = files[i].toURI().toURL();
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        return urls;
    }

    /**
     * 获得URL，常用于使用绝对路径时的情况
     *
     * @param files URL对应的文件对象
     * @return URL
     */
    public static URL[] getURLs(Set<File> files) {
        return getURLs(files.stream().filter(File::exists).toArray(File[]::new));
    }

    public static String toString(URL[] urls) {
        return Arrays.stream(urls).map(URL::toString).collect(Collectors.joining(","));
    }
}
