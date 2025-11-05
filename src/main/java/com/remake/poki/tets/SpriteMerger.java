package com.remake.poki.tets;

/**
 * @author : ASUS ------- ^^!
 * @created : 27/05/2025,
 **/
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpriteMerger {

    public static void main(String[] args) {
        String inputDirPath = "C:\\Users\\ASUS\\Downloads\\sprites\\DefineSprite_477";
        String outputDirPath = "C:\\Users\\ASUS\\Desktop\\game\\New folder";

        try {
            String outputFilePath = mergeSpritesAndExport(inputDirPath, outputDirPath);
            System.out.println("✅ Đã xuất sprite sheet tới: " + outputFilePath);
        } catch (IOException e) {
            System.err.println("❌ Lỗi khi xử lý ảnh: " + e.getMessage());
        }
    }

    public static String mergeSpritesAndExport(String inputDirPath, String outputDirPath) throws IOException {
        File inputDir = new File(inputDirPath);
        if (!inputDir.exists() || !inputDir.isDirectory()) {
            throw new IllegalArgumentException("Thư mục đầu vào không hợp lệ: " + inputDirPath);
        }

        File[] imageFiles = inputDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
        if (imageFiles == null || imageFiles.length == 0) {
            throw new IllegalArgumentException("Không tìm thấy ảnh PNG trong thư mục: " + inputDirPath);
        }

        // Sắp xếp theo số thứ tự trong tên file
        Arrays.sort(imageFiles, Comparator.comparingInt(SpriteMerger::extractNumber));

        BufferedImage firstImage = ImageIO.read(imageFiles[0]);
        int singleWidth = firstImage.getWidth();
        int singleHeight = firstImage.getHeight();

        int totalWidth = singleWidth * imageFiles.length;
        int totalHeight = singleHeight;

        BufferedImage spriteSheet = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics g = spriteSheet.getGraphics();

        for (int i = 0; i < imageFiles.length; i++) {
            BufferedImage img = ImageIO.read(imageFiles[i]);
            g.drawImage(img, i * singleWidth, 0, null);
        }

        g.dispose();

        // Tên file là kích thước từng tấm ảnh (VD: 64x64.png)
        String outputFileName = singleWidth + "x" + singleHeight + ".png";
        File outputDir = new File(outputDirPath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        File outputFile = new File(outputDir, outputFileName);
        ImageIO.write(spriteSheet, "PNG", outputFile);

        return outputFile.getAbsolutePath();
    }

    // Trích số từ tên file để sắp xếp (VD: 12 từ "sprite12.png")
    private static int extractNumber(File file) {
        String name = file.getName();
        Matcher matcher = Pattern.compile("(\\d+)").matcher(name);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            return Integer.MAX_VALUE; // Không có số thì đẩy về sau cùng
        }
    }
}

