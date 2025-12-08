package com.remake.poki.tool;

import java.io.*;
import java.net.*;
import java.nio.file.*;

public class BatchDownloader {

    private static final int BUFFER = 8192;
    private static final int TIMEOUT = 10000;

    public static void main(String[] args) {

        String folder = "C:\\Users\\ASUS\\Downloads\\out";
        Path outputDir = Paths.get(folder);

        try {
            Files.createDirectories(outputDir);
        } catch (IOException e) {
            System.err.println("Cannot create output folder");
            return;
        }

        for (int petId = 1; petId <= 5000; petId++) {
            String url = "https://aola.100bt.com/h5/peticon/large/peticon" + petId + ".png";
            Path savePath = outputDir.resolve(petId + ".png");

            try {
                if (download(url, savePath)) {
                    System.out.println("Downloaded: " + petId + ".png");
                } else {
                    System.out.println("Skip (not found): " + petId);
                }
            } catch (Exception e) {
                System.out.println("Error at ID " + petId + ": " + e.getMessage());
            }
        }

        System.out.println("Done!");
    }

    private static boolean download(String urlStr, Path savePath) throws IOException {

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(TIMEOUT);
        conn.setReadTimeout(TIMEOUT);
        conn.setInstanceFollowRedirects(true);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");

        int status = conn.getResponseCode();

        if (status == 404) {
            conn.disconnect();
            return false; // skip
        }

        if (status >= 400) {
            conn.disconnect();
            return false;
        }

        try (InputStream in = conn.getInputStream();
             OutputStream out = Files.newOutputStream(savePath)) {

            byte[] buffer = new byte[BUFFER];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        }

        conn.disconnect();
        return true;
    }
}
