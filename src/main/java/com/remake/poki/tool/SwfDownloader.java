package com.remake.poki.tool;

import java.io.InputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class SwfDownloader {

    private static final String BASE_URL =
            "https://aola.100bt.com/play/catalog/costume%d.swf";
    private static final String BASE_URL2 =
            "https://aola.100bt.com/play/catalog/clothes%d.swf";

    public static void main(String[] args) {
        // Thư mục lưu file
        Path downloadDir = Paths.get("C:\\Users\\ASUS\\Desktop\\clothes");

        try {
            Files.createDirectories(downloadDir);
        } catch (IOException e) {
            System.err.println("Không tạo được thư mục download: " + e.getMessage());
            return;
        }

        for (int i = 1; i <= 400; i++) {
            String fileUrl = String.format(BASE_URL2, i);
            String fileName = String.format("costume%d.swf", i);
            Path outputFile = downloadDir.resolve(fileName);

            System.out.println("Đang tải: " + fileUrl);

            try {
                downloadFile(fileUrl, outputFile);
                System.out.println("  → OK: " + outputFile);
            } catch (FileNotFoundException404 e404) {
                // 404 thì skip, không in stacktrace dài
                System.out.println("  → Không tồn tại (404), skip.");
            } catch (IOException e) {
                System.out.println("  → Lỗi khác, skip: " + e.getMessage());
            }
        }

        System.out.println("Hoàn thành.");
    }

    private static void downloadFile(String fileUrl, Path outputFile)
            throws IOException, FileNotFoundException404 {

        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // Giả làm browser một chút cho chắc
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.setConnectTimeout(10_000);
        connection.setReadTimeout(30_000);

        int status = connection.getResponseCode();
        if (status == HttpURLConnection.HTTP_NOT_FOUND) {
            // Tự định nghĩa exception 404 để xử lý riêng
            throw new FileNotFoundException404("File not found: " + fileUrl);
        }
        if (status != HttpURLConnection.HTTP_OK) {
            throw new IOException("HTTP status " + status + " khi tải " + fileUrl);
        }

        try (InputStream in = connection.getInputStream()) {
            Files.copy(in, outputFile, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            connection.disconnect();
        }
    }

    // Exception riêng cho 404
    private static class FileNotFoundException404 extends Exception {
        public FileNotFoundException404(String message) {
            super(message);
        }
    }
}
