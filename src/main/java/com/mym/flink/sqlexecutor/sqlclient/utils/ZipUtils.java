package com.mym.flink.sqlexecutor.sqlclient.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;


@Slf4j
public class ZipUtils {

    public static void unzip(String zipFile, String dir) {
        try (ZipFile zip = new ZipFile(zipFile)) {
            Enumeration<ZipArchiveEntry> entries = zip.getEntries();
            while (entries.hasMoreElements()) {
                ZipArchiveEntry zipArchiveEntry = entries.nextElement();
                File file = new File(dir, zipArchiveEntry.getName());
                writeFile(file, zip.getInputStream(zipArchiveEntry));
                log.info("======解压成功=======,file:{}", file.getAbsolutePath());
            }
        } catch (IOException e) {
            log.error("压缩包处理异常，异常信息:", e);
        }
    }

    private static void writeFile(File file, InputStream inputStream) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        if (file.isDirectory()) {
            return;
        }
        try (OutputStream outputStream = new FileOutputStream(file)) {
            byte[] bytes = new byte[4096];
            int len;
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        // unzip("/Users/zackyoung/Downloads/85.zip", "/Users/zackyoung/Downloads/85");

        Files.move(
                Paths.get("/Users/zackyoung/Downloads/85/jar/"),
                Paths.get("/Users/zackyoung/Downloads/85/123/"));
    }
}
