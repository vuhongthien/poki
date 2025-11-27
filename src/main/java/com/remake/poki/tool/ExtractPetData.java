package com.remake.poki.tool;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
public class ExtractPetData {
    public static void main(String[] args) {


        String inputDirPath = "C:\\Users\\ASUS\\Downloads\\sprites\\sprites\\DefineSprite_518";
        String outputFilePath = "C:\\swf";

        try {
            mergeSprites(inputDirPath, outputFilePath);
            System.out.println("Ghép sprite thành công vào: " + outputFilePath);
        } catch (IOException e) {
            System.err.println("Lỗi khi xử lý ảnh: " + e.getMessage());
        }
    }

    public static void mergeSprites(String inputDirPath, String outputFilePath) throws IOException {
        File inputDir = new File(inputDirPath);
        if (!inputDir.exists() || !inputDir.isDirectory()) {
            throw new IllegalArgumentException("Thư mục đầu vào không hợp lệ: " + inputDirPath);
        }

        // Lấy danh sách các file .png
        File[] imageFiles = inputDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
        if (imageFiles == null || imageFiles.length == 0) {
            throw new IllegalArgumentException("Không tìm thấy ảnh PNG trong thư mục: " + inputDirPath);
        }

        // Sắp xếp tên file để đảm bảo thứ tự
        Arrays.sort(imageFiles);

        // Giả định tất cả ảnh có cùng kích thước
        BufferedImage firstImage = ImageIO.read(imageFiles[0]);
        int singleWidth = firstImage.getWidth();
        int singleHeight = firstImage.getHeight();

        int columns = imageFiles.length;
        int totalWidth = singleWidth * columns;
        int totalHeight = singleHeight;

        // Tạo ảnh sprite tổng
        BufferedImage spriteSheet = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics g = spriteSheet.getGraphics();

        for (int i = 0; i < imageFiles.length; i++) {
            BufferedImage img = ImageIO.read(imageFiles[i]);
            g.drawImage(img, i * singleWidth, 0, null);
        }

        g.dispose();

        // Xuất ra file ảnh cuối cùng
        File outputFile = new File(outputFilePath);
        ImageIO.write(spriteSheet, "PNG", outputFile);
    }
}
